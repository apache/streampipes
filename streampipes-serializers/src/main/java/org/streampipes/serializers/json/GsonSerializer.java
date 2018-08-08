/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.serializers.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.DataSinkType;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.connect.rules.*;
import org.streampipes.model.grounding.TopicDefinition;
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
    builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
    builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
    builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
    builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
    builder.registerTypeAdapter(ValueSpecification.class, new JsonLdSerializer<ValueSpecification>());
    builder.registerTypeAdapter(TopicDefinition.class, new JsonLdSerializer<TopicDefinition>());
    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(SpDataStream.class, "sourceType")
            .registerSubtype(SpDataSet.class, "org.streampipes.model.SpDataSet")
            .registerSubtype(SpDataStream.class, "org.streampipes.model.SpDataStream"));
    builder.setPrettyPrinting();
    return builder.create();
  }

  public static GsonBuilder getGsonBuilder() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(EventProperty.class, new JsonLdSerializer<EventProperty>());
    builder.registerTypeAdapter(StaticProperty.class, new JsonLdSerializer<StaticProperty>());
    builder.registerTypeAdapter(OutputStrategy.class, new JsonLdSerializer<OutputStrategy>());
    builder.registerTypeAdapter(TransportProtocol.class, new JsonLdSerializer<TransportProtocol>());
    builder.registerTypeAdapter(MappingProperty.class, new JsonLdSerializer<MappingProperty>());
    builder.registerTypeAdapter(ValueSpecification.class, new JsonLdSerializer<ValueSpecification>());
    builder.registerTypeAdapter(DataSinkType.class, new EcTypeAdapter());
    builder.registerTypeAdapter(DataProcessorType.class, new EpaTypeAdapter());
    builder.registerTypeAdapter(URI.class, new UriSerializer());
    builder.registerTypeAdapter(Frequency.class, new JsonLdSerializer<Frequency>());
    builder.registerTypeAdapter(EventPropertyQualityDefinition.class, new JsonLdSerializer<EventPropertyQualityDefinition>());
    builder.registerTypeAdapter(EventStreamQualityDefinition.class, new JsonLdSerializer<EventStreamQualityDefinition>());
    builder.registerTypeAdapter(TopicDefinition.class, new JsonLdSerializer<TopicDefinition>());
    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(SpDataStream.class, "sourceType")
            .registerSubtype(SpDataSet.class, "org.streampipes.model.SpDataSet")
            .registerSubtype(SpDataStream.class, "org.streampipes.model.SpDataStream"));
    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(TransformationRuleDescription.class, "sourceType")
            .registerSubtype(RenameRuleDescription.class, "org.streampipes.model.RenameRuleDescription")
            .registerSubtype(MoveRuleDescription.class, "org.streampipes.model.MoveRuleDescription")
            .registerSubtype(DeleteRuleDescription.class, "org.streampipes.model.DeleteRuleDescription")
            .registerSubtype(CreateNestedRuleDescription.class, "org.streampipes.model.CreateNestedRuleDescription"));

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
