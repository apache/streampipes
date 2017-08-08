package org.streampipes.pe.processors.esper.pattern.streamstopped;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

public class StreamStoppedParameter extends BindingParameters {

	private String topic;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StreamStoppedParameter(SepaInvocation graph, String topic) {
		super(graph);
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

}
