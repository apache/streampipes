package de.fzi.cep.sepa.storage.util;

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import de.fzi.cep.sepa.commons.config.Configuration;
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
	
	public static CouchDbClient getCouchDbPipelineClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_PIPELINE_DB));
		dbClient.setGsonBuilder(de.fzi.sepa.model.client.util.Utils.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbSepaInvocationClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_SEPA_INVOCATION_DB));
//		dbClient.set
//		dbClient.setGsonBuilder(de.fzi.sepa.model.client.util.Utils.getGsonBuilder());
		dbClient.setGsonBuilder(de.fzi.cep.sepa.model.util.GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	
	
	public static CouchDbClient getCouchDbConnectionClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_CONNECTION_DB));
		return dbClient;
	}

	public static CouchDbClient getCouchDbUserClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_USER_DB));
		dbClient.setGsonBuilder(de.fzi.sepa.model.client.util.Utils.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbBlockClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, "blocks"));
		dbClient.setGsonBuilder(de.fzi.sepa.model.client.util.Utils.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbMonitoringClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_MONITORING_DB));
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbNotificationClient()
	{
		Configuration cfg = Configuration.getInstance();
		CouchDbClient dbClient = new CouchDbClient(props(cfg, cfg.COUCHDB_NOTIFICATION_DB));
		return dbClient;
	}
	
	private static CouchDbProperties props(Configuration cfg, String dbname)
	{
		return new CouchDbProperties(dbname, true, cfg.COUCHDB_PROTOCOL, cfg.COUCHDB_HOSTNAME, cfg.COUCHDB_PORT, null, null);	
	}
}
