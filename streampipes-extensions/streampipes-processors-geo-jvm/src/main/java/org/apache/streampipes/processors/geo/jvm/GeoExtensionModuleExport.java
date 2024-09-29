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

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IExtensionModuleExport;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;
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

import java.util.Collections;
import java.util.List;

public class GeoExtensionModuleExport implements IExtensionModuleExport {
  @Override
  public List<StreamPipesAdapter> adapters() {
    return Collections.emptyList();
  }

  @Override
  public List<IStreamPipesPipelineElement<?>> pipelineElements() {
    return List.of(new HaversineDistanceCalculatorProcessor(), new HaversineStaticDistanceCalculatorProcessor(),
            new GoogleMapsGeocoderProcessor(), new GoogleMapsStaticGeocoderProcessor(),
            new GeoCityNameRevdecodeProcessor(), new EpsgProcessor(), new LatLngToJtsPointProcessor(),
            new TrajectoryFromPointsProcessor(), new SpeedCalculatorProcessor(), new ReprojectionProcessor(),
            new GeometryValidationProcessor(), new TopologyValidationProcessor(), new BufferGeomProcessor(),
            new BufferPointProcessor());
  }

  @Override
  public List<IModelMigrator<?, ?>> migrators() {
    return Collections.emptyList();
  }

  @Override
  public List<ConfigItem> configItems() {
    return List.of(ConfigItem.from(ConfigKeys.GOOGLE_API_KEY, "", "Google Maps API key"));
  }
}
