/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.utils;

import org.streampipes.serializers.json.GsonSerializer;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.streampipes.storage.couchdb.utils.CouchDbConfig;

public class Utils {

	public static CouchDbClient getCouchDbAdapterClient() {
		CouchDbClient dbClient = new CouchDbClient(props("adapter"));
		dbClient.setGsonBuilder(GsonSerializer.getAdapterGsonBuilder());
		return dbClient;
	}

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

	//TODO: Remove??
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

	public static CouchDbClient getCouchDbInternalUsersClient() {
		CouchDbClient dbClient = new CouchDbClient(props("_users"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbReplicatorClient() {
		CouchDbClient dbClient = new CouchDbClient(props("_replicator"));
		return dbClient;
	}

	public static CouchDbClient getCouchDbGlobalChangesClient() {
		CouchDbClient dbClient = new CouchDbClient(props("_global_changes"));
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
		return new CouchDbClient(props("pipelinecategories"));
	}

	public static CouchDbClient getCouchDbElasticsearchFilesEndppointClient() {
		return new CouchDbClient(props("file-export-endpoints-elasticsearch"));
	}

	public static CouchDbClient getCoucbDbClient(String table) {
		return new CouchDbClient(props(table));
	}
	
	private static CouchDbProperties props(String dbname)
	{
		return new CouchDbProperties(dbname, true, CouchDbConfig.INSTANCE.getProtocol(),
				CouchDbConfig.INSTANCE.getHost(), CouchDbConfig.INSTANCE.getPort(), null, null);
	}
}
