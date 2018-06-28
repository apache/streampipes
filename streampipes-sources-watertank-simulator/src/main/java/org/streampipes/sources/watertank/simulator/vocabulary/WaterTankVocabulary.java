/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sources.watertank.simulator.vocabulary;

import org.streampipes.vocabulary.SO;

public class WaterTankVocabulary {

  public static final String NS = "https://streampipes.org/vocabulary/examples/watertank/v1/";

  public static final String HAS_SENSOR_ID = NS + "hasSensorId";
  public static final String IS_OVERFLOW = NS + "isOverflow";
  public static final String IS_UNDERFLOW = NS + "isUnderflow";
  public static final String HAS_WATER_LEVEL = SO.Number;
  public static final String HAS_MASS_FLOW = SO.Number;
  public static final String HAS_TEMPERATURE = SO.Number;
  public static final String HAS_PRESSURE = SO.Number;

  public static final String HAS_VOLUME_FLOW= SO.Number;
  public static final String HAS_SENSOR_FAULT_FLAGS = SO.Number;
  public static final String HAS_DENSITY = SO.Number;
}
