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
package org.apache.streampipes.node.controller.management.pe.handler;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.pe.PipelineElementLifeCycleState;
import org.apache.streampipes.node.controller.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PipelineElementStateHandler implements IHandler<Response> {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineElementInteractionHandler.class.getCanonicalName());
    private static final String SLASH = "/";
    private static final String STATE = "state";
    private static final long RETRY_INTERVAL_MS = 5000;

    private final InvocableStreamPipesEntity graph;
    private final PipelineElementLifeCycleState type;
    private final String runningInstanceId;
    private final String state;


    public PipelineElementStateHandler(InvocableStreamPipesEntity graph, PipelineElementLifeCycleState type, String runningInstanceId) {
        this(graph, type, runningInstanceId, "{}");
    }

    public PipelineElementStateHandler(InvocableStreamPipesEntity graph, PipelineElementLifeCycleState type, String runningInstanceId, String state) {
        this.graph = graph;
        this.type = type;
        this.runningInstanceId = runningInstanceId;
        this.state = state;
    }


    @Override
    public Response handle() {
        switch(type) {
            case GETSTATE:
                return getState();
            case SETSTATE:
                return setState();
            default:
                throw new SpRuntimeException("Life cycle step not supported" + type);
        }
    }


    private Response getState(){
        Response response = new Response();
        String url = graph.getBelongsTo() + SLASH + runningInstanceId + SLASH + STATE;

        LOG.info("Trying to get state of pipeline element: {}", url);
        boolean connected = false;
        while (!connected) {

            response = HttpUtils.get(url, Response.class);
            connected = response.isSuccess();

            response.setOptionalMessage(response.getOptionalMessage());

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Failed to get State of pipeline element: " + url, e);
                }
            }
        }
        LOG.info("Successfully retrieved state from pipeline element {}", url);
        return response;
    }

    private Response setState(){
        Response response = new Response();
        String url = graph.getBelongsTo() + SLASH + runningInstanceId + SLASH + STATE;

        LOG.info("Trying to set state of pipeline element: {}", url);
        boolean connected = false;
        while (!connected) {


            response = HttpUtils.putAndRespond(url, state);

            connected = response.isSuccess();

            if (!connected) {
                LOG.debug("Retrying in {} seconds", (RETRY_INTERVAL_MS / 1000));
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Failed to set State of pipeline element: " + url, e);
                }
            }
        }
        LOG.info("Successfully retrieved state from pipeline element {}", url);
        return response;
    }


    //TODO: Optimize compression and move compression to PipelineElement Container (Brotli or Zstandard)
    //Preliminary compression logic copied from https://gist.github.com/yfnick/227e0c12957a329ad138
    private static byte[] compress(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    private static String decompress(final byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        byte[] bytes = IOUtils.toByteArray(gis);
        return new String(bytes, "UTF-8");
    }

}
