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

package org.apache.streampipes.processors.changedetection.jvm.config;


import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum ChangeDetectionJvmConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;

	public final static String serverUrl;

	private final static String service_id = "pe/org.apache.streampipes.processors.changedetection.jvm";
	private final static String service_name = "Processors Change Detection JVM";
	private final static String service_container_name = "processors-change-detection-jvm";

	ChangeDetectionJvmConfig() {
		config = SpConfig.getSpConfig(service_id);
		config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe esper");
		config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

		config.register(ConfigKeys.SERVICE_NAME_KEY, service_name, "The name of the service");

	}
	
	static {
		serverUrl = ChangeDetectionJvmConfig.INSTANCE.getHost() + ":" + ChangeDetectionJvmConfig.INSTANCE.getPort();
	}

	@Override
	public String getHost() {
		return config.getString(ConfigKeys.HOST);
	}

	@Override
	public int getPort() {
		return config.getInteger(ConfigKeys.PORT);
	}

	@Override
	public String getId() {
		return service_id;
	}

	@Override
	public String getName() {
		return config.getString(ConfigKeys.SERVICE_NAME_KEY);
	}

}
