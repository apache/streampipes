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
package org.apache.streampipes.node.controller.container.management.orchestrator.docker.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import com.spotify.docker.client.shaded.com.google.common.collect.ImmutableList;
import org.apache.streampipes.model.node.DockerContainer;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerConstants;
import org.apache.streampipes.node.controller.container.management.orchestrator.docker.DockerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class DockerUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DockerUtils.class.getCanonicalName());

    private static final String BLANK_SPACE = " ";
    private static final String DOCKER_UNIX_SOCK = "/var/run/docker.sock";
    private static DockerUtils instance;
    private static DockerClient docker;

    private DockerUtils() {
        init();
    }

    public static DockerUtils getInstance() {
        if (instance == null) {
            synchronized (DockerUtils.class) {
                if (instance == null)
                    instance = new DockerUtils();
            }
        }
        return instance;
    }

    private void init() {
        LOG.info("Initialize Docker client");
        try {
            docker = DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public void startContainer(String containerId) {
        LOG.info("Start pipeline element container {}", containerId);
        try {
            docker.startContainer(containerId);
        } catch (DockerException | InterruptedException e) {
            LOG.error("Pipeline element container could not be started. {}", e.toString());
        }
    }

    private void stopContainer(String containerId) {
        LOG.info("Stop container {}", containerId);
        try {
            docker.stopContainer(containerId, 0);
        } catch (DockerException | InterruptedException e) {
            LOG.error("Could not stop container {}", e.toString());
        }
    }

    private void removeContainer(String containerId) {
        LOG.info("Remove container {}", containerId);
        try {
            docker.removeContainer(containerId, DockerClient.RemoveContainerParam.removeVolumes(true));
        } catch (DockerException | InterruptedException e) {
            LOG.error("Could not remove container {}", e.toString());
        }
    }

    public void forceRemove(String containerName) {
        String containerId = getContainerIdByName(containerName);
        stopContainer(containerId);
        removeContainer(containerId);
    }

    private String getContainerIdByName(String containerName) {
        LOG.info("Get containerId by container name");
        try {
            return docker.listContainers()
                    .stream()
                    .findAny()
                    .filter(c -> c.names().get(0).contains(verifyContainerName(containerName)))
                    .get()
                    .id();
        } catch (DockerException | InterruptedException e) {
            LOG.error("Unable to list containers {}", e.toString());
        }
        return "";
    }


    public String createContainer(DockerContainer p) {
        LOG.info("Create pipeline element container {}", p.getContainerName());
        try {
            return docker.createContainer(getContainerConfig(p), verifyContainerName(p.getContainerName())).id();
        } catch (DockerException | InterruptedException e) {
            LOG.error("Pipeline element container could not be created. {}", e.toString());
        }
        return "";
    }

    private String verifyContainerName(String containerName) {
        return containerName.startsWith(DockerConstants.SP_DOCKER_CONTAINER_NAME_PREFIX) ?
                containerName : DockerConstants.SP_DOCKER_CONTAINER_NAME_PREFIX + containerName;
    }

    private ContainerConfig getContainerConfig(DockerContainer p) {
        return ContainerConfig.builder()
                .hostname(p.getContainerName())
                .tty(true)
                .image(p.getImageURI())
                .labels(p.getLabels())
                .env(p.getEnvVars())
                .hostConfig(getHostConfig(DockerConstants.SP_DOCKER_NETWORK_NAME, p.getContainerPorts()))
                .networkingConfig(getNetworkingConfig(DockerConstants.SP_DOCKER_NETWORK_NAME, p.getContainerName()))
                .build();
    }

    public Optional<Container> getContainer(String containerName) {
        return getContainerList()
                .stream()
                .filter(c -> c.names().get(0).contains(verifyContainerName(containerName)))
                .findAny();
    }

    public void pullImage(String image) throws DockerException, InterruptedException {
        pullImage(image, true);
    }

    public void pullImage(String image, boolean forcePull) throws DockerException, InterruptedException {
        LOG.info("Pull container image: {}", image);
        if (forcePull || !findLocalImage(image)) {
            docker.pull(image);
            LOG.info("Image {} successfully pulled", image);
        }
        LOG.info("Image {} from local cache", image);
    }

    public List<Container> getContainerList() {
        try {
            return docker.listContainers();
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static HostConfig getHostConfig(String network, String[] ports) {
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            portBindings.put(port + "/tcp", Collections.singletonList(PortBinding.of("0.0.0.0", port)));
        }

        return HostConfig.builder()
                .portBindings(portBindings)
                .networkMode(network)
                .build();
    }

    private static HostConfig getHostConfig(String network) {
        return HostConfig.builder()
                .networkMode(network)
                .build();
    }

    private static ContainerConfig.NetworkingConfig getNetworkingConfig(String network, String containerName) {
        return ContainerConfig.NetworkingConfig
                .create(new HashMap<String, EndpointConfig>() {{
                    put(network, getEndpointConfig(containerName));}}
                );

    }

    private static EndpointConfig getEndpointConfig(String containerName) {
        return EndpointConfig.builder()
                .aliases(ImmutableList.of(containerName))
                .build();
    }

    public static void prune() {
        //List<String> pruneItems = Arrays.asList("images", "containers", "volumes", "networks");
        List<String> pruneItems = Collections.singletonList("images");

        for (String i: pruneItems) {
            try {
                //TODO: change! docker spotify client does not support docker system commands
                //TODO: make sure curl is installed in node-controller container
                Process p = Runtime.getRuntime().exec(
                        "curl -X POST --unix-socket"
                                + BLANK_SPACE
                                + DOCKER_UNIX_SOCK
                                + BLANK_SPACE
                                + "http:/v" + apiVersion() + "/" + i + "/prune");

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));

                String s;
                while ((s = stdInput.readLine()) != null) {
                    LOG.info("Docker engine response: {}", s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        public static DockerInfo getDockerInfo() {
        DockerInfo dockerInfo = new DockerInfo();
        try {
            Info info = docker.info();
            Version version = docker.version();

            dockerInfo.setServerVersion(info.serverVersion());
            dockerInfo.setApiVersion(version.apiVersion());
            dockerInfo.setKernelVersion(version.kernelVersion());
            dockerInfo.setArch(version.arch());
            dockerInfo.setOsType(info.osType());
            dockerInfo.setOs(info.operatingSystem());
            dockerInfo.setCpus(info.cpus());
            dockerInfo.setMemTotal(info.memTotal());
            dockerInfo.setHasNvidiaRuntime(hasNvidiaRuntime());

            return dockerInfo;
        } catch (DockerException | InterruptedException e) {
            LOG.error("Could net get docker info {}", e.toString());
        }
        return dockerInfo;
    }

    private static String apiVersion() {
        String version = "";
        try {
            version = docker.version().apiVersion();
        } catch (DockerException | InterruptedException e) {
            LOG.error("Could not get Docker API version. {}", e.toString());
        }
        return version;
    }

    public boolean findLocalImage(String imageName) {
        List<Image> images = Collections.emptyList();
        try {
            images = docker.listImages(DockerClient.ListImagesParam.byName(imageName));
        } catch (DockerException | InterruptedException e) {
           LOG.error("Could not search docker image {}", e.toString());
        }
        return images.stream()
                .findAny()
                .isPresent();
    }

    public List<Container> getRunningStreamPipesContainer() {

        Map<String,String> allContainerLabels = new HashMap<>();
        allContainerLabels.putAll(DockerConstants.SP_DOCKER_CONTAINER_EXTENSIONS_LABELS);
        allContainerLabels.putAll(DockerConstants.SP_DOCKER_CONTAINER_BROKER_LABELS);

        return getContainerList()
                .stream()
                .filter(c -> c.labels().values().stream()
                        .anyMatch(v -> allContainerLabels.values().stream()
                                .anyMatch(cl -> cl.equals(v))
                        )
                )
                .collect(Collectors.toList());
    }

    private static boolean hasNvidiaRuntime() {
        boolean hasNvidiaRuntime = false;
        try {
            Process p = Runtime.getRuntime().exec(
                    "curl --unix-socket"
                            + BLANK_SPACE
                            + DOCKER_UNIX_SOCK
                            + BLANK_SPACE
                            + "http:/v" + apiVersion() + "/info");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = stdInput.readLine()) != null) {
                sb.append(line);
            }
            JsonObject rootObj = new JsonParser().parse(sb.toString()).getAsJsonObject();

            // check if runtime nvidia exists
            hasNvidiaRuntime = rootObj.getAsJsonObject("Runtimes")
                    .keySet()
                    .stream()
                    .anyMatch(e -> e.equals("nvidia"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hasNvidiaRuntime;
    }
}
