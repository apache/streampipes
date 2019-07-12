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
import org.streampipes.model.*;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.model.connect.rules.Schema.CreateNestedRuleDescription;
import org.streampipes.model.connect.rules.Schema.DeleteRuleDescription;
import org.streampipes.model.connect.rules.Schema.MoveRuleDescription;
import org.streampipes.model.connect.rules.Schema.RenameRuleDescription;
import org.streampipes.model.connect.rules.Stream.RemoveDuplicatesTransformationRuleDescription;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
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

  public static GsonBuilder getAdapterGsonBuilder() {
    GsonBuilder builder = getGsonBuilder();
    builder.registerTypeHierarchyAdapter(AdapterDescription.class, new AdapterSerializer());
    builder.registerTypeAdapter(TransformationRuleDescription.class, new JsonLdSerializer<TransformationRuleDescription>());
//    builder.registerTypeHierarchyAdapter(TransformationRuleDescription.class, new AdapterSerializer());

    return builder;
  }

  public static Gson getAdapterGson() {
    return getAdapterGsonBuilder().create();
  }

  public static Gson getGson() {
    return getGsonBuilder().create();
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
    builder.registerTypeAdapter(AdapterType.class, new AdapterTypeAdapter());
    builder.registerTypeAdapter(Message.class, new JsonLdSerializer<Message>());
    builder.registerTypeAdapter(DataProcessorType.class, new EpaTypeAdapter());
    builder.registerTypeAdapter(URI.class, new UriSerializer());
    builder.registerTypeAdapter(Frequency.class, new JsonLdSerializer<Frequency>());
    builder.registerTypeAdapter(EventPropertyQualityDefinition.class, new JsonLdSerializer<EventPropertyQualityDefinition>());
    builder.registerTypeAdapter(EventStreamQualityDefinition.class, new JsonLdSerializer<EventStreamQualityDefinition>());
    builder.registerTypeAdapter(TopicDefinition.class, new JsonLdSerializer<TopicDefinition>());
    builder.registerTypeAdapter(TransformationRuleDescription.class, new JsonLdSerializer<TransformationRuleDescription>());
    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(SpDataStream.class, "sourceType")
            .registerSubtype(SpDataSet.class, "org.streampipes.model.SpDataSet")
            .registerSubtype(SpDataStream.class, "org.streampipes.model.SpDataStream"));

    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(TransformationRuleDescription.class, "sourceType")
            .registerSubtype(RenameRuleDescription.class, "org.streampipes.model.RenameRuleDescription")
            .registerSubtype(MoveRuleDescription.class, "org.streampipes.model.MoveRuleDescription")
            .registerSubtype(DeleteRuleDescription.class, "org.streampipes.model.DeleteRuleDescription")
            .registerSubtype(CreateNestedRuleDescription.class, "org.streampipes.model.CreateNestedRuleDescription")
            .registerSubtype(RemoveDuplicatesTransformationRuleDescription.class, "org.streampipes.model.RemoveDuplicatesRuleDescription")
            .registerSubtype(AddTimestampRuleDescription.class, "org.streampipes.model.AddTimestampRuleDescription")
            .registerSubtype(AddValueTransformationRuleDescription.class, "org.streampipes.model.AddValueTransformationRuleDescription")
            .registerSubtype(UnitTransformRuleDescription.class, "org.streampipes.model.UnitTransformRuleDescription")
            .registerSubtype(TimestampTranfsformationRuleDescription.class, "org.streampipes.model.TimestampTranfsformationRuleDescription"));

    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(AdapterDescription.class, "sourceType")
            .registerSubtype(SpecificAdapterSetDescription.class, "org.streampipes.model.connect.adapter.SpecificAdapterSetDescription")
            .registerSubtype(SpecificAdapterStreamDescription.class, "org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription")
            .registerSubtype(GenericAdapterSetDescription.class, "org.streampipes.model.connect.adapter.GenericAdapterSetDescription")
            .registerSubtype(GenericAdapterStreamDescription.class, "org.streampipes.model.connect.adapter.GenericAdapterStreamDescription"));

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
