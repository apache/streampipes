/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.vocabulary;

import java.util.Arrays;
import java.util.List;

public class SPSensor {

  public static final String ACCELERATION_X = "http://streampipes.org/hmi/accelerationX";
  public static final String ACCELERATION_Y = "http://streampipes.org/hmi/accelerationY";
  public static final String ACCELERATION_Z = "http://streampipes.org/hmi/accelerationZ";

  public static final String GYROSCOPE_X = "http://streampipes.org/hmi/gyroscopeX";
  public static final String GYROSCOPE_Y = "http://streampipes.org/hmi/gyroscopeY";
  public static final String GYROSCOPE_Z = "http://streampipes.org/hmi/gyroscopeZ";

  public static final String MAGNETOMETER_X = "http://streampipes.org/hmi/magnetometerX";
  public static final String MAGNETOMETER_Y = "http://streampipes.org/hmi/magnetometerY";
  public static final String MAGNETOMETER_Z = "http://streampipes.org/hmi/magnetometerZ";

  public static final String TEMPERATURE = "http://streampipes.org/hmi/temperature";


  public static final String AMBIENT_LIGHT = "http://streampipes.org/hmi/ambientLight";

  public static final String IMAGE = "https://image.com";

  public static final String STATE = "http://streampipes.org/process/state";

  public static List<String> getAll() {
    return Arrays.asList(ACCELERATION_X,
        ACCELERATION_Y,
        ACCELERATION_Z,
        GYROSCOPE_X,
        GYROSCOPE_Y,
        GYROSCOPE_Z,
        AMBIENT_LIGHT,
        IMAGE,
        STATE);
  }

}
