package de.fzi.cep.sepa.storage.util;

import java.util.List;

import org.lightcouch.CouchDbClient;

import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.input.Option;

public class Utils {

	public static StaticProperty getClientPropertyById(List<StaticProperty> properties, String id)
	{
		for(StaticProperty p : properties)
		{
			if (p.getElementId().equals(id)) return p;
		}
		return null;
		//TODO exceptions
	}
	
	public static Option getOptionById(List<Option> options, String id) {
		for(Option o : options)
		{
			if (o.getElementId().equals(id)) return o;
		}
		return null;
	}
	
	public static CouchDbClient getCouchDBClient()
	{
		CouchDbClient dbClient = new CouchDbClient("couchdb-pipeline.properties");
		dbClient.setGsonBuilder(de.fzi.sepa.model.client.util.Utils.getGsonBuilder());
		return dbClient;
	}
}
