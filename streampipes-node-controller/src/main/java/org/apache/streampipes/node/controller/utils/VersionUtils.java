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
package org.apache.streampipes.node.controller.utils;

import org.apache.streampipes.model.client.version.VersionInfo;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(VersionUtils.class.getCanonicalName());

    private static final long RETRY_INTERVAL_MS = 5000;
    private static final String ROUTE = "/streampipes-backend/api/v2/info/versions";

    public static String getStreamPipesVersion() {
        boolean connected = false;
        VersionInfo versionInfo = new VersionInfo();
        String host = NodeConfiguration.getBackendHost();
        int port = NodeConfiguration.getBackendPort();

        String endpoint = HttpUtils.generateEndpoint(host, port, ROUTE);
        String bearerToken = NodeConfiguration.getNodeApiKey();

        LOG.info("Trying to retrieve StreamPipes version from backend: " + endpoint);

        while (!connected) {
            versionInfo = HttpUtils.get(endpoint, bearerToken, VersionInfo.class);

            if (versionInfo.getBackendVersion() != null) {
                connected = true;
            }

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info("Successfully retrieved StreamPipes version from backend");
        return versionInfo.getBackendVersion();
    }
}
