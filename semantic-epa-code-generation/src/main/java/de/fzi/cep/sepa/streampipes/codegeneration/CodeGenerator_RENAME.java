package de.fzi.cep.sepa.streampipes.codegeneration;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public abstract class CodeGenerator_RENAME {

	protected String tempFolder;
	protected ConsumableSEPAElement element;
	protected DeploymentConfiguration config;

	public CodeGenerator_RENAME(DeploymentConfiguration config, ConsumableSEPAElement element) {
		this.tempFolder = RandomStringUtils.randomAlphabetic(8) + config.getArtifactId();
		this.element = element;
		this.config = config;
	}

	public File createProject() {
		createFolder(getTempDir());
		create();
		File result = toZip();
		deleteFolder(getTempDir());
		return result;
	}

	private void createFolder(String folder) {
		File file = new File(folder);
		file.mkdir();
	}
	
	private void deleteFolder(String folder) {
		try {
			FileUtils.deleteDirectory(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract File getGeneratedFile();

	protected abstract void create();

	protected abstract void createDirectoryStructure();

	protected String getTempDir() {
		return ConfigurationManager.getStreamPipesConfigFileLocation() + tempFolder + File.separator;
	}

	protected File toZip() {
		String generatedProjects = ConfigurationManager.getStreamPipesConfigFileLocation() + "generated_projects"
				+ File.separator;
		createFolder(generatedProjects);

		String zipFolder = generatedProjects + new Timestamp(System.currentTimeMillis()) + "_";
		File outputFile = new File(zipFolder + config.getArtifactId() + ".zip");
		new ZipFileGenerator(new File(getTempDir()), outputFile).makeZip();
		return outputFile;
	}
}
