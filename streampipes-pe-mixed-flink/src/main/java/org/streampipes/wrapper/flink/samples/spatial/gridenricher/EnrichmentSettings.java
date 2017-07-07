package org.streampipes.wrapper.flink.samples.spatial.gridenricher;

import java.io.Serializable;

/**
 * Created by riemer on 08.04.2017.
 */
public class EnrichmentSettings implements Serializable {

  private double latitudeStart;
  private double longitudeStart;

  private int cellSize;

  private String latPropertyName;
  private String lngPropertyName;

  public EnrichmentSettings() {
  }

  public EnrichmentSettings(double latitudeStart, double longitudeStart, int cellSize, String latPropertyName, String lngPropertyName) {
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
