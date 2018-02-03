package org.streampipes.wrapper.flink;

import java.io.Serializable;

public class FlinkDeploymentConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private String jarFile;
	private String host;
	private int port;
	
	public FlinkDeploymentConfig(String jarFile, String host, int port) {
		super();
		this.jarFile = jarFile;
		this.host = host;
		this.port = port;
	}
	
	public String getJarFile() {
		return jarFile;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}

}
