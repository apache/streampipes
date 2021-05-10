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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.eventrelay.SpDataStreamRelayContainer;
import org.apache.streampipes.model.node.NodeInfoDescription;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class.getCanonicalName());

    private static final String HTTP_PROTOCOL = "http";
    private static final int CONNECT_TIMEOUT = 1000;

    public static Response get(String url) {
        try {
            return Request.Get(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
        } catch (IOException e) {
            throw new SpRuntimeException("Something went wrong during GET request", e);
        }
    }

    public static <T>T get(String url, Class<T> clazz) {
        try {
            return deserialize(Request.Get(url)
                            .connectTimeout(CONNECT_TIMEOUT)
                            .execute()
                            .returnContent().asString(), clazz);
        } catch (IOException e) {
            throw new SpRuntimeException("Something went wrong during GET request", e);
        }
    }

    public static <T>T get(String url, String bearerToken, Class<T> clazz) {
        try {
            return deserialize(Request.Get(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .addHeader("Authorization", "Bearer " + bearerToken)
                    .execute()
                    .returnContent().asString(), clazz);
        } catch (IOException e) {
            throw new SpRuntimeException("Something went wrong during GET request", e);
        }
    }

    public static boolean put(String url, String body) {
        try {
            Request.Put(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Something went wrong during PUT request", e);
            return false;
        }
    }

    public static boolean post(String url, String body) {
        try {
            Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Something went wrong during POST request", e);
            return false;
        }
    }

    public static <T> boolean post(String url, T object) {
        String body = serialize(object);
        try {
            Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Something went wrong during POST request", e);
            return false;
        }
    }

    public static String generateEndpoint(String host, int port, String route) {

        if (route.startsWith("/")) {
            route = route.substring(1);
        }

        if (host != null && port != -1) {
            return String.format("%s://%s:%s/%s", HTTP_PROTOCOL, host, port, route);
        } else {
            throw new SpRuntimeException("Could not generate endpoint");
        }
    }

    public static <T> String serialize(T object) {
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not serialize object", e);
        }
    }

    public static <T>T deserialize(String objectString, Class<T> clazz) {
        try {
            return JacksonSerializer
                    .getObjectMapper()
                    .readValue(objectString, clazz);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not deserialize object", e);
        }
    }
}
