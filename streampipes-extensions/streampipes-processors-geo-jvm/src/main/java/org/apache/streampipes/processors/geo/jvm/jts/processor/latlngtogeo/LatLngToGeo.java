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
package org.apache.streampipes.processors.geo.jvm.jts.processor.latlngtogeo;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.geo.jvm.jts.helper.SpGeometryBuilder;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import org.locationtech.jts.geom.Point;


public class LatLngToGeo implements EventProcessor<LatLngToGeoParameter> {

  private static Logger log;
  private String latitude;
  private String longitude;
  private String epsgCode;


  @Override
  public void onInvocation(LatLngToGeoParameter params, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {

    log = params.getGraph().getLogger(LatLngToGeoParameter.class);
    this.latitude = params.getLat();
    this.longitude = params.getLng();
    this.epsgCode = params.getEpsg();

  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) {

    Double lat = in.getFieldBySelector(latitude).getAsPrimitive().getAsDouble();
    Double lng = in.getFieldBySelector(longitude).getAsPrimitive().getAsDouble();
    Integer epsg = in.getFieldBySelector(epsgCode).getAsPrimitive().getAsInt();

    Point geom = SpGeometryBuilder.createSPGeom(lng, lat, epsg);

    if (!geom.isEmpty()) {
      in.addField(LatLngToGeoController.WKT_RUNTIME, geom.toString());
      out.collect(in);
    } else {
      log.warn("An empty point geometry in " + LatLngToGeoController.EPA_NAME + " is created due"
          + "invalid input field. Latitude: " + lat + "Longitude: " + lng);
      log.error("Event is filtered out due invalid geometry");

    }
  }

  @Override
  public void onDetach() {

  }
}
