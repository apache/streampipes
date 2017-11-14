package org.streampipes.wrapper.params.binding;

import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SchemaUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class EventProcessorBindingParams extends
				BindingParams<DataProcessorInvocation> implements
				Serializable {

	private static final long serialVersionUID = 7716492945641719007L;

	private SpDataStream outputStream;
	private String outName;

	private final Map<String, Object> outEventType;
	
	private final static String topicPrefix = "topic://";
	
	public EventProcessorBindingParams(DataProcessorInvocation graph)
	{
		super(new DataProcessorInvocation(graph));
		this.outEventType = SchemaUtils.toRuntimeMap(graph.getOutputStream().getEventSchema().getEventProperties());
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
		return SchemaUtils.toPropertyList(outputStream.getEventSchema().getEventProperties());
	}
	
}
