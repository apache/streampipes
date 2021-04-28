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
package org.apache.streampipes.node.controller.management.orchestrator.docker;


import org.apache.streampipes.model.node.container.DockerContainer;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.container.StreamPipesDockerServiceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DockerContainerDeclarerSingleton {
    private static final Logger LOG =
            LoggerFactory.getLogger(DockerContainerDeclarerSingleton.class.getCanonicalName());

    private static DockerContainerDeclarerSingleton instance;
    private final Map<String, DockerContainer> dockerContainers;

    private DockerContainerDeclarerSingleton() {
        this.dockerContainers = new HashMap<>();
    }

    public static DockerContainerDeclarerSingleton getInstance() {
        if (DockerContainerDeclarerSingleton.instance == null) {
            DockerContainerDeclarerSingleton.instance = new DockerContainerDeclarerSingleton();
        }
        return DockerContainerDeclarerSingleton.instance;
    }

    public DockerContainerDeclarerSingleton register(AbstractStreamPipesDockerContainer container) {
        LOG.info("Register container for auto-deploy: " + container.declareDockerContainer().getServiceId());
        this.dockerContainers.put(
                container.declareDockerContainer().getServiceId(),
                container.declareDockerContainer());
        return getInstance();
    }

    public Map<String, DockerContainer> getDockerContainers() {
        return new HashMap<>(dockerContainers);
    }

    public List<DockerContainer> getAllDockerContainerAsList() {
        return new ArrayList<>(dockerContainers.values());
    }

    public List<DockerContainer> getAutoDeploymentDockerContainers() {
        if ("kafka".equals(NodeConfiguration.getNodeBrokerProtocol())) {
            remove(StreamPipesDockerServiceID.SP_SVC_MOSQUITTO_ID);
        } else {
            remove(StreamPipesDockerServiceID.SP_SVC_KAFKA_ID, StreamPipesDockerServiceID.SP_SVC_ZOOKEEPER_ID);
        }
        LinkedHashMap<String, DockerContainer> sorted = sort();
        return new ArrayList<>(sorted.values());
    }

    // Helpers

    private LinkedHashMap<String, DockerContainer> sort() {
        Map<String, List<String>> dependencyGraph = new HashMap<>();

        // initialize
        dockerContainers.keySet().forEach(k -> dependencyGraph.put(k, new ArrayList<>()));

        // add dependencies
        dockerContainers.values().forEach(container -> {
            if (container.getDependsOnContainers().size() > 0) {
                container.getDependsOnContainers().forEach(d -> {
                    dependencyGraph.get(d).add(container.getServiceId());
                });
            }
        });

        LinkedHashMap<String, DockerContainer> sortedContainerList = new LinkedHashMap<>();
        for (String container: topologicalSortBFS(dependencyGraph)) {
            sortedContainerList.put(container, dockerContainers.get(container));
        }

        return sortedContainerList;
    }

    private List<String> topologicalSortBFS(Map<String, List<String>> graph){
        Map<String, Integer> indegree = new HashMap<>();
        for(String k : graph.keySet()){
            if(!indegree.containsKey(k))
                indegree.put(k, 0);
            for(String d : graph.get(k))
                indegree.put(d, 1 + indegree.getOrDefault(d, 0));
        }

        Queue<String> q = new LinkedList<>();

        for(String v : indegree.keySet()) {
            if(indegree.get(v)==0)
                q.add(v);
        }

        List<String> topologicalSort = new ArrayList<>();
        while(!q.isEmpty()){
            String vertex = q.remove();
            topologicalSort.add(vertex);
            for(String nextVertex : graph.get(vertex)){
                indegree.put(nextVertex, indegree.get(nextVertex)-1);
                if(indegree.get(nextVertex)==0)
                    q.add(nextVertex);
            }
        }
        return topologicalSort;
    }

    private void remove(String... keys) {
        Set<String> filterPredicate = new HashSet<>(Arrays.asList(keys));
        dockerContainers.keySet().removeAll(filterPredicate);
    }
}
