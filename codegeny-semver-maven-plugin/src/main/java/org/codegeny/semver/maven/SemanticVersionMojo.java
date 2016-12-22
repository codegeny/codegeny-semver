package org.codegeny.semver.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.codegeny.semver.Change;
import org.codegeny.semver.Version;
import org.codegeny.semver.model.API;
import org.codegeny.semver.model.APIClassPath;
import org.codegeny.semver.rule.RuleEngine;
import org.codegeny.semver.rule.RuleEngineFactory;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.resolution.ArtifactRequest;

@Mojo(name = "version", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyCollection = ResolutionScope.COMPILE)
public class SemanticVersionMojo extends AbstractMojo {

	@Component
	private ArtifactResolver artifactResolver;

	@Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
	private ArtifactRepository localRepository;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	@Component
	private ProjectBuilder projectBuilder;

	@Component
	private RepositorySystem repositorySystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
	private RepositorySystemSession repositorySystemSession;

	@Parameter(defaultValue = "false")
	private boolean verbose;

	@Parameter(defaultValue = "false")
	private boolean skip;

	@Parameter(defaultValue = "semver.releaseVersion")
	private String releaseVersionName;

	@Parameter(defaultValue = "semver.snapshotVersion")
	private String snapshotVersionName;

	public void execute() throws MojoExecutionException {

		if (skip) {
			return;
		}

		Version currentVersion = Version.parseVersion(mavenProject.getVersion());
		Version previousVersion = currentVersion.previousVersion();

		getLog().info("About to check difference with " + previousVersion);

		Artifact previousArtifact = repositorySystem.createArtifact(mavenProject.getGroupId(), mavenProject.getArtifactId(), previousVersion.toString(), mavenProject.getPackaging());
		previousArtifact = localRepository.find(previousArtifact);
		File previousArtifactFile = previousArtifact.getFile();

		getLog().info("Checking existence of " + previousArtifact + " / " + previousArtifactFile);

		if (previousArtifactFile == null || !previousArtifactFile.exists()) {
			getLog().info("Previous version does not exist in local repository, downloading");
			try {
				ArtifactRequest request = new ArtifactRequest();
				request.setArtifact(new DefaultArtifact(previousArtifact.getGroupId(), previousArtifact.getArtifactId(), previousArtifact.getType(), previousArtifact.getVersion()));
				request.setRepositories(mavenProject.getRemoteProjectRepositories());
				artifactResolver.resolveArtifact(repositorySystemSession, request);
			} catch (Exception exception) {
				throw new MojoExecutionException("Cannot resolve previous version", exception);
			}
			previousArtifact = localRepository.find(previousArtifact);
			previousArtifactFile = previousArtifact.getFile();
		}

		getLog().info("Found " + previousArtifact.getVersion());
		getLog().info("Opening " + previousArtifact.getFile());

		try {
			
			Change change = compareJars(new File(mavenProject.getBuild().getOutputDirectory()), previousArtifactFile);
			
			Version releaseVersion = change.nextVersion(currentVersion);
			Version snapshotVersion = releaseVersion.nextPatchVersion();
			
			getLog().info("Release version " + releaseVersion);
			getLog().info("Snapshot version " + snapshotVersion + "-SNAPSHOT");
			
		} catch (Exception exception) {
			throw new MojoExecutionException("Cannot compare APIs", exception);
		} 
	}
	
	private Stream<String> recursive(File file) {
		if (file.isDirectory()) {
			return Stream.of(file.listFiles()).flatMap(this::recursive);
		} else {
			return Stream.of(file.getAbsolutePath()).filter(n -> n.endsWith(".class"));
		}
	}

	protected Change compareJars(File currentFolder, File previousJar) throws IOException, MojoExecutionException {
		
		// TODO expand class path to maven dependencies
		
		APIClassPath defaultClassPath = APIClassPath.classLoader(Thread.currentThread().getContextClassLoader());
		
		Set<String> currentClassNames = recursive(currentFolder)
				.map(n -> n.replace("/", "."))
				.map(n -> n.substring(0, n.length() - ".class".length()))
				.collect(Collectors.toSet());
		
		APIClassPath currentClassPath = resource -> {
			File f = new File(currentFolder, resource);
			return f.exists() ? new FileInputStream(f) : null;
		};
		
		try (JarFile previousJarFile = new JarFile(previousJar)) {
		
			API currentAPI = new API(currentClassPath.or(defaultClassPath));
			
			Set<String> previousClassNames = Collections.list(previousJarFile.entries()).stream()
				.map(e -> e.getName())
				.filter(n -> n.endsWith(".class"))
				.map(n -> n.replace("/", "."))
				.map(n -> n.substring(0, n.length() - ".class".length()))
				.collect(Collectors.toSet());
			
			APIClassPath previousClassPath = resource -> {
				JarEntry e = previousJarFile.getJarEntry(resource);
				return e == null ? null : previousJarFile.getInputStream(e);
			};
			
			API previousAPI = new API(previousClassPath.or(defaultClassPath));
			
			RuleEngineFactory factory = new RuleEngineFactory();
			RuleEngine engine = factory.createRuleEngine();
			
			Set<String> allClasses = new HashSet<>();
			allClasses.addAll(previousClassNames);
			allClasses.addAll(currentClassNames);
			
			return engine.compare(previousAPI, currentAPI, allClasses);
		}
	}
}