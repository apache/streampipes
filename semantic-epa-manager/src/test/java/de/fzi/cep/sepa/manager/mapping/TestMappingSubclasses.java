package de.fzi.cep.sepa.manager.mapping;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.fzi.cep.sepa.commons.exceptions.NoMatchingGroundingException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.sepa.model.client.util.Utils;

public class TestMappingSubclasses {

	public static void main(String[] args) throws NoMatchingGroundingException, NoMatchingSchemaException, Exception
	{
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestMappingSubclasses.jsonld"), "UTF-8"), Pipeline.class);
		System.out.println(pipeline.getSepas().size());
		
		PipelineModificationMessage message = new PipelineValidationHandler(pipeline, true).validateConnection().computeMappingProperties().getPipelineModificationMessage();
		
		System.out.println(Utils.getGson().toJson(message));
	}
}
