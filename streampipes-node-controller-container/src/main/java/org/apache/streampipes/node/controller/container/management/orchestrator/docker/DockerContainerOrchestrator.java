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
package org.apache.streampipes.node.controller.container.management.orchestrator.docker;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.NotFoundException;
import com.spotify.docker.client.messages.Container;
import org.apache.commons.lang.StringUtils;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.management.orchestrator.ContainerOrchestrator;
import org.apache.streampipes.node.controller.container.management.orchestrator.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class DockerContainerOrchestrator implements ContainerOrchestrator {

    private static final Logger LOG =
            LoggerFactory.getLogger(DockerContainerOrchestrator.class.getCanonicalName());

    private final DockerUtils docker = DockerUtils.getInstance();
    private static DockerContainerOrchestrator instance = null;

    private DockerContainerOrchestrator() {}

    public static DockerContainerOrchestrator getInstance() {
        if (instance == null) {
            synchronized (DockerContainerOrchestrator.class) {
                if (instance == null)
                    instance = new DockerContainerOrchestrator();
            }
        }
        return instance;
    }

    @Override
    public void init() {
        DockerNodeContainer.INSTANCE.get().forEach(this::deploy);
    }

    @Override
    public String deploy(PipelineElementDockerContainer p) {
        LOG.info("Pull image and deploy pipeline element container {}", p.getImageURI());

        Optional<Container> containerOptional = docker.getContainer(p.getContainerName());
        if (!containerOptional.isPresent()) {
            LOG.info("Deploy pipeline element container \"" + p.getImageURI() + "\"");
            String containerId = "";
            try {
                containerId = deployPipelineElementContainer(p);
            } catch (Exception e) {
                LOG.error("Could not deploy pipeline element. {}", e.toString());
            }
            LOG.info("Finished pull image and deployed pipeline element container");
            ImmutableMap<String, ? extends Serializable> m = ImmutableMap.of(
                    "pipelineElementContainer", p.getContainerName(),
                    "containerId", containerId,
                    "status", ContainerStatus.DEPLOYED
            );
            return new Gson().toJson(m);
        }
        LOG.info("Container already running {}", p.getContainerName());
        ImmutableMap<String, ? extends Serializable> m = ImmutableMap.of(
                "message", "Pipeline element container already running",
                "status", ContainerStatus.RUNNING
        );
        return new Gson().toJson(m);
    }

    @Override
    public String remove(PipelineElementDockerContainer p) {
        LOG.info("Remove pipeline element container: {}", p.getImageURI());

        Optional<Container> containerOptional = docker.getContainer(p.getContainerName());
        if(containerOptional.isPresent()) {

            docker.forceRemove(p.getContainerName());

            // deregister and delete kv pair in service in consul
            ConsulUtil.deregisterService(p.getServiceId());
            ConsulUtil.deleteConfig(p.getServiceId());

            ImmutableMap<String, ? extends Serializable> m = ImmutableMap.of(
                    "message",
                    "Pipeline element container removed",
                    "status", ContainerStatus.REMOVED
            );
            return new Gson().toJson(m);
        }
        ImmutableMap<String, ? extends Serializable> m = ImmutableMap.of(
                "message",
                "Pipeline element container does not exist",
                "status", ContainerStatus.UNKNOWN
        );
        return new Gson().toJson(m);
    }

    @Override
    public String list() {
        LOG.info("List running pipeline element container");

        List<Container> containerList = docker.getRunningPipelineElementContainer();
        HashMap<String, Object> m = new HashMap<>();
        if (containerList.size() > 0) {
            for (Container c: containerList) {
                m.put("containerName", StringUtils.remove(c.names().get(0), "/"));
                m.put("containerId", c.id());
                m.put("image", c.image());
                m.put("state", c.state());
                m.put("status", c.status());
                m.put("labels", c.labels());
            }
        }
        return new Gson().toJson(m);
    }

    private String deployPipelineElementContainer(PipelineElementDockerContainer p) throws Exception {
        return deployPipelineElementContainer(p, true);
    }

    private String deployPipelineElementContainer(PipelineElementDockerContainer p, boolean pullImage) throws Exception {
        if (pullImage) {
            try {
                docker.pullImage(p.getImageURI(), false);
            } catch (DockerException | InterruptedException e) {
                LOG.error("unable to pull pipeline element container image {}", e.toString());
                deployPipelineElementContainer(p, false);
            }
        }
        if (!pullImage && !docker.findLocalImage(p.getImageURI())) {
            throw new NotFoundException("Image not found locally");
        }
        String containerId = docker.createContainer(p);
        docker.startContainer(containerId);

        return containerId;
    }
}
