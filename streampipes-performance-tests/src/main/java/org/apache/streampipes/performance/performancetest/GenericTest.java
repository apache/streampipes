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
package org.apache.streampipes.performance.performancetest;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.StreamPipesCredentials;
import org.apache.streampipes.logging.evaluation.EvaluationLogger;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.List;
import java.util.Optional;

public class GenericTest implements Test{

    private boolean stopPipeline;
    private final boolean shouldBeMigrated;
    private final boolean shouldBeReconfigured;
    private final long timeToSleepBeforeManipulation;
    private final long timeToSleepAfterManipulation;
    private final StreamPipesClient client;
    private final Pipeline pipeline;
    private final EvaluationLogger evalLogger = EvaluationLogger.getInstance();
    private float reconfigurableValue = 1;

    public GenericTest(String pipelineName, boolean stopPipeline, boolean shouldBeMigrated, boolean shouldBeReconfigured,
                       long timeToSleepBeforeManipulation, long timeToSleepAfterManipulation){
        this.stopPipeline = stopPipeline;
        this.shouldBeMigrated = shouldBeMigrated;
        this.shouldBeReconfigured = shouldBeReconfigured;
        this.timeToSleepBeforeManipulation = timeToSleepBeforeManipulation;
        this.timeToSleepAfterManipulation = timeToSleepAfterManipulation;
        StreamPipesCredentials credentials = StreamPipesCredentials
                .from(System.getenv("SP_USER"), System.getenv("SP_API_KEY"));
        // Create an instance of the StreamPipes client
        client = StreamPipesClient
                .create(System.getenv("SP_HOST"), Integer.parseInt(System.getenv("SP_PORT")), credentials, true);
        List<Pipeline> pipelines = client.pipelines().all();
        pipeline = pipelines.stream()
                .filter(p -> p.getName().equals(pipelineName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));
    }

    @Override
    public void setStopPipeline(boolean stopPipeline){
        this.stopPipeline = stopPipeline;
    }

