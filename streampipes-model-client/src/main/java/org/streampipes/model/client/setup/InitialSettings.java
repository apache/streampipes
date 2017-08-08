package org.streampipes.model.client.setup;

public class InitialSettings {


	private String adminEmail;
	private String adminPassword;
	private String couchDbHost;
	private String sesameHost;
	private String kafkaHost;
	private String zookeeperHost;
	private String jmsHost;

	public InitialSettings(String adminEmail, String adminPassword, String couchDbHost, String sesameHost, String kafkaHost, String zookeeperHost, String jmsHost) {
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		this.couchDbHost = couchDbHost;
		this.sesameHost = sesameHost;
		this.kafkaHost = kafkaHost;
		this.zookeeperHost = zookeeperHost;
		this.jmsHost = jmsHost;
	}

	public InitialSettings() {
		// TODO Auto-generated constructor stub
	}

	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getCouchDbHost() {
		return couchDbHost;
	}

	public void setCouchDbHost(String couchDbHost) {
		this.couchDbHost = couchDbHost;
    }

	public String getSesameHost() {
		return sesameHost;
	}

	public void setSesameHost(String sesameHost) {
		this.sesameHost = sesameHost;
	}

	public String getKafkaHost() {
		return kafkaHost;
	}

	public void setKafkaHost(String kafkaHost) {
		this.kafkaHost = kafkaHost;
	}

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public String getJmsHost() {
		return jmsHost;
	}

	public void setJmsHost(String jmsHost) {
		this.jmsHost = jmsHost;
	}
}
