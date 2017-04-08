package de.fzi.cep.sepa.flink.samples.spatial.gridenricher;

import java.io.Serializable;

/**
 * Created by riemer on 08.04.2017.
 */
public class SpatialGridCalculator implements Serializable {

  private double SOUTHDIFF = 0.004491556;
  private double EASTDIFF = 0.005986;

  private EnrichmentSettings settings;

  public SpatialGridCalculator(EnrichmentSettings settings) {
    this.settings = settings;
  }

  public CellOption computeCells(double latitude, double longitude) {
    this.SOUTHDIFF = (settings.getCellSize() / 500.0) * SOUTHDIFF;
    this.EASTDIFF = (settings.getCellSize() / 500.0) * EASTDIFF;
    int cellX = calculateXCoordinate(longitude);
    int cellY = calculateYCoordinate(latitude);

    return new CellOption(cellX, cellY, settings.getLatitudeStart() - (cellY * SOUTHDIFF),
            settings.getLongitudeStart() + (cellX *
            EASTDIFF), settings.getLatitudeStart() - ((cellY + 1) * SOUTHDIFF), settings.getLongitudeStart() + (
                    (cellX + 1) * EASTDIFF),
            settings.getCellSize());
  }

  private int calculateXCoordinate(double longitude) {
    Double ret = (Math.abs(settings.getLongitudeStart() - longitude) / EASTDIFF);
    return ret.intValue() + 1;
  }

  private int calculateYCoordinate(double latitude) {
    Double ret = (Math.abs(settings.getLatitudeStart() - latitude) / SOUTHDIFF);
    return ret.intValue() + 1;
  }

}