    @Override
    public void execute(int nrRuns) {

        String testType = System.getenv("TEST_TYPE");
        String offloadingThreshold = System.getenv("OFFLOADING_THRESHOLD");

        Object[] line = null;

        if (testType.equals("Reconfiguration") && nrRuns == 0){
            executeOffloading();
            return;
        }
        //Start Pipeline
        if (!pipeline.isRunning()) {
            long beforeStart = System.nanoTime();
            PipelineOperationStatus startMessage = client.pipelines().start(pipeline);
            long deploymentDuration = System.nanoTime() - beforeStart;
            if(testType.equals("Deployment")){
                line = new Object[]{System.currentTimeMillis(), "Deployment duration", nrRuns, deploymentDuration, deploymentDuration / 1000000000.0, true};
            }
            System.out.println(startMessage.getTitle());
            if (startMessage.isSuccess()) {
                pipeline.setRunning(true);
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
             Tuple2<String, String> migrationNodes = prepareMigration();
            long beforeMigration = System.nanoTime();
            PipelineOperationStatus migrationMessage = client.pipelines().migrate(pipeline);
            long migrationDuration = System.nanoTime() - beforeMigration;
            if(testType.equals("Migration")){
                line = new Object[]{"Migration duration", nrRuns, migrationDuration, migrationDuration/1000000000.0,migrationNodes.a, migrationNodes.b, true};
            }
            System.out.println(migrationMessage.getTitle());
            if (!migrationMessage.isSuccess()) {
                assert line != null;
                line[line.length -1] = false;
            }
        }
        //Reconfiguration
        if (shouldBeReconfigured) {
            if (testType.equals("Reconfiguration"))
                pipeline.getSepas().forEach(p -> p.getStaticProperties().stream()
                    .filter(FreeTextStaticProperty.class::isInstance)
                    .map(FreeTextStaticProperty.class::cast)
                    .filter(FreeTextStaticProperty::isReconfigurable)
                    .forEach(sp -> {
                        if (sp.getInternalName().equals("i-am-reconfigurable")) {
                            sp.setValue(Float.toString(this.reconfigurableValue++));
                        }
                    }));
            else if (testType.equals("Offloading"))
                pipeline.getSepas().forEach(p -> p.getStaticProperties().stream()
                        .filter(FreeTextStaticProperty.class::isInstance)
                        .map(FreeTextStaticProperty.class::cast)
                        .filter(FreeTextStaticProperty::isReconfigurable)
                        .forEach(sp -> {
                            if (sp.getInternalName().equals("load")) {
                                sp.setValue(offloadingThreshold != null ? offloadingThreshold : "0.9");
                            }
                        }));
            line = new Object[]{"Reconfiguration triggered", nrRuns, (this.reconfigurableValue - 1), true};
            System.out.println("Reconfiguration triggered with value " + (this.reconfigurableValue-1));
            PipelineOperationStatus message = client.pipelines().reconfigure(pipeline);
            System.out.println(message.getTitle());
            if (!message.isSuccess()) {
                line[line.length -1] = false;
            }
        }

        try {
            if(shouldBeMigrated){
                long sleepTime = timeToSleepAfterManipulation + (long)(Math.random()*10000);
                Thread.sleep(sleepTime);
            }else {
                Thread.sleep(timeToSleepAfterManipulation);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Stop Pipeline
        if(stopPipeline && pipeline.isRunning()){
            PipelineOperationStatus stopMessage = client.pipelines().stop(pipeline);
            if(stopMessage.isSuccess()) {
                System.out.println("Pipeline successfully stopped");
                pipeline.setRunning(false);
            }
            else{
                System.out.println("Pipeline could not be stopped.");
                assert line != null;
                line[line.length -1] = false;
            }
        }
        assert line != null;
        evalLogger.logMQTT(testType, line);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeOffloading(){
        if (!pipeline.isRunning()) {
            PipelineOperationStatus startMessage = client.pipelines().start(pipeline);
            System.out.println(startMessage.getTitle());
            if (startMessage.isSuccess()) {
                pipeline.setRunning(true);
            }
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float[] reconfigurationValues = {0, 0, 0, 0, 0, 1, 2, 3, 4, 5};

        for(int i = 0; i<200; i++){

            float reconfigurationValue = reconfigurationValues[i%10];
            pipeline.getSepas().forEach(p -> p.getStaticProperties().stream()
                    .filter(FreeTextStaticProperty.class::isInstance)
                    .map(FreeTextStaticProperty.class::cast)
                    .filter(FreeTextStaticProperty::isReconfigurable)
                    .forEach(sp -> {
                        if (sp.getInternalName().equals("i-am-reconfigurable")) {
                            sp.setValue(Float.toString(reconfigurationValue));
                        }
                    }));

            PipelineOperationStatus message = client.pipelines().reconfigure(pipeline);
            System.out.println(message.getTitle());
        }


    }


    private Tuple2<String, String> prepareMigration(){
        String nodeFrom = "";
        String nodeTo = "";
        String appId = System.getenv("MIGRATION_ENTITY_APP_ID");
        String[] node1 = System.getenv("MIGRATION_NODE_1").split(";");
        String[] node2 = System.getenv("MIGRATION_NODE_2").split(";");
        Optional<DataProcessorInvocation> processor = pipeline.getSepas().stream().filter(p -> p.getAppId().equals(appId)).findFirst();
        if (processor.isPresent()){
            if (processor.get().getDeploymentTargetNodeId().equals(node1[0])){
                nodeFrom = node1[0];
                nodeTo = node2[0];
                processor.get().setDeploymentTargetNodeId(node2[0]);
                processor.get().setDeploymentTargetNodeHostname(node2[1]);
                processor.get().setDeploymentTargetNodePort(Integer.parseInt(node2[2]));
            } else{
                nodeFrom = node2[0];
                nodeTo = node1[0];
                processor.get().setDeploymentTargetNodeId(node1[0]);
                processor.get().setDeploymentTargetNodeHostname(node1[1]);
                processor.get().setDeploymentTargetNodePort(Integer.parseInt(node1[2]));
            }
        }
        return new Tuple2<>(nodeFrom, nodeTo);
    }
}
