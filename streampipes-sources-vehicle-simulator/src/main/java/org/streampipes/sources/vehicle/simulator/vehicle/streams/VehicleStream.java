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

package org.streampipes.sources.vehicle.simulator.vehicle.streams;


import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.sources.vehicle.simulator.config.VehicleSimulatorConfig;
import org.streampipes.vocabulary.Geo;

public class VehicleStream extends AbstractAlreadyExistingStream {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("vehicle-position", "Vehicle Position", "An event stream " +
            "that produces current vehicle positions")
            .property(EpProperties.timestampProperty("timestamp"))
            .property(EpProperties.stringEp(Labels.from("plate-number", "Plate Number", "Denotes the " +
                    "plate number of the vehicle"), "plateNumber", "http://my.company/plateNumber"))
            .property(EpProperties.doubleEp(Labels.from("latitude", "Latitude", "Denotes the latitude " +
                    "value of the vehicle's position"), "latitude", Geo
                    .lat))
            .property(EpProperties.doubleEp(Labels.from("longitude", "Longitude", "Denotes the longitude " +
                    "value of the vehicle's position"), "longitude", Geo.lng))
            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(VehicleSimulatorConfig.INSTANCE.getKafkaHost(), VehicleSimulatorConfig.INSTANCE.getKafkaPort(),
                    "org.streampipes.examples.sources.vehicle"))
            .build();
  }
}
