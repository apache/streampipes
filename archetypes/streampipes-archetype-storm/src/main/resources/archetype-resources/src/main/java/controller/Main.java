package ${package}.controller;

import java.util.Arrays;

import de.fzi.cep.sepa.desc.ModelSubmitter;

public class Main {

	public static void main(String[] args)
	{
		try {
			ModelSubmitter.submitAgent(Arrays.asList(new ${classNamePrefix}Controller()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
