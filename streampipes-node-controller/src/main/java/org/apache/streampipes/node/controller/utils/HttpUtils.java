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
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
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

    public static <T> T get(String url, Class<T> clazz) {
        try {
            Response response = Request.Get(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            if (clazz.isInstance(Response.class)) {
                return (T) response;
            } else if (clazz.isInstance(String.class)) {
                return (T) response.returnContent().asString();
            } else if (clazz.isAssignableFrom(byte[].class)) {
                return (T) response.returnContent().asBytes();
            } else {
                return deserialize(response, clazz);
            }
        } catch (IOException e) {
            throw new SpRuntimeException("Something went wrong during GET request", e);
        }
    }

    public static <T>T get(String url, final TypeReference<T> type) {
        try {
            return deserialize(Request.Get(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute()
                    .returnContent().asString(), type);
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

    public static boolean put(String url) {
        try {
            Request.Put(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return true;
        } catch (IOException e) {
            LOG.error("Something went wrong during PUT request", e);
            return false;
        }
    }

    public static <T> boolean put(String url, T object) {
        String body = serialize(object);
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

    //TODO: Naming is provisional; rename
    public static org.apache.streampipes.model.Response putAndRespond(String url, String body) {
        org.apache.streampipes.model.Response response = new org.apache.streampipes.model.Response();
        javax.ws.rs.core.Response.ok().build();
        response.setSuccess(false);
        try {
            response = deserialize(Request.Put(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute(), org.apache.streampipes.model.Response.class);
            return response;
        } catch (IOException e) {
            LOG.error("Something went wrong during PUT request", e);
            return response;
        }
    }

    public static String post(String url, String body) {
        try {
            return Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute().returnContent().asString();
        } catch (IOException e) {
            LOG.error("Something went wrong during POST request", e);
            return null;
        }
    }

    public static <T1, T2> T2 post(String url, T1 object, Class<T2> clazz) {
        String body = serialize(object);
        try {
            Response response = Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();

            if (clazz.isInstance(Response.class)) {
                return (T2) response;
            } else if (clazz.isInstance(String.class)) {
                return (T2) response.returnContent().asString();
            } else if (clazz.isAssignableFrom(byte[].class)) {
                return (T2) response.returnContent().asBytes();
            } else if (clazz.isAssignableFrom(Boolean.class)) {
                return (T2) Boolean.TRUE;
            } else {
                return deserialize(response, clazz);
            }
        } catch (IOException e) {
            if (clazz.isAssignableFrom(Boolean.class)) {
                return (T2) Boolean.FALSE;
            }

            throw new SpRuntimeException("Something went wrong during POST request", e);
        }
    }

    public static <T1, T2> T2 post(String url, String bearerToken, T1 object, Class<T2> clazz) {
        String body = serialize(object);
        try {
            Response response = Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .addHeader("Authorization", "Bearer " + bearerToken)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();

            if (clazz.isInstance(Response.class)) {
                return (T2) response;
            } else if (clazz.isInstance(String.class)) {
                return (T2) response.returnContent().asString();
            } else if (clazz.isAssignableFrom(byte[].class)) {
                return (T2) response.returnContent().asBytes();
            } else if (clazz.isAssignableFrom(Boolean.class)) {
                return (T2) Boolean.TRUE;
            } else {
                return deserialize(response, clazz);
            }
        } catch (IOException e) {
            if (clazz.isAssignableFrom(Boolean.class)) {
                return (T2) Boolean.FALSE;
            }

            throw new SpRuntimeException("Something went wrong during POST request", e);
        }
    }

    public static <T> org.apache.streampipes.model.Response post(String url, T object) {
        String body = serialize(object);
        org.apache.streampipes.model.Response response = new org.apache.streampipes.model.Response();
        response.setSuccess(false);
        try {
            response = deserialize(Request.Post(url)
                    .bodyString(body, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute(), org.apache.streampipes.model.Response.class);
            return response;
        } catch (IOException e) {
            LOG.error("Something went wrong during POST request", e);
            return response;
        }
    }

//    public static <T1, T2> T2 post(String url, T1 object, Class<T2> clazz) {
//        String body = serialize(object);
//        try {
//            return deserialize(Request.Post(url)
//                    .bodyString(body, ContentType.APPLICATION_JSON)
//                    .connectTimeout(CONNECT_TIMEOUT)
//                    .execute()
//                    .returnContent().asString(), clazz);
//        } catch (IOException e) {
//            throw new SpRuntimeException("Something went wrong during POST request", e);
//        }
//    }

    public static org.apache.streampipes.model.Response delete(String url) {
        org.apache.streampipes.model.Response response = new org.apache.streampipes.model.Response();
        response.setSuccess(false);
        try {
            response = deserialize(Request.Delete(url)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute(), org.apache.streampipes.model.Response.class);
            return response;
        } catch (Exception e) {
            LOG.error("Something went wrong during DELETE request", e);
            return response;
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

    public static <T>T deserialize(Response response, Class<T> clazz) {
        try {
            String responseString = response.returnContent().asString();
            //TODO: Fix issue when starting connect adapters (& remove quick fix)
            if(clazz.equals(String.class))
                return (T) responseString;
            return JacksonSerializer
                    .getObjectMapper()
                    .readValue(responseString, clazz);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not deserialize object", e);
        } catch (IOException e) {
            throw new SpRuntimeException("Could not get return content as string", e);
        }
    }

    public static <T>T deserialize(String objectString, final TypeReference<T> type) {
        try {
            return JacksonSerializer
                    .getObjectMapper()
                    .readValue(objectString, type);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not deserialize object", e);
        }
    }
}
