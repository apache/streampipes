package org.streampipes.storage.util;

import org.streampipes.model.util.GsonSerializer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import org.streampipes.commons.config.Configuration;

public class Utils {



	public static CouchDbClient getCouchDbPipelineClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "pipeline"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbSepaInvocationClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "invocation"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}

	public static CouchDbClient getCouchDbConnectionClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "connection"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbVisualizationClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "visualizations"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbRdfEndpointClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "rdfendpoint"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbVisualizablePipelineClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "visualizablepipeline"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbDashboardClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "dashboard"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbUserClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "users"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbBlockClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "blocks"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbMonitoringClient() {
		CouchDbClient dbClient = new CouchDbClient(props(cfg(), "monitoring"));
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbNotificationClient() {
		return new CouchDbClient(props(cfg(), "notification"));
	}
	
	public static CouchDbClient getCouchDbPipelineCategoriesClient() {
		return new CouchDbClient(props(cfg(), "pipelineCategories"));
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
