package org.codegeny.semver.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
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
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
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
	
	@Parameter
	private Set<String> publicDependencies = new HashSet<>();
	
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
		
		Map<String, Version> publicDependenciesVersion = mavenProject.getDependencies().stream()
				.filter(a -> publicDependencies.contains(a.getGroupId() + ":" + a.getArtifactId()))
				.collect(Collectors.toMap(a -> a.getGroupId() + ":" + a.getArtifactId(), a -> Version.parseVersion(a.getVersion())));
		
		if (verbose) {
			if (publicDependenciesVersion.isEmpty()) {
				getLog().info("No public dependencies found for current version");
			} else {
				getLog().info("Public dependencies found for current version:");
				publicDependenciesVersion.forEach((k, v) -> getLog().info(k + ":" + v));
			}
		}
		
		Version currentVersion = Version.parseVersion(mavenProject.getVersion());
		Version previousVersion = currentVersion.previousVersion();
		
		getLog().info("About to check difference with " + previousVersion);
		
		Artifact previousArtifact = repositorySystem.createArtifact(mavenProject.getGroupId(), mavenProject.getArtifactId(), previousVersion.toString(), mavenProject.getPackaging());
		previousArtifact = localRepository.find(previousArtifact);
		File previousArtifactFile = previousArtifact.getFile();
		
		getLog().info("Checking existance of " + previousArtifact + " / " + previousArtifactFile);
		
		if (previousArtifactFile == null || !previousArtifactFile.exists()) {
			getLog().info("Previous version does not exist in local repository, downloading");
			try {
				ArtifactRequest request = new ArtifactRequest();
				request.setArtifact(new DefaultArtifact(previousArtifact.getGroupId() + ":" + previousArtifact.getArtifactId() + ":" + previousArtifact.getVersion()));
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
			
			DefaultProjectBuildingRequest request = new DefaultProjectBuildingRequest();
			request.setLocalRepository(localRepository);
			request.setRepositorySession(repositorySystemSession);
			MavenProject previousMavenProject = projectBuilder.build(previousArtifact, request).getProject();
			
			Map<String, Version> previousPublicDependenciesVersion = previousMavenProject.getDependencies().stream()
					.filter(a -> publicDependencies.contains(a.getGroupId() + ":" + a.getArtifactId()))
					.collect(Collectors.toMap(a -> a.getGroupId() + ":" + a.getArtifactId(), a -> Version.parseVersion(a.getVersion())));
			
			if (verbose) {
				if (previousPublicDependenciesVersion.isEmpty()) {
					getLog().info("No public dependencies found for previous version");
				} else {
					getLog().info("Public dependencies found for previous version:");
					previousPublicDependenciesVersion.forEach((k, v) -> getLog().info(k + ":" + v));
				}
			}
				
			File outputDirectory = new File(mavenProject.getBuild().getOutputDirectory());
			File currentProperties = new File(outputDirectory, "META-INF/semver.properties");
			
			final Set<Integer> currentHashes;
			try (Stream<String> lines = Files.lines(currentProperties.toPath())) {
				currentHashes = lines.map(i -> Integer.parseUnsignedInt(i, 16)).collect(Collectors.toSet());			
			}
			

			final Set<Integer> previousHashes;
			try (JarFile previousJar = new JarFile(previousArtifactFile)) {
				JarEntry previousProperties = previousJar.getJarEntry("META-INF/semver.properties");
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(previousJar.getInputStream(previousProperties)))) {
					try (Stream<String> lines = reader.lines()) {
						previousHashes = lines.map(i -> Integer.parseUnsignedInt(i, 16)).collect(Collectors.toSet());
					}
				}
			}
			
			Version releaseVersion;
			if (!currentHashes.containsAll(previousHashes)) {
				releaseVersion = currentVersion.nextMajorVersion();
				getLog().info("Major breaking change (previous API has been altered in this version)");
			} else  if (!currentHashes.equals(previousHashes)) {
				releaseVersion = currentVersion.nextMinorVersion();
				getLog().info("Minor breaking change (previous API has been augmented in this version)");
			} else {
				releaseVersion = currentVersion;
				getLog().info("Patch breaking change (previous API has not been modified in this version)");
			}
			
			Version snapshotVersion = releaseVersion.nextPatchVersion();
			
			getLog().info("Release version: " + releaseVersion);
			getLog().info("Snapshot version: " + snapshotVersion + "-SNAPSHOT");
			
			System.setProperty(releaseVersionName, releaseVersion.toString());
			System.setProperty(snapshotVersionName, snapshotVersion.toString() + "-SNAPSHOT");	
			
		} catch (ProjectBuildingException projectBuildingException) {
			throw new MojoExecutionException("Cannot build previous version", projectBuildingException);
		} catch (IOException ioException) {
			throw new MojoExecutionException("Cannot open previous version", ioException);
		}
	}
}