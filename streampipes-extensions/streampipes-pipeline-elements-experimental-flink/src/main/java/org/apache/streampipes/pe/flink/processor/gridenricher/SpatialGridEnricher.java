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

package org.apache.streampipes.pe.flink.processor.gridenricher;

import org.apache.streampipes.model.runtime.Event;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class SpatialGridEnricher implements FlatMapFunction<Event, Event> {

  private EnrichmentSettings settings;
  private SpatialGridCalculator calculator;

  public SpatialGridEnricher(EnrichmentSettings settings) {
    this.settings = settings;
    this.calculator = new SpatialGridCalculator(settings);
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws
      Exception {
    Double latitude = in.getFieldBySelector(settings.getLatPropertyName()).getAsPrimitive()
        .getAsDouble();
    Double longitude = in.getFieldBySelector(settings.getLngPropertyName()).getAsPrimitive().getAsDouble();

    CellOption result = calculator.computeCells(latitude, longitude);
//    System.out.println("x=" +result.getCellX() +", y=" +result.getCellY());

    out.collect(toOutput(in, result));
  }

  private Event toOutput(Event in, CellOption result) {
    in.addField(SpatialGridConstants.GRID_X_KEY, result.getCellX());
    in.addField(SpatialGridConstants.GRID_Y_KEY, result.getCellY());
    in.addField(SpatialGridConstants.GRID_CELLSIZE_KEY, result.getCellSize());
    in.addField(SpatialGridConstants.GRID_LAT_NW_KEY, result.getLatitudeNW());
    in.addField(SpatialGridConstants.GRID_LON_NW_KEY, result.getLongitudeNW());
    in.addField(SpatialGridConstants.GRID_LAT_SE_KEY, result.getLatitudeSE());
    in.addField(SpatialGridConstants.GRID_LON_SE_KEY, result.getLongitudeSE());

    return in;
  }

}
