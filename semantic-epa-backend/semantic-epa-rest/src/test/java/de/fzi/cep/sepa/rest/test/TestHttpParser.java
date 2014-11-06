package de.fzi.cep.sepa.rest.test;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.rest.ActionImpl;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.cep.sepa.storage.util.Transformer;


public class TestHttpParser {

	public static void main(String[] args) throws ClientProtocolException, URISyntaxException, IOException
	{
		//new ActionImpl().addSEP(null,"http://localhost:8091/jms");
		//System.out.println(new SEP().getSEPs());
		
		//String result = HttpJsonParser.getContentFromUrl("http://localhost:8091/jms");
		
		//System.out.println(result);
		//SEC sec = Transformer.fromJsonLd(SEC.class, result);
		//System.out.println(sec.getName());
		
		//SEPA sepa = Transformer.fromJsonLd(SEPA.class, result);
		//String x = Utils.getGson().toJson(ClientModelTransformer.toSEPAClientModel(sepa));
		//System.out.println(x);
		/*
		if (StorageManager.INSTANCE.getStorageAPI().exists(sep))
		{
			System.out.println("exists");
			//StorageManager.INSTANCE.getStorageAPI().deleteSEP(sep);
			System.out.println(sep.getRdfId().toString());
		}
		*/
		//StorageManager.INSTANCE.getStorageAPI().storeSEP(result);
		 
		 
		
		//new SEP().addSEP("", "http://localhost:8089/twitter");
	}
}
