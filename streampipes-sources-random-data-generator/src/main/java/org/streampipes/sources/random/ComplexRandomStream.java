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

package org.streampipes.sources.random;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.helpers.ValueSpecifications;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.sources.config.SourcesConfig;
import org.streampipes.vocabulary.SO;

public class ComplexRandomStream implements DataStreamDeclarer {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("complex-stream", "Complex Stream", "Stream used for testing list-based " +
            "properties and " +
            "value " +
            "specifications")
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    "org.streampipes.test.complex"))
            .format(Formats.jsonFormat())
            .property(EpProperties.timestampProperty("timestamp"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testMeasurement")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testDimension")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(EpProperties.stringEp(Labels.withTitle("string", "string description"), "testString",
                    "http://test.de", ValueSpecifications.from("A", "B", "C")))
            .property(EpProperties.stringEp(Labels.withTitle("string2", "string description"), "testString2",
                    "http://test.de", ValueSpecifications.from("A", "B", "C", "D")))
            .property(EpProperties.integerEp(Labels.withTitle("integer2", "integerDescription"), "testInteger2",
                    SO.Number, ValueSpecifications.from(0.0f, 1.0f, 1.f)))
            .property(EpProperties.integerEp(Labels.withTitle("integer", "integerDescription"), "testInteger",
                    SO.Number, ValueSpecifications.from(10.0f, 100.0f, 10.0f)))
            .build();
  }

  @Override
  public void executeStream() {

  }

  @Override
  public boolean isExecutable() {
    return false;
  }
}
