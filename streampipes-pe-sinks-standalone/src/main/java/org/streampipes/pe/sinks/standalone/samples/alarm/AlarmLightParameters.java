package org.streampipes.pe.sinks.standalone.samples.alarm;


import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class AlarmLightParameters extends EventSinkBindingParams {

	private String state;
	
	public AlarmLightParameters(SecInvocation graph, String state) {
		super(graph);
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
