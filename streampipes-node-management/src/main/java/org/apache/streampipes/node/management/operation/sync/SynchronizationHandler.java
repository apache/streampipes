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
package org.apache.streampipes.node.management.operation.sync;

import org.apache.streampipes.node.management.utils.HttpRequest;
import org.apache.streampipes.node.management.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizationHandler<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SynchronizationHandler.class.getCanonicalName());

    private static final long RETRY_INTERVAL_MS = 5000;
    private final T element;
    private final String route;
    private final HttpRequest httpRequest;
    private final boolean withBody;

    public SynchronizationHandler(T element, String route, HttpRequest httpRequest, boolean withBody) {
        this.element = element;
        this.route = route;
        this.httpRequest = httpRequest;
        this.withBody = withBody;
    }

    public boolean synchronize() {
        boolean synced = false;

        String body = "{}";
        if (withBody) {
            body = HttpUtils.serialize(element);
        }

        String url = HttpUtils.generateEndpoint(element, route);
        LOG.info("Trying to sync with node controller=" + url);

        boolean connected = false;
        while (!connected) {
            // call node controller REST endpoints
            switch (httpRequest) {
                case POST:
                    connected = HttpUtils.post(url, body);
                    break;
                case PUT :
                    connected = HttpUtils.put(url, body);
                    break;
            }

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 10000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synced = true;
        }
        LOG.info("Successfully synced with node controller=" + url);
        return synced;
    }
}
