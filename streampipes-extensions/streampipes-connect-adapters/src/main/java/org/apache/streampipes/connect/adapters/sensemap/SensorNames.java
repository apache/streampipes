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

package org.apache.streampipes.connect.adapters.sensemap;

import java.util.Arrays;
import java.util.List;

public class SensorNames {
  public static final String KEY_NOT_FOUND = "KEY_NOT_FOUND";

  public static final String KEY_ID = "id";
  public static final String KEY_TIMESTAMP = "timestamp";
  public static final String KEY_NAME = "name";
  public static final String KEY_MODEL = "model";
  public static final String KEY_LATITUDE = "latitude";
  public static final String KEY_LONGITUDE = "longitude";


  public static final String KEY_TEMPERATURE = "temperature";
  public static final String KEY_HUMIDITY = "humidity";
  public static final String KEY_PRESSURE = "pressure";
  public static final String KEY_ILLUMINANCE = "illuminance";
  public static final String KEY_UV_INTENSITY = "uv_intensity";
  public static final String KEY_PM10 = "pm10";
  public static final String KEY_PM2_5 = "pm2_5";


  public static final String LABEL_ID = "Id";
  public static final String LABEL_NAME = "Name";
  public static final String LABEL_MODEL = "Model";
  public static final String LABEL_LATITUDE = "latitude";
  public static final String LABEL_LONGITUDE = "longitude";


  public static final String LABEL_TEMPERATURE = "Temperature";
  public static final String LABEL_HUMIDITY = "Humidity";
  public static final String LABEL_PRESSURE = "Pressure";
  public static final String LABEL_ILLUMINANCE = "Illuminance";
  public static final String LABEL_UV_INTENSITY = "UV Intensity";
  public static final String LABEL_PM10 = "Particulate Matter 10";
  public static final String LABEL_PM2_5 = "Particulate Matter 2.5";


  public static final String GERMAN_TEMPERATURE = "Temperatur";
  public static final String GERMAN_HUMIDITY = "rel. Luftfeuchte";
  public static final String GERMAN_PRESSURE = "Luftdruck";
  public static final String GERMAN_ILLUMINANCE = "Beleuchtungsstärke";
  public static final String GERMAN_UV_INTENSITY = "UV-Intensität";
  public static final String GERMAN_PM10 = "PM10";
  public static final String GERMAN_PM2_5 = "PM2.5";

  public static final String[] ALL_SENSOR_KEYS = {
      KEY_TEMPERATURE,
      KEY_HUMIDITY,
      KEY_PRESSURE,
      KEY_ILLUMINANCE,
      KEY_UV_INTENSITY,
      KEY_PM10,
      KEY_PM2_5
  };

  public static final String[] ALL_SENSOR_LABELS = {
      LABEL_TEMPERATURE,
      LABEL_HUMIDITY,
      LABEL_PRESSURE,
      LABEL_ILLUMINANCE,
      LABEL_UV_INTENSITY,
      LABEL_PM10,
      LABEL_PM2_5
  };
  public static final List<String> ALL_META_KEYS = Arrays.asList(
      KEY_ID, KEY_NAME, KEY_TIMESTAMP, KEY_MODEL, KEY_LONGITUDE, KEY_LATITUDE
  );

  public static String getKey(String germanKeyValue) {
    if (check(germanKeyValue, GERMAN_TEMPERATURE)) {
      return KEY_TEMPERATURE;
    } else if (check(germanKeyValue, GERMAN_HUMIDITY)) {
      return KEY_HUMIDITY;
    } else if (check(germanKeyValue, GERMAN_PRESSURE)) {
      return KEY_PRESSURE;
    } else if (check(germanKeyValue, GERMAN_ILLUMINANCE)) {
      return KEY_ILLUMINANCE;
    } else if (check(germanKeyValue, GERMAN_UV_INTENSITY)) {
      return KEY_UV_INTENSITY;
    } else if (check(germanKeyValue, GERMAN_PM10)) {
      return KEY_PM10;
    } else if (check(germanKeyValue, GERMAN_PM2_5)) {
      return KEY_PM2_5;
    } else {
      return KEY_NOT_FOUND;
    }
  }

  public static String getKeyFromLabel(String labelString) {
    if (LABEL_TEMPERATURE.equals(labelString)) {
      return KEY_TEMPERATURE;
    } else if (LABEL_HUMIDITY.equals(labelString)) {
      return KEY_HUMIDITY;
    } else if (LABEL_PRESSURE.equals(labelString)) {
      return KEY_PRESSURE;
    } else if (LABEL_ILLUMINANCE.equals(labelString)) {
      return KEY_ILLUMINANCE;
    } else if (LABEL_UV_INTENSITY.equals(labelString)) {
      return KEY_UV_INTENSITY;
    } else if (LABEL_PM10.equals(labelString)) {
      return KEY_PM10;
    } else if (LABEL_PM2_5.equals(labelString)) {
      return KEY_PM2_5;
    } else {
      return KEY_NOT_FOUND;
    }
  }

  private static boolean check(String value, String contains) {
    return value.contains(contains);
  }


}
