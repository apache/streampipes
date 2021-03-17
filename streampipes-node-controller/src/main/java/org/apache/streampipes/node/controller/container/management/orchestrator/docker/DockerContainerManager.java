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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.NotFoundException;
import com.spotify.docker.client.messages.Container;
import org.apache.commons.lang.StringUtils;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.node.controller.container.management.node.NodeManager;
import org.apache.streampipes.node.controller.container.management.orchestrator.ContainerOrchestrator;
import org.apache.streampipes.node.controller.container.management.orchestrator.ContainerDeploymentStatus;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.utils.DockerUtils;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DockerContainerManager implements ContainerOrchestrator {

    private static final Logger LOG =
            LoggerFactory.getLogger(DockerContainerManager.class.getCanonicalName());

    private final DockerUtils docker = DockerUtils.getInstance();
    private static DockerContainerManager instance = null;

    private DockerContainerManager() {}

    public static DockerContainerManager getInstance() {
        if (instance == null) {
            synchronized (DockerContainerManager.class) {
                if (instance == null)
                    instance = new DockerContainerManager();
            }
        }
        return instance;
    }

    @Override
    public void init() {
        DockerConstants.NodeContainer.INSTANCE.getAllStreamPipesContainer().forEach(container -> {
            ContainerDeploymentStatus status = deploy(container);

            if (status.getStatus() == DockerConstants.ContainerStatus.DEPLOYED) {
                NodeManager.getInstance().addToRegisteredContainers(status.getContainer());
            }
        });
    }

    @Override
    public ContainerDeploymentStatus deploy(DockerContainer container) {

        LOG.info("Pull image and deploy pipeline element container {}", container.getImageUri());
        Optional<Container> containerOptional = docker.getContainer(container.getContainerName());
        if (!containerOptional.isPresent()) {
            LOG.info("Deploy pipeline element container \"" + container.getImageUri() + "\"");
            String containerId = "";
            try {
                containerId = deployPipelineElementContainer(container);
            } catch (Exception e) {
                LOG.error("Could not deploy pipeline element. {}", e.toString());
                return generateContainerStatus(containerId, container, DockerConstants.ContainerStatus.FAILED);
            }
            LOG.info("Finished pull image and deployed pipeline element container");
            return generateContainerStatus(containerId, container, DockerConstants.ContainerStatus.DEPLOYED);
        }
        LOG.info("Container already running {}", container.getContainerName());
        return generateContainerStatus(containerOptional.get().id(), container, DockerConstants.ContainerStatus.RUNNING);
    }

    @Override
    public ContainerDeploymentStatus remove(DockerContainer container) {
        LOG.info("Remove pipeline element container: {}", container.getImageUri());

        Optional<com.spotify.docker.client.messages.Container> containerOptional = docker.getContainer(container.getContainerName());
        if(containerOptional.isPresent()) {

            docker.forceRemove(container.getContainerName());

            // deregister and delete kv pair in service in consul
            ConsulUtil.deregisterService(container.getServiceId());
            ConsulUtil.deleteConfig(container.getServiceId());

            return generateContainerStatus(containerOptional.get().id(), container,
                    DockerConstants.ContainerStatus.REMOVED);

        }
        return generateContainerStatus(containerOptional.get().id(), container,
                DockerConstants.ContainerStatus.UNKNOWN);
    }

    @Override
    public String list() {
        LOG.info("List running pipeline element container");

        List<Container> containerList = docker.getRunningStreamPipesContainer();
        HashMap<String, Object> containerJson = new HashMap<>();
        if (containerList.size() > 0) {
            for (Container c: containerList) {
                containerJson.put("containerName", StringUtils.remove(c.names().get(0), "/"));
                containerJson.put("containerId", c.id());
                containerJson.put("image", c.image());
                containerJson.put("state", c.state());
                containerJson.put("status", c.status());
                containerJson.put("labels", c.labels());
            }
        }
        try {
            return JacksonSerializer.getObjectMapper().writeValueAsString(containerJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize container list to JSON", e);
        }
    }

    private String deployPipelineElementContainer(DockerContainer container) throws Exception {
        return deployPipelineElementContainer(container, true);
    }

    private String deployPipelineElementContainer(DockerContainer container, boolean pullImage) throws Exception {
        if (pullImage) {
            try {
                docker.pullImage(container.getImageUri(), false);
            } catch (DockerException | InterruptedException e) {
                LOG.error("unable to pull pipeline element container image {}", e.toString());
                deployPipelineElementContainer(container, false);
            }
        }
        if (!pullImage && !docker.findLocalImage(container.getImageUri())) {
            throw new NotFoundException("Image not found locally");
        }
        String containerId = docker.createContainer(container);
        docker.startContainer(containerId);

        return containerId;
    }

    private ContainerDeploymentStatus generateContainerStatus(String containerId, DockerContainer container,
                                                              DockerConstants.ContainerStatus containerStatus) {
        ContainerDeploymentStatus status = new ContainerDeploymentStatus();

        status.setTimestamp(System.currentTimeMillis());
        status.setContainerId(containerId);
        status.setContainer(container);
        status.setStatus(containerStatus);

        return status;
    }
}
