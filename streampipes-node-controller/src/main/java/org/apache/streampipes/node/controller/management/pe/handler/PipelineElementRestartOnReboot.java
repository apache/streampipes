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

import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.management.IHandler;
import org.apache.streampipes.node.controller.management.pe.PipelineElementManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PipelineElementRestartOnReboot implements IHandler<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineElementRestartOnReboot.class.getCanonicalName());

    private final List<InvocableStreamPipesEntity> pipelineElements;

    public PipelineElementRestartOnReboot(List<InvocableStreamPipesEntity> pipelineElements) {
        this.pipelineElements = pipelineElements;
    }

    @Override
    public Boolean handle() {
        List<Response> allStatus = new ArrayList<>();
        pipelineElements.forEach(graph -> {
            Response status = PipelineElementManager.getInstance().invoke(graph);
            allStatus.add(status);

            if (status.isSuccess()) {
                if (status.getOptionalMessage().isEmpty()) {
                    LOG.info("Pipeline element successfully restarted {}", status.getElementId());
                } else {
                    LOG.info("Pipeline element already running {}", status.getElementId());
                }
            } else {
                LOG.info("Pipeline element could not be restarted - are the pipeline element containers " +
                        "running? {}", status.getElementId());
            }
        });

        return allStatus.stream().allMatch(Response::isSuccess);
    }
}
