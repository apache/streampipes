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

package org.apache.streampipes.sources.vehicle.simulator.vehicle.streams;


import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.sdk.builder.DataStreamBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Formats;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Protocols;
import org.apache.streampipes.sources.AbstractAdapterIncludedStream;
import org.apache.streampipes.sources.vehicle.simulator.config.ConfigKeys;
import org.apache.streampipes.sources.vehicle.simulator.simulator.VehicleDataSimulator;
import org.apache.streampipes.vocabulary.Geo;

public class VehicleStream extends AbstractAdapterIncludedStream {

  @Override
  public SpDataStream declareModel() {
    return DataStreamBuilder.create("vehicle-position", "Vehicle Position", "An event stream "
            + "that produces current vehicle positions")
        .property(EpProperties.timestampProperty("timestamp"))
        .property(EpProperties.stringEp(Labels.from("plate-number", "Plate Number", "Denotes the "
            + "plate number of the vehicle"), "plateNumber", "http://my.company/plateNumber"))
        .property(EpProperties.doubleEp(Labels.from("latitude", "Latitude", "Denotes the latitude "
            + "value of the vehicle's position"), "latitude", Geo
            .LAT))
        .property(EpProperties.doubleEp(Labels.from("longitude", "Longitude", "Denotes the longitude "
            + "value of the vehicle's position"), "longitude", Geo.LNG))
        .format(Formats.jsonFormat())
        .protocol(Protocols.kafka(
            configExtractor().getString(ConfigKeys.KAFKA_HOST),
            configExtractor().getInteger(ConfigKeys.KAFKA_PORT),
            "org.apache.streampipes.examples.sources.vehicle"))
        .build();
  }

  @Override
  public void executeStream() {
    Thread thread = new Thread(new VehicleDataSimulator());
    thread.start();
  }
}
