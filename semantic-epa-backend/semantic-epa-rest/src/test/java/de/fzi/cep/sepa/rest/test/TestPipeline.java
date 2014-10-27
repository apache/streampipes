package de.fzi.cep.sepa.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;




import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.Test;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestPipeline {

	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("src/main/resources/TestJSON.json"));
		String json = scanner.useDelimiter("\\Z").next();
		scanner.close();
		
		System.out.println(json);
		Pipeline pipeline = Utils.getGson().fromJson(json, Pipeline.class);
		StorageManager.INSTANCE.getPipelineStorageAPI().store(pipeline);
	}
}
