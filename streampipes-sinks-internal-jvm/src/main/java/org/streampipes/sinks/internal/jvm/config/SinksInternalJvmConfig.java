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

package org.streampipes.sinks.internal.jvm.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum SinksInternalJvmConfig implements PeConfig {
	INSTANCE;

	private SpConfig config;

	public final static String serverUrl;
	public final static String iconBaseUrl;

	private final static String service_id = "pe/org.streampipes.sinks.internal.jvm";
	private final static String service_name = "Sinks Internal JVM";
	private final static String service_container_name = "sinks-internal-jvm";


	SinksInternalJvmConfig() {
		config = SpConfig.getSpConfig(service_id);
		config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe esper");
		config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

		config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
		config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");
		config.register(ConfigKeys.NGINX_HOST, System.getenv("STREAMPIPES_HOST"), "External hostname of " +
						"StreamPipes Nginx");
		config.register(ConfigKeys.NGINX_PORT, 80, "External port of StreamPipes Nginx");
		config.register(ConfigKeys.COUCHDB_HOST, "couchdb", "Host for couchdb of the pe sinks project");
		config.register(ConfigKeys.COUCHDB_PORT, 5984, "Port for couchdb of the pe sinks project");
		config.register(ConfigKeys.JMS_HOST, "activemq", "Hostname for pe actions service for active mq");
		config.register(ConfigKeys.JMS_PORT, 61616, "Port for pe actions service for active mq");

		config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

	}
	
	static {
		serverUrl = SinksInternalJvmConfig.INSTANCE.getHost() + ":" + SinksInternalJvmConfig.INSTANCE.getPort();
		iconBaseUrl = "http://" + SinksInternalJvmConfig.INSTANCE.getIconHost() + ":" + SinksInternalJvmConfig.INSTANCE.getIconPort() +"/assets/img/pe_icons";
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

	public String getCouchDbHost() {
		return config.getString(ConfigKeys.COUCHDB_HOST);
	}

	public int getCouchDbPort() {
		return config.getInteger(ConfigKeys.COUCHDB_PORT);
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

	public String getNginxHost() {
		return config.getString(ConfigKeys.NGINX_HOST);
	}

	public Integer getNginxPort() {
	    
		return config.getInteger(ConfigKeys.NGINX_PORT);
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
