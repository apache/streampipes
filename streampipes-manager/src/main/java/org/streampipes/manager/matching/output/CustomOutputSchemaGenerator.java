package org.streampipes.manager.matching.output;

import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.output.CustomOutputStrategy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CustomOutputSchemaGenerator implements OutputSchemaGenerator<CustomOutputStrategy> {

	private List<EventProperty> customProperties;
	
	public CustomOutputSchemaGenerator(List<EventProperty> customProperties) {
		this.customProperties = rewrite(customProperties);
	}
	
	private List<EventProperty> rewrite(List<EventProperty> customProperties2) {
		List<EventProperty> newCustomProperties = new ArrayList<>();
		for(int i = 0; i < customProperties2.size(); i++) {
			if (customProperties2.get(i) instanceof EventPropertyPrimitive) {
				EventPropertyPrimitive prop = (EventPropertyPrimitive) customProperties2.get(i);
				if (newCustomProperties.stream().anyMatch(nc -> nc.getRuntimeName().equals(prop.getRuntimeName()))) {
					EventPropertyPrimitive newp = new EventPropertyPrimitive(prop);
					newp.setRuntimeName(prop.getRuntimeName() +"1");
					newp.setRdfId(new SupportsRdfId.URIKey(URI.create(prop.getElementId() +"1")));
					newCustomProperties.add(newp);
				}
				else
					newCustomProperties.add(prop);
			} 
			else
				newCustomProperties.add(customProperties2.get(i));
		}
		
		return newCustomProperties;
	}

	@Override
	public EventSchema buildFromOneStream(EventStream stream) {
		return new EventSchema(customProperties);
	}

	@Override
	public EventSchema buildFromTwoStreams(EventStream stream1,
			EventStream stream2) {
		return buildFromOneStream(stream1);
	}

	@Override
	public CustomOutputStrategy getModifiedOutputStrategy(
			CustomOutputStrategy strategy) {
		return strategy;
	}
}
