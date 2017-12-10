package org.streampipes.pe.processors.esper.pattern.streamstopped;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class StreamStoppedParameter extends EventProcessorBindingParams {

	private String topic;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StreamStoppedParameter(DataProcessorInvocation graph, String topic) {
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
