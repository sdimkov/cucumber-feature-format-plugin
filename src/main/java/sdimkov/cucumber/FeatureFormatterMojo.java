package sdimkov.cucumber;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class FeatureFormatterMojo extends AbstractMojo
{
	private String[] featureExtensions = new String[] { "feature"};

	@Parameter(defaultValue = "${project.basedir}", required = true, readonly = true)
	private File baseDir;

	// Indents

	@Parameter(property = "format.featureIndent", defaultValue = "0")
	private int featureIndent;

	@Parameter(property = "format.scenarioIndent", defaultValue = "2")
	private int scenarioIndent;

	@Parameter(property = "format.scenarioIndent", defaultValue = "4")
	private int givenIndent;

	@Parameter(property = "format.scenarioIndent", defaultValue = "5")
	private int whenIndent;

	@Parameter(property = "format.scenarioIndent", defaultValue = "5")
	private int thenIndent;

	@Parameter(property = "format.scenarioIndent", defaultValue = "6")
	private int andIndent;

	// Blank lines before

	@Parameter(property = "format.featureBlankLines", defaultValue = "0")
	private int featureBlankLines;

	@Parameter(property = "format.scenarioBlankLines", defaultValue = "2")
	private int scenarioBlankLines;

	@Parameter(property = "format.scenarioBlankLines", defaultValue = "0")
	private int givenBlankLines;

	@Parameter(property = "format.scenarioBlankLines", defaultValue = "0")
	private int whenBlankLines;

	@Parameter(property = "format.scenarioBlankLines", defaultValue = "0")
	private int thenBlankLines;

	@Parameter(property = "format.scenarioBlankLines", defaultValue = "0")
	private int andBlankLines;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		Iterator<File> iterator = FileUtils.iterateFiles(baseDir, featureExtensions, true);

		while (iterator.hasNext()) {
			File featureFile = iterator.next();
			try {
				getLog().debug("Processing " + featureFile.getAbsolutePath());
				new FluentFormatter(featureFile)
						.setBlankLinesBefore("Feature:", featureBlankLines)
						.setBlankLinesBefore("Scenario:", scenarioBlankLines)
						.setBlankLinesBefore("Given", givenBlankLines)
						.setBlankLinesBefore("When", whenBlankLines)
						.setBlankLinesBefore("Then", thenBlankLines)
						.setBlankLinesBefore("And", andBlankLines)

						.setIndent("Feature:", featureIndent)
						.setIndent("Scenario:", scenarioIndent)
						.setIndent("Given", givenIndent)
						.setIndent("When", whenIndent)
						.setIndent("Then", thenIndent)
						.setIndent("And", andIndent)

						.format().save();
			}
			catch (IOException e) {
				getLog().error("Unable to process " + featureFile.getAbsolutePath(), e);
			}
			catch (Throwable t) {
				getLog().error("Unhandled exception:", t);
			}
		}
	}
}
