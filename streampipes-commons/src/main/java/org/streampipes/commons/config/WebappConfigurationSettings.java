package org.streampipes.commons.config;

import java.util.List;

public class WebappConfigurationSettings {

	private String couchDbProtocol;
	private String couchDbHost;
	private int couchDbPort;
	
	private String sesameUrl;
	private String sesameDbName;
	
	private String adminEmail;
	private String adminUserName;
	private String adminPassword;
	
	private String hippoUrl;
	private String panddaUrl;
	private String streamStoryUrl;
	private String humanInspectionReportUrl;
	private String humanMaintenanceReportUrl;
	
	private String kafkaProtocol;
	private int kafkaPort;
	private String kafkaHost;
	
	private String jmsProtocol;
	private int jmsPort;
	private String jmsHost;
	
	private String zookeeperProtocol;
	private int zookeeperPort;
	private String zookeeperHost;
	
	private String couchDbUserDbName;
	private String couchDbPipelineDbName;
	private String couchDbConnectionDbName;
	private String couchDbMonitoringDbName;
	private String couchDbNotificationDbName;
	
	private String appConfig;
	private String marketplaceUrl;
	private List<String> podUrls;
	
	
	
	public WebappConfigurationSettings(String couchDbHost, String sesameUrl, String sesameDbName,
			String adminEmail, String adminUserName, String adminPassword) {
		super();
		this.couchDbHost = couchDbHost;
		this.sesameUrl = sesameUrl;
		this.sesameDbName = sesameDbName;
		this.adminEmail = adminEmail;
		this.adminUserName = adminUserName;
		this.adminPassword = adminPassword;
	}
	
	public WebappConfigurationSettings() {
		// TODO Auto-generated constructor stub
	}

	public String getSesameUrl() {
		return sesameUrl;
	}
	public void setSesameUrl(String sesameUrl) {
		this.sesameUrl = sesameUrl;
	}
	public String getAdminUserName() {
		return adminUserName;
	}
	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
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

	public String getHippoUrl() {
		return hippoUrl;
	}

	public void setHippoUrl(String hippoUrl) {
		this.hippoUrl = hippoUrl;
	}

	public String getPanddaUrl() {
		return panddaUrl;
	}

	public void setPanddaUrl(String panddaUrl) {
		this.panddaUrl = panddaUrl;
	}

	public String getStreamStoryUrl() {
		return streamStoryUrl;
	}

	public void setStreamStoryUrl(String streamStoryUrl) {
		this.streamStoryUrl = streamStoryUrl;
	}

	public String getSesameDbName() {
		return sesameDbName;
	}

	public void setSesameDbName(String sesameDbName) {
		this.sesameDbName = sesameDbName;
	}

	public String getCouchDbProtocol() {
		return couchDbProtocol;
	}

	public void setCouchDbProtocol(String couchDbProtocol) {
		this.couchDbProtocol = couchDbProtocol;
	}

	public String getCouchDbHost() {
		return couchDbHost;
	}

	public void setCouchDbHost(String couchDbHost) {
		this.couchDbHost = couchDbHost;
	}

	
	public int getCouchDbPort() {
		return couchDbPort;
	}

	public void setCouchDbPort(int couchDbPort) {
		this.couchDbPort = couchDbPort;
	}

	public String getKafkaProtocol() {
		return kafkaProtocol;
	}

	public void setKafkaProtocol(String kafkaProtocol) {
		this.kafkaProtocol = kafkaProtocol;
	}

	public int getKafkaPort() {
		return kafkaPort;
	}

	public void setKafkaPort(int kafkaPort) {
		this.kafkaPort = kafkaPort;
	}

	public String getKafkaHost() {
		return kafkaHost;
	}

	public void setKafkaHost(String kafkaHost) {
		this.kafkaHost = kafkaHost;
	}

	public String getJmsProtocol() {
		return jmsProtocol;
	}

	public void setJmsProtocol(String jmsProtocol) {
		this.jmsProtocol = jmsProtocol;
	}

	public int getJmsPort() {
		return jmsPort;
	}

	public void setJmsPort(int jmsPort) {
		this.jmsPort = jmsPort;
	}

	public String getJmsHost() {
		return jmsHost;
	}

	public void setJmsHost(String jmsHost) {
		this.jmsHost = jmsHost;
	}

	public String getZookeeperProtocol() {
		return zookeeperProtocol;
	}

	public void setZookeeperProtocol(String zookeeperProtocol) {
		this.zookeeperProtocol = zookeeperProtocol;
	}

	public int getZookeeperPort() {
		return zookeeperPort;
	}

	public void setZookeeperPort(int zookeeperPort) {
		this.zookeeperPort = zookeeperPort;
	}

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public String getCouchDbUserDbName() {
		return couchDbUserDbName;
	}

	public void setCouchDbUserDbName(String couchDbUserDbName) {
		this.couchDbUserDbName = couchDbUserDbName;
	}

	public String getCouchDbPipelineDbName() {
		return couchDbPipelineDbName;
	}

	public void setCouchDbPipelineDbName(String couchDbPipelineDbName) {
		this.couchDbPipelineDbName = couchDbPipelineDbName;
	}

	public String getCouchDbConnectionDbName() {
		return couchDbConnectionDbName;
	}

	public void setCouchDbConnectionDbName(String couchDbConnectionDbName) {
		this.couchDbConnectionDbName = couchDbConnectionDbName;
	}

	public String getCouchDbMonitoringDbName() {
		return couchDbMonitoringDbName;
	}

	public void setCouchDbMonitoringDbName(String couchDbMonitoringDbName) {
		this.couchDbMonitoringDbName = couchDbMonitoringDbName;
	}

	public String getCouchDbNotificationDbName() {
		return couchDbNotificationDbName;
	}

	public void setCouchDbNotificationDbName(String couchDbNotificationDbName) {
		this.couchDbNotificationDbName = couchDbNotificationDbName;
	}

	public String getHumanInspectionReportUrl() {
		return humanInspectionReportUrl;
	}

	public void setHumanInspectionReportUrl(String humanInspectionReportUrl) {
		this.humanInspectionReportUrl = humanInspectionReportUrl;
	}

	public String getHumanMaintenanceReportUrl() {
		return humanMaintenanceReportUrl;
	}

	public void setHumanMaintenanceReportUrl(String humanMaintenanceReportUrl) {
		this.humanMaintenanceReportUrl = humanMaintenanceReportUrl;
	}

	public String getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(String appConfig) {
		this.appConfig = appConfig;
	}

	public String getMarketplaceUrl() {
		return marketplaceUrl;
	}

	public void setMarketplaceUrl(String marketplaceUrl) {
		this.marketplaceUrl = marketplaceUrl;
	}

	public List<String> getPodUrls() {
		return podUrls;
	}

	public void setPodUrls(List<String> podUrls) {
		this.podUrls = podUrls;
	}
	
	
}
