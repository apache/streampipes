package org.streampipes.pe.sinks.standalone.samples.demonstrator.ventil;


import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class DemonstratorVentilParameters extends EventSinkBindingParams {

	private String state;
	
	public DemonstratorVentilParameters(DataSinkInvocation graph, String state) {
		super(graph);
		this.state = state;
	}

	public String getState() {
		return state;
	}

}
