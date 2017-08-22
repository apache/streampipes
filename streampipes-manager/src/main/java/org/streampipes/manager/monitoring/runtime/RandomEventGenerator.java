package org.streampipes.manager.monitoring.runtime;

import java.util.List;
import java.util.Map;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;

public class RandomEventGenerator extends EventGenerator {

	private RandomDataGenerator dataGenerator;
	
	public RandomEventGenerator(EventSchema schema,
			FormatGenerator formatGenerator) {
		super(schema, formatGenerator);
		this.dataGenerator = new RandomDataGenerator();
	}

	@Override
	protected Map<String, Object> makeNestedProperty(EventPropertyNested nested) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object makePrimitiveProperty(EventPropertyPrimitive primitive) {
		return dataGenerator.getValue(primitive);
	}

	@Override
	protected List<?> makeListProperty(EventPropertyList list) {
		// TODO Auto-generated method stub
		return null;
	}

}
