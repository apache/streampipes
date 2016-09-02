package de.fzi.cep.sepa.storage.util;

import de.fzi.cep.sepa.model.util.GsonSerializer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import de.fzi.cep.sepa.commons.config.Configuration;

public class Utils {

	public static CouchDbClient getCouchDbPipelineClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), cfg().COUCHDB_PIPELINE_DB));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbSepaInvocationClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), cfg().COUCHDB_SEPA_INVOCATION_DB));
		dbClient.setGsonBuilder(de.fzi.cep.sepa.model.util.GsonSerializer.getGsonBuilder());
		return dbClient;
	}

	public static CouchDbClient getCouchDbConnectionClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), cfg().COUCHDB_CONNECTION_DB));
		return dbClient;
	}

	public static CouchDbClient getCouchDbUserClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), cfg().COUCHDB_USER_DB));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbBlockClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "blocks"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbMonitoringClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), cfg().COUCHDB_MONITORING_DB));
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbNotificationClient() {
		return new CouchDbClient(props(cfg(), cfg().COUCHDB_NOTIFICATION_DB));
	}
	
	public static CouchDbClient getCouchDbPipelineCategoriesClient() {
		return new CouchDbClient(props(cfg(), cfg().COUCHDB_PIPELINE_CATEGORY_DB));
	}
	
	public static CouchDbClient getCouchDbAppStorageClient() {
		return new CouchDbClient(props(cfg(), "apps"));
	}

	private static Configuration cfg() {
		return Configuration.getInstance();
	}
	
	private static CouchDbProperties props(Configuration cfg, String dbname)
	{
		return new CouchDbProperties(dbname, true, cfg.COUCHDB_PROTOCOL, cfg.COUCHDB_HOSTNAME, cfg.COUCHDB_PORT, null, null);	
	}
}
