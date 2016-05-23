package de.fzi.cep.sepa.implementations.stream.story.main;

public class ModelInvocationRequestParameters {
	private String pipelineId;
	private int modelId;
	private String zookeeperHost;
	private int zookeeperPort;
	private String inputTopic;
	private String kafkaHost;
	private int kafkaPort;
	private String outputTopic;

	public ModelInvocationRequestParameters(String pipelineId, int modelId, String zookeeperHost, int zookeeperPort,
			String inputTopic, String kafkaHost, int kafkaPort, String outputTopic) {
		super();
		this.pipelineId = pipelineId;
		this.modelId = modelId;
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.inputTopic = inputTopic;
		this.kafkaHost = kafkaHost;
		this.kafkaPort = kafkaPort;
		this.outputTopic = outputTopic;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getZookeeperHost() {
		return zookeeperHost;
	}

	public void setZookeeperHost(String zookeeperHost) {
		this.zookeeperHost = zookeeperHost;
	}

	public int getZookeeperPort() {
		return zookeeperPort;
	}

	public void setZookeeperPort(int zookeeperPort) {
		this.zookeeperPort = zookeeperPort;
	}

	public String getInputTopic() {
		return inputTopic;
	}

	public void setInputTopic(String inputTopic) {
		this.inputTopic = inputTopic;
	}

	public String getKafkaHost() {
		return kafkaHost;
	}

	public void setKafkaHost(String kafkaHost) {
		this.kafkaHost = kafkaHost;
	}

	public int getKafkaPort() {
		return kafkaPort;
	}

	public void setKafkaPort(int kafkaPort) {
		this.kafkaPort = kafkaPort;
	}

	public String getOutputTopic() {
		return outputTopic;
	}

	public void setOutputTopic(String outoutTopic) {
		this.outputTopic = outoutTopic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputTopic == null) ? 0 : inputTopic.hashCode());
		result = prime * result + ((kafkaHost == null) ? 0 : kafkaHost.hashCode());
		result = prime * result + kafkaPort;
		result = prime * result + modelId;
		result = prime * result + ((outputTopic == null) ? 0 : outputTopic.hashCode());
		result = prime * result + ((pipelineId == null) ? 0 : pipelineId.hashCode());
		result = prime * result + ((zookeeperHost == null) ? 0 : zookeeperHost.hashCode());
		result = prime * result + zookeeperPort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelInvocationRequestParameters other = (ModelInvocationRequestParameters) obj;
		if (inputTopic == null) {
			if (other.inputTopic != null)
				return false;
		} else if (!inputTopic.equals(other.inputTopic))
			return false;
		if (kafkaHost == null) {
			if (other.kafkaHost != null)
				return false;
		} else if (!kafkaHost.equals(other.kafkaHost))
			return false;
		if (kafkaPort != other.kafkaPort)
			return false;
		if (modelId != other.modelId)
			return false;
		if (outputTopic == null) {
			if (other.outputTopic != null)
				return false;
		} else if (!outputTopic.equals(other.outputTopic))
			return false;
		if (pipelineId == null) {
			if (other.pipelineId != null)
				return false;
		} else if (!pipelineId.equals(other.pipelineId))
			return false;
		if (zookeeperHost == null) {
			if (other.zookeeperHost != null)
				return false;
		} else if (!zookeeperHost.equals(other.zookeeperHost))
			return false;
		if (zookeeperPort != other.zookeeperPort)
			return false;
		return true;
	}
	
	
}