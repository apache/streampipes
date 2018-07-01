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

  @SafeVarargs
  public static List<Option> from(Tuple2<String, String>... options) {
    return Arrays.stream(options).map(o -> new Option(o.a, o.b)).collect(Collectors.toList());
  }
}
