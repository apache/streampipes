package de.fzi.cep.sepa.runtime.param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public abstract class BindingParameters implements Serializable {

	private static final long serialVersionUID = 7716492945641719007L;

	protected SepaInvocation graph;
	
	private List<InputStreamParameters> inputStreamParams = new ArrayList<>();
	
	private EventGrounding outputGrounding;
	private EventStream outputStream;
	private String outName;
	
	private static String topicPrefix = "topic://";
	
	public BindingParameters(SepaInvocation graph)
	{
		this.graph = graph;
		
		graph.getInputStreams().forEach(s -> inputStreamParams.add(new InputStreamParameters(s)));

		outputStream = graph.getOutputStream();
		outputGrounding = outputStream.getEventGrounding();
		outName = topicPrefix + outputGrounding.getTransportProtocol().getTopicName();
		
	}

	public SepaInvocation getGraph() {
		return graph;
	}
	
	public void setGraph(SepaInvocation invocation) {
		this.graph = graph;
	}

	public String getOutName()
	{
		return outName;
	}

	public List<InputStreamParameters> getInputStreamParams() {
		return inputStreamParams;
	}
	
	public List<String> getOutputProperties()
	{
		return outputStream.getEventSchema().toPropertyList();
	}
	
}
