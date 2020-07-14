package org.apache.streampipes.node.controller.container.management.pe;/*
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.streampipes.model.client.pipeline.PipelineElementStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PipelineElementManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(PipelineElementManager.class.getCanonicalName());

    private static final Integer CONNECT_TIMEOUT = 10000;

    private static PipelineElementManager instance = null;

    private PipelineElementManager() {}

    public static PipelineElementManager getInstance() {
        if (instance == null) {
            synchronized (PipelineElementManager.class) {
                if (instance == null)
                    instance = new PipelineElementManager();
            }
        }
        return instance;
    }

    /**
     * registeration of newly started pipeline element runtime container
     */

    /**
     * invokes pipeline elements when pipeline is started
     */
    public String invokePipelineElement(String pipelineElementEndpoint, String payload) {
        LOG.info("Invoking element: {}" + pipelineElementEndpoint);
        try {
            Response httpResp = Request
                    .Post(pipelineElementEndpoint)
                    .bodyString(payload, ContentType.APPLICATION_JSON)
                    .connectTimeout(CONNECT_TIMEOUT)
                    .execute();
            return httpResp.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return "";
    }

    /**
     * detaches pipeline elements when pipeline is stopped
     */
    // TODO: implement detach pe logic
    public void detachPipelineElement() {

    }

}
