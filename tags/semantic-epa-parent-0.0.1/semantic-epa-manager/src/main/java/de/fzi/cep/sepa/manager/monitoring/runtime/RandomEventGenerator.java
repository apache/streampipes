package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.List;
import java.util.Map;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;

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
