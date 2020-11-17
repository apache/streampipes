/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.internal.jvm.config;


import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum SinksInternalJvmConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;

	public final static String serverUrl;

	private final static String service_id = "pe/org.apache.streampipes.sinks.internal.jvm";
	private final static String service_name = "Sinks Internal JVM";
	private final static String service_container_name = "sinks-internal-jvm";


	SinksInternalJvmConfig() {
		config = SpConfig.getSpConfig(service_id);
		config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe esper");
		config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

		config.register(ConfigKeys.COUCHDB_HOST, "couchdb", "Host for couchdb of the pe sinks project");
		config.register(ConfigKeys.COUCHDB_PORT, 5984, "Port for couchdb of the pe sinks project");
		config.register(ConfigKeys.JMS_HOST, "activemq", "Hostname for pe actions service for active mq");
		config.register(ConfigKeys.JMS_PORT, 61616, "Port for pe actions service for active mq");
		config.register(ConfigKeys.DATA_LAKE_HOST, "influxdb", "Hostname for the StreamPipes data lake database");
		config.register(ConfigKeys.DATA_LAKE_PROTOCOL, "http", "Protocol for the StreamPipes data lake database");
		config.register(ConfigKeys.DATA_LAKE_PORT, 8086, "Port for the StreamPipes data lake database");
		config.register(ConfigKeys.DATA_LAKE_USERNAME, "default", "Username for the StreamPipes data lake database");
		config.registerPassword(ConfigKeys.DATA_LAKE_PASSWORD, "default", "Password for the StreamPipes data lake database");
		config.register(ConfigKeys.DATA_LAKE_DATABASE_NAME, "sp", "Database name for the StreamPipes data lake database");
		config.register(ConfigKeys.BACKEND_HOST, "backend", "Hostname for the StreamPipes-Backend");
		config.register(ConfigKeys.BACKEND_PORT, 8030, "Port for the StreamPipes-Backend");
		config.register(ConfigKeys.BACKEND_PROTOCOL, "http", "Protocol for the StreamPipes-Backend");
		config.register(ConfigKeys.IMAGE_STORAGE_LOCATION, "/spImages/", "Storage location of the data lake images");

		config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

	}
	
	static {
		serverUrl = SinksInternalJvmConfig.INSTANCE.getHost() + ":" + SinksInternalJvmConfig.INSTANCE.getPort();
	}

	@Override
	public String getHost() {
		return config.getString(ConfigKeys.HOST);
	}

	@Override
	public int getPort() {
		return config.getInteger(ConfigKeys.PORT);
	}

	public String getCouchDbHost() {
		return config.getString(ConfigKeys.COUCHDB_HOST);
	}

	public int getCouchDbPort() {
		return config.getInteger(ConfigKeys.COUCHDB_PORT);
	}

	public String getJmsHost() {
		return config.getString(ConfigKeys.JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(ConfigKeys.JMS_PORT);
	}

	public String getDataLakeHost() {
		return config.getString(ConfigKeys.DATA_LAKE_HOST);
	}

	public String getDataLakeProtocol() {
		return config.getString(ConfigKeys.DATA_LAKE_PROTOCOL);
	}

	public Integer getDataLakePort() {
		return config.getInteger(ConfigKeys.DATA_LAKE_PORT);
	}

	public String getDataLakeUsername() {
		return config.getString(ConfigKeys.DATA_LAKE_USERNAME);
	}


	public String getDataLakePassword() {
		return config.getString(ConfigKeys.DATA_LAKE_PASSWORD);
	}


	public String getDataLakeDatabaseName() {
		return config.getString(ConfigKeys.DATA_LAKE_DATABASE_NAME);
	}


	@Override
	public String getId() {
		return service_id;
	}

	@Override
	public String getName() {
		return config.getString(ConfigKeys.SERVICE_NAME);
	}


	public String getStreamPipesBackendHost() {
		return config.getString(ConfigKeys.BACKEND_HOST);
	}

	public String getStreamPipesBackendProtocol() {
		return config.getString(ConfigKeys.BACKEND_PROTOCOL);
	}


	public String getImageStorageLocation() {
		return config.getString(ConfigKeys.IMAGE_STORAGE_LOCATION);
	}

	public Integer getStreamPipesBackendPort() {
		return config.getInteger(ConfigKeys.BACKEND_PORT);
	}

	public String getStreamPipesBackendUrl() { return getStreamPipesBackendProtocol() + "://"
			+ getStreamPipesBackendHost() + ":" + getStreamPipesBackendPort(); }



}
