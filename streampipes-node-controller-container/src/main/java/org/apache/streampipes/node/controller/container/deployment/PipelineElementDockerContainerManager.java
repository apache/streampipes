package org.apache.streampipes.node.controller.container.deployment;/*
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
import com.google.gson.reflect.TypeToken;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.apache.commons.lang.StringUtils;
import org.apache.streampipes.container.util.ConsulUtil;
import org.apache.streampipes.model.node.PipelineElementDockerContainer;
import org.apache.streampipes.node.controller.container.deployment.utils.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

public class PipelineElementDockerContainerManager {

    private static final Logger LOG =
            LoggerFactory.getLogger(PipelineElementDockerContainerManager.class.getCanonicalName());

    private static final String SP_NETWORK = "spnet";
    private static final String SP_CONTAINER_PREFIX = "streampipes_";

    static DockerClient dockerClient;

    static {
        try {
            dockerClient = DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public static String deploy(PipelineElementDockerContainer container) throws DockerException, InterruptedException {
        LOG.info("Deployment request for: {}", container.getContainerName());

        if (!containerDeployed(container.getContainerName())) {
            LOG.info("Deployment request accepted");

            ContainerConfig containerConfig = ContainerConfig.builder()
                    .hostname(container.getContainerName())
                    .tty(true)
                    .image(container.getImageURI())
                    .labels(container.getLabels())
                    .env(container.getEnvVars())
                    .hostConfig(DockerUtils.getHostConfig(SP_NETWORK, container.getContainerPorts()))
                    .networkingConfig(DockerUtils.getNetworkingConfig(SP_NETWORK, container.getContainerName()))
                    .build();

            ContainerCreation creation = dockerClient
                    .createContainer(containerConfig, SP_CONTAINER_PREFIX + container.getContainerName());

            LOG.info("Deploy pipeline element container: {}", container.getContainerName());
            dockerClient.startContainer(creation.id());

            return "{\n" +
                    "  \"message\": \"deployment request for " + container.getContainerName() + " accepted\",\n" +
                    "  \"status\": \"deployed\",\n" +
                    "  \"containerId\": \"" + creation.id() + "\"\n" +
                    "}";
        }

        LOG.info("Deployment request declined: {}", container.getContainerName());
        return "{\n" +
                "  \"message\": \"deployment request for " + container.getContainerName() + " declined\",\n" +
                "  \"status\": \"running\"\n" +
                "}";
    }

    public static String stopAndRemove(PipelineElementDockerContainer container) {

        if(containerDeployed(container.getContainerName())) {

            LOG.info("Stop and remove pipeline element container: {}", container.getContainerName());

            String cId = containerId(container.getContainerName());
            // stop and remove docker container
            try {
                dockerClient.stopContainer(cId, 0);
                dockerClient.removeContainer(cId);

                // deregister and delete kv pair in service in consul
                ConsulUtil.deregisterService(container.getServiceId());
                ConsulUtil.deleteKeys(container.getServiceId());

            } catch (DockerException | InterruptedException e) {
                e.printStackTrace();
            }

            return "{\n" +
                    "  \"message\": \"stop and remove request for " + container.getContainerName() + " executed\",\n" +
                    "  \"status\": \"container removed\"\n" +
                    "}";

        }
        LOG.info("Deployment request declined: {}", container.getContainerName());
        return "{\n" +
                "  \"message\": \"stop and remove request for " + container.getContainerName() + " declined\",\n" +
                "  \"status\": \"no such container running\"\n" +
                "}";
    }

    private static boolean containerDeployed(String containerName) {
        boolean deployed = false;
        try {
            List<Container> runningContainers = dockerClient.listContainers();
            for (Container c: runningContainers) {
                if ( StringUtils.contains(c.names().get(0), containerName) ) {
                    deployed = true;
                    break;
                }
                deployed = false;
            }
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return deployed;
    }

    private static String containerId(String containerName){
        try {
            List<Container> containers = dockerClient.listContainers();
            for (Container c: containers) {
                if(c.names().get(0).contains(containerName)) {
                    return c.id();
                }
            }
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPipelineElementContainer() {

        LOG.info("Retrieve deployed pipeline element containers");
        Gson gson = new Gson();
        Type resultType = new TypeToken<List<Map<String, Object>>>(){}.getType();
        List<Map<String, String>> pe = new ArrayList<>();
        try {
            List<Container> runningContainers = dockerClient.listContainers();
            if (runningContainers.size() > 0) {
                for (Container c: runningContainers) {
                    if (c.labels().containsValue("pipeline-element")) {
                        Map<String, String> inner = new HashMap<String, String>() {
                            {
                                put("pipeline-element", StringUtils.remove(c.names().get(0), "/" + SP_CONTAINER_PREFIX));
                                put("containerId", c.id());
                                put("state", c.state());
                                put("status", c.status());
                            }
                        };
                        pe.add(inner);
                    }
                }
            }
            return gson.toJson(pe, resultType);
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return gson.toJson(pe, resultType);
    }

}
