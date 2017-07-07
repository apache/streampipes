package org.streampipes.pe.sinks.standalone.samples;

public abstract class ActionParameters {

	protected String topic;
	protected String url;
	
	
	public ActionParameters(String topic, String url) {
		super();
		this.topic = topic;
		this.url = url;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
