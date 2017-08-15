package org.streampipes.pe.mixed.flink.extensions;

import org.apache.flink.api.java.functions.KeySelector;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by riemer on 22.04.2017.
 */
public class MapKeySelector implements Serializable {

  private String groupBy;

  public MapKeySelector(String groupBy) {
    this.groupBy = groupBy;
  }

  public KeySelector<Map<String, Object>, String> getKeySelector() {
    return new KeySelector<Map<String, Object>, String>() {
      @Override
      public String getKey(Map<String, Object> in) throws Exception {
        return String.valueOf(in.get(groupBy));
      }
    };
  }
}
