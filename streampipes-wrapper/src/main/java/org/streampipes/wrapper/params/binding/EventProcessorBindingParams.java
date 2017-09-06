package org.streampipes.wrapper.params.binding;

import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepaInvocation;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class EventProcessorBindingParams extends
				BindingParams<SepaInvocation> implements
				Serializable {

	private static final long serialVersionUID = 7716492945641719007L;

	private EventStream outputStream;
	private String outName;

	private final Map<String, Object> outEventType;
	
	private final static String topicPrefix = "topic://";
	
	public EventProcessorBindingParams(SepaInvocation graph)
	{
		super(new SepaInvocation(graph));
		this.outEventType = graph.getOutputStream().getEventSchema().toRuntimeMap();
		outputStream = graph.getOutputStream();
		EventGrounding outputGrounding = outputStream.getEventGrounding();
		outName = outputGrounding.getTransportProtocol().getTopicName();
		
	}

	public String getOutName()
	{
		return outName;
	}

	public Map<String, Object> getOutEventType() {
		return outEventType;
	}

	public List<String> getOutputProperties()
	{
		return outputStream.getEventSchema().toPropertyList();
	}
	
}
