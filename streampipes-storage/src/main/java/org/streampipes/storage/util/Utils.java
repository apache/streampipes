package org.streampipes.storage.util;

import org.streampipes.model.util.GsonSerializer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import org.streampipes.commons.config.old.Configuration;

public class Utils {



	public static CouchDbClient getCouchDbPipelineClient() {
		CouchDbClient dbClient = new CouchDbClient(props("pipeline"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbSepaInvocationClient() {
		CouchDbClient dbClient = new CouchDbClient(props("invocation"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}

	public static CouchDbClient getCouchDbConnectionClient() {
		CouchDbClient dbClient = new CouchDbClient(props("connection"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbVisualizationClient() {
		CouchDbClient dbClient = new CouchDbClient(props("visualizations"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbRdfEndpointClient() {
		CouchDbClient dbClient = new CouchDbClient(props("rdfendpoint"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbVisualizablePipelineClient() {
		CouchDbClient dbClient = new CouchDbClient(props("visualizablepipeline"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbDashboardClient() {
		CouchDbClient dbClient = new CouchDbClient(props("dashboard"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbUserClient() {
		CouchDbClient dbClient = new CouchDbClient(props("users"));
		dbClient.setGsonBuilder(GsonSerializer.getGsonBuilder());
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbMonitoringClient() {
		CouchDbClient dbClient = new CouchDbClient(props("monitoring"));
		return dbClient;
	}
	
	public static CouchDbClient getCouchDbNotificationClient() {
		return new CouchDbClient(props("notification"));
	}
	
	public static CouchDbClient getCouchDbPipelineCategoriesClient() {
		return new CouchDbClient(props("pipelineCategories"));
	}
	
	private static Configuration cfg() {
		return Configuration.getInstance();
	}

	private static CouchDbProperties props(String dbname)
	{
		return new CouchDbProperties(dbname, true, CouchDbConfig.INSTANCE.getProtocol(),
				CouchDbConfig.INSTANCE.getHost(), CouchDbConfig.INSTANCE.getPort(), null, null);
	}
}
