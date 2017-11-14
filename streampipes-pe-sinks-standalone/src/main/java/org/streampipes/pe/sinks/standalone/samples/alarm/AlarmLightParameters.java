package org.streampipes.pe.sinks.standalone.samples.alarm;


import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class AlarmLightParameters extends EventSinkBindingParams {

	private String state;
	
	public AlarmLightParameters(DataSinkInvocation graph, String state) {
		super(graph);
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
