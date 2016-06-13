package de.fzi.cep.sepa.model.client.deployment;

public class DeploymentConfiguration {

	private String groupId;
	private String artifactId;
	private String classNamePrefix;
	
	private String elementId;
	private boolean draft;
	
	private int port;
	
	private DeploymentType deploymentType;
	private ElementType elementType;
	private OutputType outputType;

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

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}
				
}
