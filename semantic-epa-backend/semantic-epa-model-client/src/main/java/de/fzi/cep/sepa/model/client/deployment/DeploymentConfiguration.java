package de.fzi.cep.sepa.model.client.deployment;

public class DeploymentConfiguration {

	private String groupId;
	private String artifactId;
	private String classNamePrefix;
	
	private String elementId;
	private boolean draft;
	
	private int port;
	
	private DeploymentType deploymentType;
	private DeploymentMethod deploymentMethod;

	
	public DeploymentConfiguration(String groupId, String artifactId,
			String classNamePrefix, int port) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.classNamePrefix = classNamePrefix;
		this.port = port;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getClassNamePrefix() {
		return classNamePrefix;
	}

	public void setClassNamePrefix(String classNamePrefix) {
		this.classNamePrefix = classNamePrefix;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public DeploymentType getDeploymentType() {
		return deploymentType;
	}

	public void setDeploymentType(DeploymentType deploymentType) {
		this.deploymentType = deploymentType;
	}

	public DeploymentMethod getDeploymentMethod() {
		return deploymentMethod;
	}

	public void setDeploymentMethod(DeploymentMethod deploymentMethod) {
		this.deploymentMethod = deploymentMethod;
	}
		
}
