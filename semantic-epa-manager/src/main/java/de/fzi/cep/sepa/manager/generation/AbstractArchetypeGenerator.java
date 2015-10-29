package de.fzi.cep.sepa.manager.generation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.deployment.DeploymentConfiguration;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;

public abstract class AbstractArchetypeGenerator {

	protected DeploymentConfiguration config;
	protected String tempFolderId;
	protected NamedSEPAElement element;
	
	public AbstractArchetypeGenerator(DeploymentConfiguration config, NamedSEPAElement element)
	{
		this.config = config;
		this.tempFolderId = RandomStringUtils.randomAlphabetic(8);
		this.element = element;
		createTempFolder(tempFolderId);
	}
	
	public File getGeneratedArchetype()
	{
		createArchive();
		storeDescriptionFile();
		return toZip();
	}
	
	protected File toZip()
	{
		File outputFile = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId +File.separator +config.getArtifactId() +".zip");
		new ZipFileGenerator(new File(ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId +File.separator +config.getArtifactId()), outputFile).makeZip();
		return outputFile;
	}
	
	protected void createArchive() {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setGoals(Arrays.asList(getMavenCommand()));
		request.setBaseDirectory(new File(getTempDir(tempFolderId)));

		DefaultInvoker invoker = new DefaultInvoker();
		try {
			invoker.execute( request );
		} catch (MavenInvocationException e) {
			e.printStackTrace();
		}
	}
	
	protected void storeDescriptionFile()
	{
		File descriptionFile = new File(getTempDir(tempFolderId) 
				+File.separator 
				+config.getArtifactId() 
				+File.separator 
				+"src" 
				+File.separator 
				+"main" 
				+File.separator 
				+"resources" 
				+File.separator
				+makeName(element.getName()) +".jsonld");
		
		try {
			FileUtils.write(descriptionFile, generateJsonLd());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getTempDir(String folderId) {
		return ConfigurationManager.getStreamPipesConfigFileLocation() +tempFolderId;
	}

	private String generateJsonLd()
	{
		try {
			return (Utils.asString(new JsonLdTransformer().toJsonLd(element)));
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException
				| InvalidRdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected String makeName(String elementName)
	{
		return elementName.toLowerCase().replaceAll(" ", "_");
	}
		
	protected void createTempFolder(String folderId) {
		File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() +folderId);
		file.mkdir();
	}
	
	protected abstract String getMavenCommand();
}
