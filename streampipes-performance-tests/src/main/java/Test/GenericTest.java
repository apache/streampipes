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
package Test;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.StreamPipesCredentials;
import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.List;

public class GenericTest implements Test{

    private final boolean stopPipeline;
    private final boolean shouldBeMigrated;
    private final boolean shouldBeReconfigured;
    private final long timeToSleepBeforeManipulation;
    private final long timeToSleepAfterManipulation;
    private final StreamPipesClient client;
    private final Pipeline pipeline;
    private final EvaluationLogger evalLogger = EvaluationLogger.getInstance();

    public GenericTest(String pipelineName, boolean stopPipeline, boolean shouldBeMigrated, boolean shouldBeReconfigured,
                       long timeToSleepBeforeManipulation, long timeToSleepAfterManipulation){
        this.stopPipeline = stopPipeline;
        this.shouldBeMigrated = shouldBeMigrated;
        this.shouldBeReconfigured = shouldBeReconfigured;
        this.timeToSleepBeforeManipulation = timeToSleepBeforeManipulation;
        this.timeToSleepAfterManipulation = timeToSleepAfterManipulation;
        StreamPipesCredentials credentials = StreamPipesCredentials
                .from(System.getenv("user"), System.getenv("apiKey"));
        // Create an instance of the StreamPipes client
        client = StreamPipesClient
                .create("localhost", 8082, credentials, true);
        List<Pipeline> pipelines = client.pipelines().all();
        pipeline = pipelines.stream()
                .filter(p -> p.getName().equals(pipelineName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));
    }

    @Override
    public void execute() {

        //Start Pipeline
        if (!pipeline.isRunning()) {
            long beforeStart = System.nanoTime();
            PipelineOperationStatus startMessage = client.pipelines().start(pipeline);
            long deploymentDuration = System.nanoTime() - beforeStart;
            Object[] line = {System.currentTimeMillis() ,"Deployment duration", deploymentDuration};
            evalLogger.addLine(line);
            if (startMessage.isSuccess()) {
                System.out.println(startMessage.getTitle());
            }
        }

        try {
            Thread.sleep(timeToSleepBeforeManipulation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Manipulate Pipeline
        //Migration
        if (shouldBeMigrated) {
            pipeline.getSepas().forEach(p -> {
                p.setDeploymentTargetNodeId("edge-02.node-controller");
                p.setDeploymentTargetNodeHostname("edge02.example.de");
                p.setDeploymentTargetNodePort(7078);
            });
            long beforeMigration = System.nanoTime();
            PipelineOperationStatus migrationMessage = client.pipelines().migrate(pipeline);
            long migrationDuration = System.nanoTime() - beforeMigration;
            Object[] line = {System.currentTimeMillis(), "Migration duration", migrationDuration};
            evalLogger.addLine(line);
            if (migrationMessage.isSuccess()) {
                System.out.println(migrationMessage.getTitle());
            }
        }
        //Reconfiguration
        if (shouldBeReconfigured) {
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

        try {
            Thread.sleep(timeToSleepAfterManipulation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Stop Pipeline
        if(stopPipeline && !pipeline.isRunning()){
            PipelineOperationStatus stopMessage = client.pipelines().stop(pipeline);
            if(stopMessage.isSuccess()) {
                System.out.println("Pipeline successfully stopped");
            }
        }
    }
}
