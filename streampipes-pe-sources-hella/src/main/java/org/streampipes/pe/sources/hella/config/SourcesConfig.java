package org.streampipes.pe.sources.hella.config;


import org.streampipes.config.SpConfig;

public enum SourcesConfig {
    INSTANCE;

    private SpConfig config;
    private final static String HOST = "host";
    private final static String PORT = "port";

	public final static String serverUrl;
	public final static String iconBaseUrl;
	public final static String topicPrefixDdm;
	public final static String topicPrefixRam;

	SourcesConfig() {
		config = SpConfig.getSpConfig("pe/org.streampipes.pe.sources.hella");
		config.register(HOST, "sources-hella", "Hostname for the pe sources hella");
		config.register(PORT, 8090, "Port for the pe sources hella");
	}


	static {
    	serverUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort();
		iconBaseUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort() +"/img";
		topicPrefixDdm = "SEPA.SEP.DDM.";
		topicPrefixRam = "SEPA.SEP.Ram";
	}

    public String getHost() {
        return config.getString(HOST);
    }

    public int getPort() {
        return config.getInteger(PORT);
    }

}
