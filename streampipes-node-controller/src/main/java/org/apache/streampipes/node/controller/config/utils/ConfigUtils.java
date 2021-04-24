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
package org.apache.streampipes.node.controller.config.utils;

import org.apache.streampipes.node.controller.config.EnvConfigParam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ConfigUtils {

    public static <T>T checkSpNodeControllerUrlOrElseDefault(String key, T defaultValue, Class<T> type) {
        return checkEnvOrElseDefault(EnvConfigParam.NODE_CONTROLLER_URL.getEnvironmentKey(), key, defaultValue, type);
    }

    public static <T>T checkSpUrlOrElseDefault(String key, T defaultValue, Class<T> type) {
        return checkEnvOrElseDefault(EnvConfigParam.BACKEND_URL.getEnvironmentKey(), key, defaultValue, type);
    }

    public static <T>T checkEnvOrElseDefault(String envUrl, String defaultKey, T defaultValue, Class<T> type) {
        T result = null;
        if (System.getenv(envUrl) != null) {
            try {
                URL url = new URL(System.getenv(envUrl));
                if (type.equals(String.class)) {
                    result = (T) url.getHost();
                } else if (type.equals(Integer.class)) {
                    result = (T) Integer.valueOf(url.getPort());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Could not parse provide URL:", e);
            }
        } else {
            result = envOrDefault(defaultKey, defaultValue, type);
        }
        return result;
    }

    public static <T> T envOrDefault(String key, T defaultValue, Class<T> type) {
        if(type.equals(Integer.class)) {
            return System.getenv(key) != null ? (T) Integer.valueOf(System.getenv(key)) : defaultValue;
        } else if(type.equals(Boolean.class)) {
            return System.getenv(key) != null ? (T) Boolean.valueOf(System.getenv(key)) : defaultValue;
        } else {
            return System.getenv(key) != null ? type.cast(System.getenv(key)) : defaultValue;
        }
    }

    public static String generateSixDigitShortUUID() {
        String uuid = UUID.randomUUID().toString();
        return  uuid.substring(uuid.lastIndexOf('-') + 6);
    }
}
