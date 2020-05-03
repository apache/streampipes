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
    public final static String KEY_NOT_FOUND = "KEY_NOT_FOUND";

    public final static String KEY_ID = "id";
    public final static String KEY_TIMESTAMP = "timestamp";
    public final static String KEY_NAME = "name";
    public final static String KEY_MODEL = "model";
    public final static String KEY_LATITUDE = "latitude";
    public final static String KEY_LONGITUDE = "longitude";


    public final static String KEY_TEMPERATURE = "temperature";
    public final static String KEY_HUMIDITY = "humidity";
    public final static String KEY_PRESSURE = "pressure";
    public final static String KEY_ILLUMINANCE = "illuminance";
    public final static String KEY_UV_INTENSITY = "uv_intensity";
    public final static String KEY_PM10 = "pm10";
    public final static String KEY_PM2_5 = "pm2_5";


    public final static String LABEL_ID = "Id";
    public final static String LABEL_NAME = "Name";
    public final static String LABEL_MODEL = "Model";
    public final static String LABEL_LATITUDE = "latitude";
    public final static String LABEL_LONGITUDE = "longitude";



    public final static String LABEL_TEMPERATURE = "Temperature";
    public final static String LABEL_HUMIDITY = "Humidity";
    public final static String LABEL_PRESSURE = "Pressure";
    public final static String LABEL_ILLUMINANCE = "Illuminance";
    public final static String LABEL_UV_INTENSITY = "UV Intensity";
    public final static String LABEL_PM10 = "Particulate Matter 10";
    public final static String LABEL_PM2_5 = "Particulate Matter 2.5";


    public final static String GERMAN_TEMPERATURE = "Temperatur";
    public final static String GERMAN_HUMIDITY = "rel. Luftfeuchte";
    public final static String GERMAN_PRESSURE = "Luftdruck";
    public final static String GERMAN_ILLUMINANCE = "Beleuchtungsstärke";
    public final static String GERMAN_UV_INTENSITY = "UV-Intensität";
    public final static String GERMAN_PM10 = "PM10";
    public final static String GERMAN_PM2_5 = "PM2.5";

    public final static String[] ALL_SENSOR_KEYS = {
            KEY_TEMPERATURE,
            KEY_HUMIDITY,
            KEY_PRESSURE,
            KEY_ILLUMINANCE,
            KEY_UV_INTENSITY,
            KEY_PM10,
            KEY_PM2_5
    };

    public final static String[] ALL_SENSOR_LABELS = {
            LABEL_TEMPERATURE,
            LABEL_HUMIDITY,
            LABEL_PRESSURE,
            LABEL_ILLUMINANCE,
            LABEL_UV_INTENSITY,
            LABEL_PM10,
            LABEL_PM2_5
    };
    public final static List<String> ALL_META_KEYS = Arrays.asList(
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
