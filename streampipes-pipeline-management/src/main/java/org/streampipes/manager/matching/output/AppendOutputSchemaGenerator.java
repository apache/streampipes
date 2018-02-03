package org.streampipes.manager.matching.output;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.AppendOutputStrategy;

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
	public EventSchema buildFromOneStream(SpDataStream stream) {
		properties.addAll(stream.getEventSchema().getEventProperties());
		properties.addAll(renameDuplicates(stream.getEventSchema().getEventProperties()));
		return new EventSchema(properties);
	}

	@Override
	public EventSchema buildFromTwoStreams(SpDataStream stream1,
			SpDataStream stream2) {
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
