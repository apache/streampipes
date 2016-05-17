package de.fzi.cep.sepa.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

public class TestPipeline {

	public static void main(String[] args) throws FileNotFoundException
	{
        URL url = Thread.currentThread().getContextClassLoader().getResource("TestJSON.json");
		Scanner scanner = new Scanner(new File(url.getPath()));
        String json = scanner.useDelimiter("\\Z").next();
		scanner.close();

		System.out.println(json);
		Pipeline pipeline = Utils.getGson().fromJson(json, Pipeline.class);
		
		System.out.println(pipeline.getStreams().size());
		System.out.println(pipeline.getSepas().size());
		
		StorageManager.INSTANCE.getPipelineStorageAPI().store(pipeline);
		
		StorageManager.INSTANCE.getPipelineStorageAPI().getAllPipelines();
	}
}
