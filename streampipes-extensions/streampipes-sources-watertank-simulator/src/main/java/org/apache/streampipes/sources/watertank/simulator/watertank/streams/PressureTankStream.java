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

package org.apache.streampipes.sources.watertank.simulator.watertank.streams;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataStreamBuilder;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Formats;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Protocols;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.sources.AbstractAlreadyExistingStream;
import org.apache.streampipes.sources.watertank.simulator.config.ConfigKeys;
import org.apache.streampipes.sources.watertank.simulator.vocabulary.WaterTankVocabulary;

public class PressureTankStream extends AbstractAlreadyExistingStream {

  @Override
  public SpDataStream declareModel() {
    return DataStreamBuilder.create("org.apache.streampipes.sources.simulator.pressure")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .property(EpProperties.timestampProperty("timestamp"))
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, "sensorId")
            .label("Sensor ID")
            .description("The ID of the sensor")
            .domainProperty(WaterTankVocabulary.HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "pressure")
            .label("Pressure")
            .description("Denotes the current pressure in the pressure tank")
            .domainProperty(WaterTankVocabulary.HAS_PRESSURE)
            .valueSpecification(0.0f, 100.0f, 0.5f)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .format(Formats.jsonFormat())
        .protocol(Protocols.kafka(
            configExtractor().getString(ConfigKeys.KAFKA_HOST),
            configExtractor().getInteger(ConfigKeys.KAFKA_PORT),
            "org.apache.streampipes.examples.pressuretank"))
        .build();
  }

}
