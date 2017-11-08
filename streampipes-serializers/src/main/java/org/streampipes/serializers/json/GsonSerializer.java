package org.streampipes.serializers.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.quality.EventPropertyQualityDefinition;
import org.streampipes.model.impl.quality.EventStreamQualityDefinition;
import org.streampipes.model.impl.quality.Frequency;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;

import java.net.URI;

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
		builder.registerTypeAdapter(URI.class, new UriSerializer());
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
                if (f.getName().equals("elementId")) return true;
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
