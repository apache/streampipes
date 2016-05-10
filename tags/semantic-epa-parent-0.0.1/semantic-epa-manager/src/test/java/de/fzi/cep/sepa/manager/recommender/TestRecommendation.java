package de.fzi.cep.sepa.manager.recommender;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.sepa.model.client.util.Utils;

public class TestRecommendation {

	public static void main(String[] args) throws Exception
	{
		Thread.sleep(2000);
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestRecommendation2.jsonld"), "UTF-8"), Pipeline.class);
		System.out.println(pipeline.getSepas().size());
		long start = System.currentTimeMillis();
		RecommendationMessage message = Operations.findRecommendedElements(pipeline);
		long end = System.currentTimeMillis();
		System.out.println("took: " +(end-start));
		start = System.currentTimeMillis();
		System.out.println(Utils.getGson().toJson(message));
		RecommendationMessage message2 = Operations.findRecommendedElements(pipeline);
		end = System.currentTimeMillis();
		System.out.println("took: " +(end-start));
		start = System.currentTimeMillis();
		System.out.println(Utils.getGson().toJson(message));
		RecommendationMessage message3 = Operations.findRecommendedElements(pipeline);
		end = System.currentTimeMillis();
		System.out.println("took: " +(end-start));
		System.out.println(Utils.getGson().toJson(message2));
	}
}
