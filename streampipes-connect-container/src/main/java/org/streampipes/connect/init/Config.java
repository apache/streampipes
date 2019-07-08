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

package org.streampipes.connect.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static final String MASTER = "MASTER";
    public static final String WORKER = "WORKER";


    public static String CONNECTOR_CONTAINER_ID = "MAIN_CONTAINER";


    public static int MASTER_PORT = 8099;
    public static int WORKER_PORT = 8098;
    public static String HOST = "localhost";

    public static String getMasterBaseUrl() {
        return "http://" + HOST + ":" + MASTER_PORT + "/";
    }

    public static String getWorkerBaseUrl() {
        return "http://" + HOST + ":" + WORKER_PORT + "/";
    }

    public static String getEnv(String envName) {

        String envVarianble = System.getenv(envName);
        if (envVarianble == null) {
            LOG.error("Environment variable " + envName + " is not set");
            return "";
        } else {
            return envVarianble;
        }
    }
}
