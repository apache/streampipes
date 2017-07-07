package org.streampipes.wrapper.flink.samples.spatial.gridenricher;

import java.io.Serializable;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridCalculator implements Serializable {

  private double SOUTHDIFF = 0.004491556;
  private double EASTDIFF = 0.005986;

  private Integer cellSize = 500;
  private Double startLat = 41.474937;
  private Double startLon = -74.913585;

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
