package de.fzi.cep.sepa.manager.pipeline;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

public class TestPipelineRenaming {

	public static void main(String[] args) throws Exception
	{
		System.out.println(StorageManager.INSTANCE.getPipelineStorageAPI().getRunningVisualizations().size());
		
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestPipelineRenaming.jsonld"), "UTF-8"), Pipeline.class);
		System.out.println(Utils.getGson().toJson(pipeline));
		Operations.findRecommendedElements(new Cloner().deepClone(pipeline));
		PipelineModificationMessage message = Operations.validatePipeline(new Cloner().deepClone(pipeline), true);
		System.out.println(Utils.getGson().toJson(pipeline));
		System.out.println(Utils.getGson().toJson(message));
	}
}
