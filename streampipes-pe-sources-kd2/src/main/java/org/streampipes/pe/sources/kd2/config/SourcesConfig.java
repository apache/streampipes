package org.streampipes.pe.sources.kd2.config;

import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.config.SpConfig;

/**
 * Created by riemer on 18.11.2016.
 */
public enum SourcesConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";

    public final static String serverUrl;
    public final static String iconBaseUrl;
    public final static String topicPrefixBiodata;

   	SourcesConfig() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.sources.kd2");
		config.register(HOST, "sources-hella", "Hostname for the pe sources kd2");
		config.register(PORT, 8090, "Port for the pe sources kd2");
	}


	static {
    	serverUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort();
		iconBaseUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort() +"/img";

        topicPrefixBiodata = "kd2.biodata.";
    }

    public String getHost() {
        return config.getString(HOST);
    }

    public int getPort() {
        return config.getInteger(PORT);
    }

}
