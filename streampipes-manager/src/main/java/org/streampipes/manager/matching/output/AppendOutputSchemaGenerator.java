package org.streampipes.manager.matching.output;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.output.AppendOutputStrategy;

public class AppendOutputSchemaGenerator implements OutputSchemaGenerator<AppendOutputStrategy> {

	private List<EventProperty> appendProperties;
	private List<EventProperty> properties;
	private List<EventProperty> renamedProperties;
	
	public AppendOutputSchemaGenerator(List<EventProperty> appendProperties) {
		this.appendProperties = appendProperties;
		this.properties = new ArrayList<>();
		this.renamedProperties = new ArrayList<>();
	}
	
	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		properties.addAll(stream.getEventSchema().getEventProperties());
		properties.addAll(renameDuplicates(stream.getEventSchema().getEventProperties()));
		return new EventSchema(properties);
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		properties.addAll(renameDuplicates(stream1.getEventSchema().getEventProperties()));
		properties.addAll(renameDuplicates(stream2.getEventSchema().getEventProperties()));
		return new EventSchema(properties);
	}
	
	private List<EventProperty> renameDuplicates(List<EventProperty> oldProperties)
	{
		List<EventProperty> renamed = new PropertyDuplicateRemover(oldProperties, appendProperties).rename();
		this.renamedProperties.addAll(renamed);
		return renamed;
	}

	@Override
	public AppendOutputStrategy getModifiedOutputStrategy(
			AppendOutputStrategy strategy) {
		strategy.setEventProperties(renamedProperties);
		return strategy;
	}

	
}
