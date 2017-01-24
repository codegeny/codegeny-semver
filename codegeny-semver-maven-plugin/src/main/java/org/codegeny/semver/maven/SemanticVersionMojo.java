package org.codegeny.semver.maven;

import java.io.File;
import java.lang.reflect.Member;

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
import org.codegeny.semver.Module;
import org.codegeny.semver.ModuleChecker;
import org.codegeny.semver.Reporter;
import org.codegeny.semver.Version;
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

	@Parameter(defaultValue = "false", property = "semver.verbose")
	private boolean verbose;

	@Parameter(defaultValue = "false", property = "semver.skip")
	private boolean skip;

	@Parameter(defaultValue = "semver.releaseVersion", property = "semver.releaseVersion.target")
	private String releaseVersionName;

	@Parameter(defaultValue = "semver.snapshotVersion", property = "semver.snapshotVersion.target")
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
			
			ModuleChecker moduleChangeChecker = ModuleChecker.newConfiguredInstance();
			
			Module currentModule = new Module(new File(mavenProject.getBuild().getOutputDirectory()));
			Module previousModule = new Module(previousArtifactFile);
			
			Change change = moduleChangeChecker.check(previousModule, currentModule, new Reporter() {
				
				@Override
				public void report(Change change, String name, Member previous, Member current) {
					getLog().info(String.format("%s :: %s :: %s - %s", change, name, previous, current));
				}
				
				@Override
				public void report(Change change, String name, Class<?> previous, Class<?> current) {
					getLog().info(String.format("%s :: %s :: %s - %s", change, name, previous, current));
				}
			});
			
			Version releaseVersion = change.nextVersion(currentVersion);
			Version snapshotVersion = releaseVersion.nextPatchVersion();
			
			getLog().info("Change type " + change);
			getLog().info("Release version " + releaseVersion);
			getLog().info("Snapshot version " + snapshotVersion + "-SNAPSHOT");
			
		} catch (Exception exception) {
			throw new MojoExecutionException("Cannot compare APIs", exception);
		} 
	}
}