package de.fzi.cep.sepa.manager.pipeline;

import java.io.File;

import org.apache.commons.io.FileUtils;


import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.sepa.model.client.util.Utils;

public class TestGrounding {

	public static void main(String[] args) throws Exception
	{
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestGrounding.jsonld"), "UTF-8"), Pipeline.class);
		Operations.validatePipeline(pipeline, true);
	}
}
