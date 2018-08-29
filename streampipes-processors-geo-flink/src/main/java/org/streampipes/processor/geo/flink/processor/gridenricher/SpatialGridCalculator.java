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

package org.streampipes.processor.geo.flink.processor.gridenricher;

import java.io.Serializable;

public class SpatialGridCalculator implements Serializable {

  private double SOUTHDIFF = 0.004491556;
  private double EASTDIFF = 0.005986;

  private Integer cellSize;
  private Double startLat;
  private Double startLon;

  private EnrichmentSettings settings;

  public SpatialGridCalculator(EnrichmentSettings settings) {
    this.settings = settings;
    this.startLat = settings.getLatitudeStart();
    this.startLon = settings.getLongitudeStart();
    this.cellSize = settings.getCellSize();
    this.SOUTHDIFF = (cellSize / 500.0) * SOUTHDIFF;
    this.EASTDIFF = (cellSize / 500.0) * EASTDIFF;
  }

  public CellOption computeCells(double latitude, double longitude) {

    int cellX = calculateXCoordinate(longitude);
    int cellY = calculateYCoordinate(latitude);

    return new CellOption(cellX, cellY, startLat - (cellY * SOUTHDIFF),
            startLon + (cellX *
            EASTDIFF), startLat - ((cellY + 1) * SOUTHDIFF), startLon + (
                    (cellX + 1) * EASTDIFF),
            cellSize);
  }

  private int calculateXCoordinate(double longitude) {
    Double ret = (Math.abs(startLon - longitude) / EASTDIFF);
    return ret.intValue() + 1;
  }

  private int calculateYCoordinate(double latitude) {
    Double ret = (Math.abs(startLat - latitude) / SOUTHDIFF);
    return ret.intValue() + 1;
  }

}
