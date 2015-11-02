package de.fzi.cep.sepa.flink;

public class FlinkDeploymentConfig {

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
	
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}	
}
