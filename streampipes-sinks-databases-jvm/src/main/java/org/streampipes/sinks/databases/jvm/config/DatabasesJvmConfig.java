/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sinks.databases.jvm.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum DatabasesJvmConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;

	public final static String serverUrl;
	public final static String iconBaseUrl;

	private final static String service_id = "pe/org.streampipes.sinks.databases.jvm";
	private final static String service_name = "Sinks Databases JVM";
	private final static String service_container_name = "sinks-databases-jvm";

	DatabasesJvmConfig() {
		config = SpConfig.getSpConfig(service_id);
		config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe esper");
		config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

		config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");
		config.register(ConfigKeys.KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
		config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
		config.register(ConfigKeys.JMS_HOST, "activemq", "Hostname for pe actions service for active mq");
		config.register(ConfigKeys.JMS_PORT, 61616, "Port for pe actions service for active mq");

		config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

	}
	
	static {
		serverUrl = DatabasesJvmConfig.INSTANCE.getHost() + ":" + DatabasesJvmConfig.INSTANCE.getPort();
		iconBaseUrl = "http://" + DatabasesJvmConfig.INSTANCE.getIconHost() + ":" + DatabasesJvmConfig.INSTANCE.getIconPort() +"/assets/img/pe_icons";
	}

	public static final String getIconUrl(String pictureName) {
		return iconBaseUrl +"/" +pictureName +".png";
	}

	@Override
	public String getHost() {
		return config.getString(ConfigKeys.HOST);
	}

	@Override
	public int getPort() {
		return config.getInteger(ConfigKeys.PORT);
	}

	public String getIconHost() {
		return config.getString(ConfigKeys.ICON_HOST);
	}

	public int getIconPort() {
		return config.getInteger(ConfigKeys.ICON_PORT);
	}

	public String getKafkaHost() {
		return config.getString(ConfigKeys.KAFKA_HOST);
	}

	public int getKafkaPort() {
		return config.getInteger(ConfigKeys.KAFKA_PORT);
	}

	public String getKafkaUrl() {
		return getKafkaHost() + ":" + getKafkaPort();
	}

	public String getJmsHost() {
		return "tcp://" + config.getString(ConfigKeys.JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(ConfigKeys.JMS_PORT);
	}

	public String getJmsUrl() {
		return getJmsHost() + ":" + getJmsPort();
	}

	@Override
	public String getId() {
		return service_id;
	}

	@Override
	public String getName() {
		return config.getString(ConfigKeys.SERVICE_NAME);
	}




}
