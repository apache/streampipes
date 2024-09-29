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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.config;

import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.List;

import org.apache.plc4x.java.api.metadata.Option;
import org.apache.plc4x.java.api.metadata.OptionMetadata;
import org.apache.plc4x.java.api.types.OptionType;

public class MetadataOptionGenerator {

  public MetadataOptionGenerator() {

  }

  public StaticProperty makeRuntimeResolvableMetadata(String labelId) {
    return makeMetadata(labelId, getRuntimeResolvableGroup(Plc4xLabels.REQUIRED_GROUP_TRANSPORT),
            getRuntimeResolvableGroup(Plc4xLabels.ADVANCED_GROUP_TRANSPORT));
  }

  public StaticProperty makeMetadata(String labelId, OptionMetadata optionMetadata) {
    return makeMetadata(labelId, getGroup(optionMetadata.getRequiredOptions(), Plc4xLabels.REQUIRED_GROUP_PROTOCOL),
            getGroup(optionMetadata.getOptions(), Plc4xLabels.ADVANCED_GROUP_PROTOCOL));
  }

  private StaticProperty makeMetadata(String labelId, StaticProperty requiredProperty,
          StaticProperty advancedProperty) {
    return StaticProperties.alternatives(Labels.withId(labelId),
            Alternatives.from(Labels.withId(Plc4xLabels.REQUIRED_OPTIONS), requiredProperty, true),
            Alternatives.from(Labels.withId(Plc4xLabels.ADVANCED_OPTIONS), advancedProperty));
  }

  private static StaticProperty getRuntimeResolvableGroup(String labelId) {
    return StaticProperties.runtimeResolvableGroupStaticProperty(Labels.withId(labelId),
            List.of(Plc4xLabels.SUPPORTED_TRANSPORTS));
  }

  private StaticProperty getGroup(List<Option> options, String labelId) {
    return StaticProperties.group(Labels.withId(labelId), false, getOptions(options));
  }

  public StaticProperty[] getOptions(List<Option> options) {
    return options.stream().map(option -> {
      var sp = StaticProperties.freeTextProperty(Labels.from(option.getKey(), option.getKey(), option.getDescription()),
              getDatatype(option.getType()));
      sp.setOptional(!option.isRequired());
      return sp;
    }).toArray(StaticProperty[]::new);
  }

  private Datatypes getDatatype(OptionType optionType) {
    switch (optionType) {
      case INT -> {
        return Datatypes.Integer;
      }
      case LONG -> {
        return Datatypes.Long;
      }
      case FLOAT -> {
        return Datatypes.Float;
      }
      case BOOLEAN -> {
        return Datatypes.Boolean;
      }
      default -> {
        return Datatypes.String;
      }
    }
  }
}
