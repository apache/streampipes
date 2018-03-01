package org.streampipes.sdk.helpers;

import org.streampipes.model.staticproperty.Option;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Options {

  /**
   * Creates a new list of options by using the provided string values.
   * @param optionLabel An arbitrary number of option labels.
   * @return
   */
  public static List<Option> from(String... optionLabel) {
    return Arrays.stream(optionLabel).map(Option::new).collect(Collectors.toList());
  }

  public static List<Option> from(Tuple2<String, String>... options) {
    return Arrays.stream(options).map(o -> new Option(o.a, o.b)).collect(Collectors.toList());
  }
}
