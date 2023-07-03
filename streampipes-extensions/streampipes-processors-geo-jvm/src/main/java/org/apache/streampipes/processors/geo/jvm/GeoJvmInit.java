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

package org.apache.streampipes.processors.geo.jvm;

import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.extensions.management.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.processors.geo.jvm.config.ConfigKeys;
import org.apache.streampipes.processors.geo.jvm.jts.processor.buffergeometry.BufferGeomProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.bufferpoint.BufferPointProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.epsg.EpsgProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.latlngtojtspoint.LatLngToJtsPointProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.reprojection.ReprojectionProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.trajectory.TrajectoryFromPointsProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.validation.complex.TopologyValidationProcessor;
import org.apache.streampipes.processors.geo.jvm.jts.processor.validation.simple.GeometryValidationProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversine.HaversineDistanceCalculatorProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.distancecalculator.haversinestatic.HaversineStaticDistanceCalculatorProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.geocoder.googlemaps.GoogleMapsGeocoderProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.geocoder.googlemapsstatic.GoogleMapsStaticGeocoderProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.GeoCityNameRevdecodeProcessor;
import org.apache.streampipes.processors.geo.jvm.latlong.processor.speedcalculator.SpeedCalculatorProcessor;
import org.apache.streampipes.service.extensions.ExtensionsModelSubmitter;

import org.apache.sis.setup.Configuration;
import org.postgresql.ds.PGSimpleDataSource;

public class GeoJvmInit extends ExtensionsModelSubmitter {


  @Override
  public SpServiceDefinition provideServiceDefinition() {


    try {
      Configuration.current().setDatabase(GeoJvmInit::createDataSource);
    } catch (IllegalStateException e) {
      // catch the exceptions due connection is already initialized.
    }

    return SpServiceDefinitionBuilder.create("org.apache.streampipes.processors.geo.jvm",
            "Processors Geo JVM",
            "",
            8090)
        .registerPipelineElements(
            new HaversineDistanceCalculatorProcessor(),
            new HaversineStaticDistanceCalculatorProcessor(),
            new GoogleMapsGeocoderProcessor(),
            new GoogleMapsStaticGeocoderProcessor(),
            new GeoCityNameRevdecodeProcessor(),
            new EpsgProcessor(),
            new LatLngToJtsPointProcessor(),
            new TrajectoryFromPointsProcessor(),
            new SpeedCalculatorProcessor(),
            new ReprojectionProcessor(),
            new GeometryValidationProcessor(),
            new TopologyValidationProcessor(),
            new BufferGeomProcessor(),
            new BufferPointProcessor())
        .registerMessagingFormats(
            new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory())
        .registerMessagingProtocols(
            new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpNatsProtocolFactory())
        .addConfig(ConfigKeys.GOOGLE_API_KEY, "", "Google Maps API key")
        .build();
  }

  // https://sis.apache.org/apidocs/org/apache/sis/setup/Configuration.html#setDatabase(java.util.function.Supplier)
  protected static PGSimpleDataSource createDataSource() {
    PGSimpleDataSource ds = new PGSimpleDataSource();
    String[] serverAddresses = {"localhost"};
    ds.setServerNames(serverAddresses);
    int[] portNumbers = {54320};
    ds.setPortNumbers(portNumbers);
    ds.setDatabaseName("EPSG");
    ds.setUser("streampipes");
    ds.setPassword("streampipes");
    ds.setReadOnly(true);

    return ds;
  }

}
