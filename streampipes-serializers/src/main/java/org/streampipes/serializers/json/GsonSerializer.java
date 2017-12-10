package org.streampipes.serializers.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.DataSinkType;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.quality.EventPropertyQualityDefinition;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.quality.Frequency;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.ValueSpecification;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;

public class GsonSerializer {

  public static Gson getGson() {
    GsonBuilder builder = new GsonBuilder();
//		builder.registerTypeAdapter(SepaDescription.class, new JsonLdSerializer());
    builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
    builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
    builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
    builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
    builder.registerTypeAdapter(ValueSpecification.class, new JsonLdSerializer<ValueSpecification>());
    //builder.registerTypeAdapter(Operation.class, new JsonLdSerializer<Operation>());
    builder.setPrettyPrinting();
    return builder.create();
  }

  public static GsonBuilder getGsonBuilder() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
    builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
    builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
    builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
    //builder.registerTypeAdapter(MeasurementProperty.class, new JsonLdSerializer<MeasurementProperty>());
    builder.registerTypeAdapter(MappingProperty.class, new JsonLdSerializer<MappingProperty>());
    builder.registerTypeAdapter(ValueSpecification.class, new JsonLdSerializer<ValueSpecification>());
    builder.registerTypeAdapter(DataSinkType.class, new EcTypeAdapter());
    builder.registerTypeAdapter(DataProcessorType.class, new EpaTypeAdapter());
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
        if (f.getName().equals("elementName")) {
          return true;
        }
        if (f.getName().equals("elementId")) {
          return true;
        }
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
