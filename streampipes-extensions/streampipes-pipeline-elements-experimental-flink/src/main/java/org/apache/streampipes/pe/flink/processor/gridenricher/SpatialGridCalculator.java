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

import java.io.Serializable;

public class SpatialGridCalculator implements Serializable {

  private double southdiff = 0.004491556;
  private double eastdiff = 0.005986;

  private Integer cellSize;
  private Double startLat;
  private Double startLon;

  private EnrichmentSettings settings;

  public SpatialGridCalculator(EnrichmentSettings settings) {
    this.settings = settings;
    this.startLat = settings.getLatitudeStart();
    this.startLon = settings.getLongitudeStart();
    this.cellSize = settings.getCellSize();
    this.southdiff = (cellSize / 500.0) * southdiff;
    this.eastdiff = (cellSize / 500.0) * eastdiff;
  }

  public CellOption computeCells(double latitude, double longitude) {

    int cellX = calculateXCoordinate(longitude);
    int cellY = calculateYCoordinate(latitude);

    return new CellOption(cellX, cellY, startLat - (cellY * southdiff),
        startLon + (cellX * eastdiff), startLat - ((cellY + 1) * southdiff), startLon + (
        (cellX + 1) * eastdiff),
        cellSize);
  }

  private int calculateXCoordinate(double longitude) {
    Double ret = (Math.abs(startLon - longitude) / eastdiff);
    return ret.intValue() + 1;
  }

  private int calculateYCoordinate(double latitude) {
    Double ret = (Math.abs(startLat - latitude) / southdiff);
    return ret.intValue() + 1;
  }

}
