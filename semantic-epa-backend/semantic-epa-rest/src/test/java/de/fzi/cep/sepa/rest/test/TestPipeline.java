package de.fzi.cep.sepa.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import de.fzi.cep.sepa.rest.Pipeline;

public class TestPipeline {

	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("src/main/resources/pipeline.json"));
		String json = scanner.useDelimiter("\\Z").next();
		scanner.close();
		
		new Pipeline().addPipelines(json);
	}
}
