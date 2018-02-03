/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.sources.samples.benni;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sdk.utils.Datatypes;

public class BenniStream implements EventStreamDeclarer {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("benni-stream", "Benni Stream", "")
            .property(EpProperties.timestampProperty("time"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.String, "mac")
                    .label("Mac Address")
                    .description("The mac address of the sensor of the sensor")
                    .domainProperty("http://schema.org/mac")
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "light")
                    .label("Light")
                    .description("Denotes the light value in the sensor")
                    .domainProperty("http://schema.org/light")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "battery")
                    .label("Battery")
                    .description("Denotes the battery value in the sensor")
                    .domainProperty("http://schema.org/battery")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "conductivity")
                    .label("Conductivity")
                    .description("Denotes the conductivity value of the sensor")
                    .domainProperty("http://schema.org/conductivity")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "moisture")
                    .label("Moisture")
                    .description("Denotes the moisture value of the sensor")
                    .domainProperty("http://schema.org/moisture")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "temperature")
                    .label("Temperature")
                    .description("Denotes the temperature value of the sensor")
                    .domainProperty("http://schema.org/temperature")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(SourcesConfig.INSTANCE.getKafkaHost(), SourcesConfig.INSTANCE.getKafkaPort(),
                    "org.streampipes.benni"))
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
