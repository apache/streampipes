package de.fzi.cep.sepa.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import de.fzi.cep.sepa.rest.util.Utils;

public class TestJSONConvert {
	
	

	public static void main(String[] argv){
		
		String content = null;
		
		
		try {
			content = new Scanner(new File("C:\\Users\\kaulfers\\workspace\\semantic-epa-parent\\semantic-epa-backend\\semantic-epa-rest\\src\\main\\resources\\TestJSON.json")).useDelimiter("\\Z").next();
			System.out.println(content);
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		de.fzi.cep.sepa.model.client.Pipeline ServerPipeline = null;
		
		try{
		
		ServerPipeline = Utils.getGson().fromJson(content, de.fzi.cep.sepa.model.client.Pipeline.class);
		
		}catch (RuntimeException e){
			e.printStackTrace();
		}
		System.out.println("TEST");
		
		System.out.println("\n");
		System.out.println("Streams: " + ServerPipeline.getStreams().size());
		
		for (int i = 0; i < ServerPipeline.getStreams().size(); i++){
			System.out.println(ServerPipeline.getStreams().get(i).getName());
			System.out.println(ServerPipeline.getStreams().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Sepas: " + ServerPipeline.getSepas().size());
		for (int i = 0; i < ServerPipeline.getSepas().size(); i++){
			System.out.println(ServerPipeline.getSepas().get(i).getName());
			System.out.println(ServerPipeline.getSepas().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Action: ");
		System.out.println(ServerPipeline.getAction().getName());
		
		
	}
	
	
}
