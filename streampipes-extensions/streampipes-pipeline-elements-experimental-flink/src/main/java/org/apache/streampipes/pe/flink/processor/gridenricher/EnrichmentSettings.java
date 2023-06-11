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

public class EnrichmentSettings implements Serializable {

  private double latitudeStart;
  private double longitudeStart;

  private int cellSize;

  private String latPropertyName;
  private String lngPropertyName;

  public EnrichmentSettings() {
  }

  public EnrichmentSettings(double latitudeStart, double longitudeStart, int cellSize, String latPropertyName,
                            String lngPropertyName) {
    this.latitudeStart = latitudeStart;
    this.longitudeStart = longitudeStart;
    this.cellSize = cellSize;
    this.latPropertyName = latPropertyName;
    this.lngPropertyName = lngPropertyName;
  }

  public double getLatitudeStart() {
    return latitudeStart;
  }

  public void setLatitudeStart(double latitudeStart) {
    this.latitudeStart = latitudeStart;
  }

  public double getLongitudeStart() {
    return longitudeStart;
  }

  public void setLongitudeStart(double longitudeStart) {
    this.longitudeStart = longitudeStart;
  }

  public int getCellSize() {
    return cellSize;
  }

  public void setCellSize(int cellSize) {
    this.cellSize = cellSize;
  }

  public String getLatPropertyName() {
    return latPropertyName;
  }

  public void setLatPropertyName(String latPropertyName) {
    this.latPropertyName = latPropertyName;
  }

  public String getLngPropertyName() {
    return lngPropertyName;
  }

  public void setLngPropertyName(String lngPropertyName) {
    this.lngPropertyName = lngPropertyName;
  }
}
