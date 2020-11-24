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

package org.apache.streampipes.connect.container.worker.management;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MasterRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(MasterRestClient.class);

    public static boolean register(String baseUrl, ConnectWorkerContainer connectWorkerContainer) {

        String url = baseUrl + "/admin@streampipes.org/master/administration";

        try {
            String adapterDescription = JacksonSerializer.getObjectMapper().writeValueAsString(connectWorkerContainer);

            Request.Post(url)
                    .bodyString(adapterDescription, ContentType.APPLICATION_JSON)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            return true;
        } catch (IOException e) {
            LOG.info("Could not connect to " + url);
        }

        return false;
    }

}
