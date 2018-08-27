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

package org.streampipes.processors.imageprocessing.jvm.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

import static org.apache.kafka.common.security.JaasUtils.SERVICE_NAME;
import static org.streampipes.processors.imageprocessing.jvm.config.ConfigKeys.*;

public enum ImageProcessingJvmConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;


	public final static String serverUrl;
	public final static String iconBaseUrl;

	private final static String service_id = "pe/org.streampipes.processors.imageprocessing.jvm";
	private final static String service_name = "Processors Image Processing JVM";
	private final static String service_container_name = "processors-imageprocessing-jvm";

	ImageProcessingJvmConfig() {
		config = SpConfig.getSpConfig(service_id);
		config.register(ConfigKeys.HOST, "pe-jvm-hmi", "Hostname for the pe image processing");
		config.register(ConfigKeys.PORT, 8090, "Port for the pe image processing");

		config.register(ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ICON_PORT, 80, "Port for the icons in nginx");
		config.register(ConfigKeys.NGINX_HOST, System.getenv("STREAMPIPES_HOST"), "External hostname of " +
						"StreamPipes Nginx");
		config.register(ConfigKeys.NGINX_PORT, 80, "External port of StreamPipes Nginx");
		config.register(KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
		config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
		config.register(ConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sinks project");
		config.register(ConfigKeys.ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sinks project");
		config.register(ConfigKeys.JMS_HOST, "tcp://activemq", "Hostname for pe actions service for active mq");
		config.register(ConfigKeys.JMS_PORT, 61616, "Port for pe actions service for active mq");

		config.register(MODEL_DIRECTORY, "/model-repository/", "The directory location for the folders of the image classification models");

		config.register(ConfigKeys.SERVICE_NAME_KEY, "HMI JVM", "The name of the service");

	}
	
	static {
		serverUrl = ImageProcessingJvmConfig.INSTANCE.getHost() + ":" + ImageProcessingJvmConfig.INSTANCE.getPort();
		iconBaseUrl = ImageProcessingJvmConfig.INSTANCE.getIconHost() + ":" + ImageProcessingJvmConfig.INSTANCE.getIconPort() +"/assets/img/pe_icons";
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
		return config.getString(ICON_HOST);
	}

	public int getIconPort() {
		return config.getInteger(ICON_PORT);
	}

	public String getKafkaHost() {
		return config.getString(KAFKA_HOST);
	}

	public int getKafkaPort() {
		return config.getInteger(KAFKA_PORT);
	}

	public String getKafkaUrl() {
		return getKafkaHost() + ":" + getKafkaPort();
	}

	public String getZookeeperHost() {
		return config.getString(ZOOKEEPER_HOST);
	}

	public int getZookeeperPort() {
		return config.getInteger(ZOOKEEPER_PORT);
	}

	public String getJmsHost() {
		return config.getString(JMS_HOST);
	}

	public int getJmsPort() {
		return config.getInteger(JMS_PORT);
	}

	public String getJmsUrl() {
		return getJmsHost() + ":" + getJmsPort();
	}

	public String getNginxHost() {
		return config.getString(NGINX_HOST);
	}

	public Integer getNginxPort() {
		return config.getInteger(NGINX_PORT);
	}

	public String getModelDirectory() {
		return config.getString(MODEL_DIRECTORY);
	}

	@Override
	public String getId() {
		return service_id;
	}

	@Override
	public String getName() {
		return config.getString(SERVICE_NAME);
	}




}
