/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sources.watertank.simulator.watertank.streams;

import org.streampipes.examples.sources.config.ExampleSourcesConfig;
import org.streampipes.examples.sources.vocabulary.WaterTankVocabulary;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.sources.AbstractAlreadyExistingStream;

public class WaterLevel2Stream extends AbstractAlreadyExistingStream {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		return DataStreamBuilder.create("water-level-2", "Water Level 2", "")
						.iconUrl(ExampleSourcesConfig.iconBaseUrl + "/icon-water-level.png")
						.property(EpProperties.timestampProperty("timestamp"))
						.property(PrimitivePropertyBuilder
										.create(Datatypes.String, "sensorId")
										.label("Sensor ID")
										.description("The ID of the sensor")
										.domainProperty(WaterTankVocabulary.HAS_SENSOR_ID)
										.scope(PropertyScope.DIMENSION_PROPERTY)
										.build())
						.property(PrimitivePropertyBuilder
										.create(Datatypes.Float, "level")
										.label("Water Level")
										.description("Denotes the current water level in the container")
										.domainProperty(WaterTankVocabulary.HAS_WATER_LEVEL)
										.scope(PropertyScope.MEASUREMENT_PROPERTY)
										.build())
						.property(PrimitivePropertyBuilder
										.create(Datatypes.Boolean, "underflow")
										.label("Underflow")
										.description("Indicates whether the tank underflows")
										.domainProperty(WaterTankVocabulary.IS_UNDERFLOW)
										.scope(PropertyScope.MEASUREMENT_PROPERTY)
										.build())
						.format(Formats.jsonFormat())
						.protocol(Protocols.kafka(ExampleSourcesConfig.INSTANCE.getKafkaHost(), ExampleSourcesConfig.INSTANCE.getKafkaPort(),
										"org.streampipes.examples.waterlevel2"))
						.build();
	}
}