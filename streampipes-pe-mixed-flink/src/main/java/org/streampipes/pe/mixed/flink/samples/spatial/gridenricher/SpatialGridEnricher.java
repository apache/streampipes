package org.streampipes.pe.mixed.flink.samples.spatial.gridenricher;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class SpatialGridEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private EnrichmentSettings settings;
  private SpatialGridCalculator calculator;

  public SpatialGridEnricher(EnrichmentSettings settings) {
    this.settings = settings;
    this.calculator = new SpatialGridCalculator(settings);
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws
          Exception {
    Double latitude = toDouble(in.get(settings.getLatPropertyName()));
    Double longitude = toDouble(in.get(settings.getLngPropertyName()));

    CellOption result = calculator.computeCells(latitude, longitude);
//    System.out.println("x=" +result.getCellX() +", y=" +result.getCellY());

    out.collect(toOutput(in, result));
  }

  private Map<String,Object> toOutput(Map<String, Object> in, CellOption result) {
    in.put(SpatialGridConstants.GRID_X_KEY, result.getCellX());
    in.put(SpatialGridConstants.GRID_Y_KEY, result.getCellY());
    in.put(SpatialGridConstants.GRID_CELLSIZE_KEY, result.getCellSize());
    in.put(SpatialGridConstants.GRID_LAT_NW_KEY, result.getLatitudeNW());
    in.put(SpatialGridConstants.GRID_LON_NW_KEY, result.getLongitudeNW());
    in.put(SpatialGridConstants.GRID_LAT_SE_KEY, result.getLatitudeSE());
    in.put(SpatialGridConstants.GRID_LON_SE_KEY, result.getLongitudeSE());

    return in;
  }

  private Double toDouble(Object value) {
    return Double.parseDouble(String.valueOf(value));
  }
}
