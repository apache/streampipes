package org.apache.streampipes.node.controller.container.deployment.utils;/*
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

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import com.spotify.docker.client.shaded.com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerUtils {

    static DockerClient dockerClient;

    static {
        try {
            dockerClient = DefaultDockerClient.fromEnv().build();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getDockerInfo() {
        try {
            Map<String, String> dockerInfoMap = new HashMap<>();

            Info info = dockerClient.info();
            Version version = dockerClient.version();

            //dockerInfoMap.put("architecture", info.architecture());
            dockerInfoMap.put("serverVersion", info.serverVersion());
            dockerInfoMap.put("apiVersion", version.apiVersion());
            dockerInfoMap.put("memTotal", Long.toString(info.memTotal()));
            dockerInfoMap.put("cpus", info.cpus().toString());
            dockerInfoMap.put("os",  version.os());
            dockerInfoMap.put("kernelVersion", version.kernelVersion());
            dockerInfoMap.put("arch", version.arch());

            return dockerInfoMap;
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static HostConfig getHostConfig(String network, String[] ports) {
        Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            portBindings.put(port + "/tcp", Collections.singletonList(PortBinding.of("0.0.0.0", port)));
        }

        return HostConfig.builder()
                .portBindings(portBindings)
                .networkMode(network)
                .build();
    }

    public static HostConfig getHostConfig(String network) {
        return HostConfig.builder()
                .networkMode(network)
                .build();
    }

    public static ContainerConfig.NetworkingConfig getNetworkingConfig(String network, String containerName) {
        return ContainerConfig.NetworkingConfig
                .create(new HashMap<String, EndpointConfig>() {{
                    put(network, getEndpointConfig(containerName));}}
                );

    }

    public static EndpointConfig getEndpointConfig(String containerName) {
        return EndpointConfig.builder()
                .aliases(ImmutableList.<String>of(containerName))
                .build();
    }

}
