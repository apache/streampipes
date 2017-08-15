package org.streampipes.codegeneration.api;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.model.client.deployment.DeploymentConfiguration;
import org.streampipes.codegeneration.ZipFileGenerator;

public abstract class ImplementationCodeGenerator extends CodeGenerator {

	protected String tempFolder;

	public ImplementationCodeGenerator(DeploymentConfiguration config, ConsumableSEPAElement element) {
		super(config, element);
		this.tempFolder = RandomStringUtils.randomAlphabetic(8) + config.getArtifactId();
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

	//TODO change this
	public File getGeneratedFile() {
		return createProject();
	}

	protected abstract void create();

	protected abstract void createDirectoryStructure();

	public abstract String getDeclareModel();

	protected String getTempDir() {
		return System.getProperty("user.home") + File.separator +".streampipes" +File.separator + tempFolder + File.separator;
	}

	protected File toZip() {
		String generatedProjects = System.getProperty("user.home") + File.separator +".streampipes" +File.separator + "generated_projects"
				+ File.separator;
		createFolder(generatedProjects);

		String zipFolder = generatedProjects + new Date().getTime() + "_";
		File outputFile = new File(zipFolder + config.getArtifactId() + ".zip");
		new ZipFileGenerator(new File(getTempDir()), outputFile).makeZip();
		return outputFile;
	}
}
