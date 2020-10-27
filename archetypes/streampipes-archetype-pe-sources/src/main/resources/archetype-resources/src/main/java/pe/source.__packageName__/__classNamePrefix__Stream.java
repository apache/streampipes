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

#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )

package ${package}.pe.source.${packageName};

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataStreamBuilder;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Formats;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Protocols;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.sources.AbstractAdapterIncludedStream;
import org.apache.streampipes.vocabulary.SO;
import ${package}.config.Config;

public class ${classNamePrefix}Stream extends AbstractAdapterIncludedStream {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("${package}.pe.source.${packageName}")
          .withLocales(Locales.EN)
          .withAssets(Assets.DOCUMENTATION, Assets.ICON)
          .property(EpProperties.timestampProperty("timestamp"))

          // TODO configure your stream here, see following as example
          .property(PrimitivePropertyBuilder
              .create(Datatypes.String, "sensorId")
              .label("Sensor ID")
              .description("The ID of the sensor")
              .scope(PropertyScope.DIMENSION_PROPERTY)
          .build())
          .property(PrimitivePropertyBuilder
              .create(Datatypes.Float, "value")
              .label("Value")
              .description("Current value in the sensor")
              .domainProperty(SO.Number)
              .scope(PropertyScope.MEASUREMENT_PROPERTY)
          .build())

          .format(Formats.jsonFormat())
          // TODO change kafka topic
          .protocol(Protocols.kafka(Config.INSTANCE.getKafkaHost(), Config.INSTANCE.getKafkaPort(), "${package}.source.jvm"))
          .build();
  }

  @Override
  public void executeStream() {
    // TODO add logic here
  }
}
