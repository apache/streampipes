package org.streampipes.pe.sinks.standalone.samples.demonstrator.pump;


import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class DemonstratorPumpParameters extends EventSinkBindingParams {

	private String state;
	
	public DemonstratorPumpParameters(DataSinkInvocation graph, String state) {
		super(graph);
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
