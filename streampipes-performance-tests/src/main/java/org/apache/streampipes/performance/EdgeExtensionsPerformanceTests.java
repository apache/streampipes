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
package org.apache.streampipes.performance;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.StreamPipesCredentials;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.List;

public class EdgeExtensionsPerformanceTests {

    public static void main (String ... args) {
        StreamPipesCredentials credentials = StreamPipesCredentials
                .from(System.getenv("user"), System.getenv("apiKey"));

        // Create an instance of the StreamPipes client
        StreamPipesClient client = StreamPipesClient
                .create("localhost", 8082, credentials, true);

        boolean shouldBeStopped = true;
        boolean shouldBeMigrated = false;
        boolean shouldBeReconfigured = false;

        // Get all pipelines
        List<Pipeline> pipelines = client.pipelines().all();

        Pipeline pipeline = pipelines.stream()
                .filter(p -> p.getName().equals("reconfigure"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));

        if (!pipeline.isRunning()) {
            // Start pipeline
            PipelineOperationStatus message = client.pipelines().start(pipelines.get(0));
            if (message.isSuccess()) {
                System.out.println(message.getTitle());
            }
        } else {
            // Stop pipeline

            if (shouldBeStopped) {
                PipelineOperationStatus message = client.pipelines().stop(pipelines.get(0));
                if(message.isSuccess()) {
                    System.out.println("Pipeline successfully stopped");
                }
            } else if (shouldBeMigrated) {
                // Migrate pipeline
                pipeline.getSepas().forEach(p -> {
                    p.setDeploymentTargetNodeId("edge-02.node-controller");
                    p.setDeploymentTargetNodeHostname("edge02.example.de");
                    p.setDeploymentTargetNodePort(7078);
                });

                PipelineOperationStatus message = client.pipelines().migrate(pipeline);
                if (message.isSuccess()) {
                    System.out.println(message.getTitle());
                }
            } else if (shouldBeReconfigured) {
                // Reconfigure pipeline
                pipeline.getSepas().forEach(p -> p.getStaticProperties().stream()
                        .filter(FreeTextStaticProperty.class::isInstance)
                        .map(FreeTextStaticProperty.class::cast)
                        .filter(FreeTextStaticProperty::isReconfigurable)
                        .forEach(sp -> {

                            if (sp.getInternalName().equals("i-am-reconfigurable")) {
                                sp.setValue("999");
                            }
                        }));

                PipelineOperationStatus message = client.pipelines().reconfigure(pipeline);
                if (message.isSuccess()) {
                    System.out.println(message.getTitle());
                }
            }
        }
    }
}
