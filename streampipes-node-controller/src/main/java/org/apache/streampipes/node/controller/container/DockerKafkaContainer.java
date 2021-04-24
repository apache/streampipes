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
package org.apache.streampipes.node.controller.container;

import org.apache.streampipes.model.node.container.*;
import org.apache.streampipes.node.controller.config.NodeConfiguration;
import org.apache.streampipes.node.controller.management.orchestrator.docker.AbstractStreamPipesDockerContainer;


public class DockerKafkaContainer extends AbstractStreamPipesDockerContainer {

    @Override
    public DockerContainer declareDockerContainer() {
        return DockerContainerBuilder.create(StreamPipesDockerServiceID.SP_SVC_KAFKA_ID)
                .withImage("fogsyio/kafka:2.2.0")
                .withContainerName("streampipes-node-broker")
                .dependsOn("svc/org.apache.streampipes.node.broker.zookeeper")
                .withExposedPorts(Ports.withMapping("9094"))
                .withEnvironmentVariables(ContainerEnvBuilder.create()
                        .addNodeEnvs(generateStreamPipesNodeEnvs())
                        .add("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,OUTSIDE:PLAINTEXT")
                        .add("KAFKA_ADVERTISED_LISTENERS",
                                "PLAINTEXT://:9092,OUTSIDE://" + NodeConfiguration.getNodeHost() + ":9094")
                        .add("KAFKA_LISTENERS", "PLAINTEXT://:9092,OUTSIDE://:9094")
                        .add("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
                        .add("KAFKA_ADVERTISED_HOST_NAME", "streampipes-node-broker")
                        .add("KAFKA_ZOOKEEPER_CONNECT", "streampipes-node-zookeeper:2181")
                        .add("KAFKA_MESSAGE_MAX_BYTES", "5000012")
                        .add("KAFKA_FETCH_MESSAGE_MAX_BYTES", "5000012")
                        .add("KAFKA_REPLICA_FETCH_MAX_BYTES", "10000000")
                        .build())
                .withLabels(ContainerLabels.with(StreamPipesDockerServiceID.SP_SVC_KAFKA_ID, retrieveNodeType(),
                        ContainerType.BROKER))
                .withVolumes(ContainerVolumesBuilder.create()
                        .add("streampipes-kafka-vol", "/kafka", false)
                        .build())
                .build();
    }

}
