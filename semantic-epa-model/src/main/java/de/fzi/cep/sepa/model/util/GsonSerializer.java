package de.fzi.cep.sepa.model.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.quality.*;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

public class GsonSerializer {

	public static Gson getGson()
	{
		GsonBuilder builder = new GsonBuilder();
//		builder.registerTypeAdapter(SepaDescription.class, new JsonLdSerializer());
		builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
		builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
		builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
		builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
		//builder.registerTypeAdapter(Operation.class, new JsonLdSerializer<Operation>());
		builder.setPrettyPrinting();
		return builder.create();
	}

	public static GsonBuilder getGsonBuilder()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
		builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
		builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
		builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
		//builder.registerTypeAdapter(MeasurementProperty.class, new JsonLdSerializer<MeasurementProperty>());
		builder.registerTypeAdapter(MappingProperty.class, new JsonLdSerializer<MappingProperty>());
		builder.registerTypeAdapter(EcType.class, new EcTypeAdapter());
		builder.registerTypeAdapter(EpaType.class, new EpaTypeAdapter());
        builder.registerTypeAdapter(Frequency.class, new JsonLdSerializer<Frequency>());
        builder.registerTypeAdapter(EventPropertyQualityDefinition.class, new JsonLdSerializer<EventPropertyQualityDefinition>());
        builder.registerTypeAdapter(EventStreamQualityDefinition.class, new JsonLdSerializer<EventStreamQualityDefinition>());
        //builder.registerTypeAdapter(EventStreamQualityRequirement.class, new JsonLdSerializer<EventStreamQualityRequirement>());
        //builder.registerTypeAdapter(EventPropertyQualityRequirement.class, new JsonLdSerializer<EventPropertyQualityRequirement>());


        builder.setPrettyPrinting();
		return builder;
	}
	
	public static Gson getGson(boolean keepIds) {
		return keepIds ? getGsonWithIds() : getGsonWithoutIds();
	}
	
	public static Gson getGsonWithIds() {
		return getGsonBuilder().create();
	}
	
	public static Gson getGsonWithoutIds() {
		GsonBuilder builder = getGsonBuilder();
		
		builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				if (f.getName().equals("elementName")) return true;
				return false;
			}
			
			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		return builder.create();
	}
		
}
