/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.storage.couchdb.serializer;

import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.ValueSpecification;
import org.apache.streampipes.model.staticproperty.MappingProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;

public class GsonSerializer {

  public static GsonBuilder getAdapterGsonBuilder() {
    GsonBuilder builder = getGsonBuilder();
    builder.registerTypeHierarchyAdapter(AdapterDescription.class, new AdapterSerializer());
    builder.registerTypeAdapter(TransformationRuleDescription.class,
        new JsonLdSerializer<TransformationRuleDescription>());
//    builder.registerTypeHierarchyAdapter(TransformationRuleDescription.class, new AdapterSerializer());

    return builder;
  }

  public static GsonBuilder getPrincipalGsonBuilder() {
    GsonBuilder builder = getGsonBuilder();
    builder.registerTypeHierarchyAdapter(Principal.class, new PrincipalDeserializer());

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
    builder.registerTypeAdapter(TopicDefinition.class, new JsonLdSerializer<TopicDefinition>());
    builder.registerTypeAdapter(TransformationRuleDescription.class,
        new JsonLdSerializer<TransformationRuleDescription>());
    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(SpDataStream.class, "sourceType")
        .registerSubtype(SpDataStream.class, "org.apache.streampipes.model.SpDataStream"));

    builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(TransformationRuleDescription.class, "sourceType")
        .registerSubtype(RenameRuleDescription.class, "org.apache.streampipes.model.RenameRuleDescription")
        .registerSubtype(MoveRuleDescription.class, "org.apache.streampipes.model.MoveRuleDescription")
        .registerSubtype(DeleteRuleDescription.class, "org.apache.streampipes.model.DeleteRuleDescription")
        .registerSubtype(CreateNestedRuleDescription.class, "org.apache.streampipes.model.CreateNestedRuleDescription")
        .registerSubtype(RemoveDuplicatesTransformationRuleDescription.class,
            "org.apache.streampipes.model.RemoveDuplicatesRuleDescription")
        .registerSubtype(AddTimestampRuleDescription.class, "org.apache.streampipes.model.AddTimestampRuleDescription")
        .registerSubtype(AddValueTransformationRuleDescription.class,
            "org.apache.streampipes.model.AddValueTransformationRuleDescription")
        .registerSubtype(UnitTransformRuleDescription.class,
            "org.apache.streampipes.model.UnitTransformRuleDescription")
        .registerSubtype(TimestampTranfsformationRuleDescription.class,
            "org.apache.streampipes.model.TimestampTranfsformationRuleDescription")
        .registerSubtype(EventRateTransformationRuleDescription.class,
            "org.apache.streampipes.model.EventRateTransformationRuleDescription")
        .registerSubtype(ChangeDatatypeTransformationRuleDescription.class,
            "org.apache.streampipes.model.ChangeDatatypeTransformationRuleDescription")
        .registerSubtype(CorrectionValueTransformationRuleDescription.class,
            "org.apache.streampipes.model.CorrectionValueTransformationRuleDescription"));

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
