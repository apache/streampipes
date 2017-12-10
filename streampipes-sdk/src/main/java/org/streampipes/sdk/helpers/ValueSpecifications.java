package org.streampipes.sdk.helpers;

import org.streampipes.model.schema.Enumeration;
import org.streampipes.model.schema.QuantitativeValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValueSpecifications {

  public static QuantitativeValue from(float minValue, float maxValue, float step) {
    return new QuantitativeValue(minValue, maxValue, step);
  }

  public static QuantitativeValue from(Integer minValue, Integer maxValue, Integer step) {
    return new QuantitativeValue((float) minValue, (float) maxValue, (float) step);
  }

  public static Enumeration from(String... values) {
    return from(Arrays.asList(values));
  }

  public static Enumeration from(Integer... values) {
    return from(Arrays.stream(values).map(v -> String.valueOf(values)).collect(Collectors.toList()));
  }

  private static Enumeration from(List<String> values) {
    Enumeration enumeration = new Enumeration();
    enumeration.setRuntimeValues(values);
    return enumeration;
  }


}
