package org.streampipes.sdk.helpers;

import org.streampipes.model.impl.staticproperty.Option;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by riemer on 20.03.2017.
 */
public class Options {

  public static List<Option> from(String... optionLabel) {
    return Arrays.stream(optionLabel).map(Option::new).collect(Collectors.toList());
  }
}
