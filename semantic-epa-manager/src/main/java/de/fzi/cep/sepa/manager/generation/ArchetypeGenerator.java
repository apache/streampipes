package de.fzi.cep.sepa.manager.generation;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;

public class ArchetypeGenerator {

	private String groupId;
	private String artifactId;
	private String elementName;
	private String tempFolderId;
	
	public ArchetypeGenerator(String groupId, String artifactId, String elementName) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.elementName = elementName;
		this.tempFolderId = RandomStringUtils.randomAlphabetic(8);
		createTempFolder(tempFolderId);
	}
	
	public File toZip()
	{
		createArchive();
		File outputFile = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId +File.separator +artifactId +".zip");
		new ZipFileGenerator(new File(ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId +File.separator +artifactId), outputFile).makeZip();
		return outputFile;
	}
	
	public void createArchive() {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals( Arrays.asList( getMavenCommand() ) );
		request.setBaseDirectory(new File(getTempDir(tempFolderId)));

		DefaultInvoker invoker = new DefaultInvoker();
		try {
			invoker.execute( request );
		} catch (MavenInvocationException e) {
			e.printStackTrace();
		}
	}
	
	private String getTempDir(String folderId) {
		return ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId;
	}

	private String getMavenCommand() {
		return "archetype:generate"
				+" -DgroupId=" +groupId 
				+" -DartifactId=" +artifactId
				+" -DarchetypeVersion=0.0.1-SNAPSHOT"
				+" -DarchetypeGroupId=de.fzi.cep.sepa"
				+" -DarchetypeArtifactId=streampipes-archetype-storm"
				+" -DarchetypeCatalog=local"
				+" -DclassNamePrefix=" +elementName;
				
	}
		
	private void createTempFolder(String folderId) {
		File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +folderId);
		file.mkdir();
	}
}
