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
import org.apache.streampipes.messaging.mqtt.MqttConsumer;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrioOffloadingTest implements Test {

    private static final Logger LOG = LoggerFactory.getLogger(PrioOffloadingTest.class.getCanonicalName());

    private boolean stopPipeline;
    private final long timeToSleepBetweenSteps;
    private final StreamPipesClient client;

    private final String pipelineNameLow;
    private final String pipelineNameHigh;

    private Pipeline pipelineLowPrio;
    private Pipeline pipelineHighPrio;

    private final List<Tuple2<String, String>> cpuSteps;

    public PrioOffloadingTest(String pipelineNameLow,
                              String pipelineNameHigh,
                              boolean stopPipeline,
                              long timeToSleepBetweenSteps) {

        this.pipelineNameLow = pipelineNameLow;
        this.pipelineNameHigh = pipelineNameHigh;
        this.stopPipeline = stopPipeline;
        this.timeToSleepBetweenSteps = timeToSleepBetweenSteps;

        // Create an instance of the StreamPipes client
        client = createStreamPipesClient();

        pipelineLowPrio = findPipelineByName(pipelineNameLow);
        pipelineHighPrio = findPipelineByName(pipelineNameHigh);

        this.cpuSteps = loadFromEnv();
    }

    @Override
    public void setStopPipeline(boolean stopPipeline) {
        this.stopPipeline = stopPipeline;
    }

    @Override
    public void execute(int nrRuns) {

        Instant start = Instant.now();

        //Start Pipeline
        if (!pipelineLowPrio.isRunning() && !pipelineHighPrio.isRunning()) {
            startingPipelines();
            // provide some time prior to reconfigure values
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread initialCpuStepThread = getInitialCpuStepThread();
        // start offloading observer thread
        Thread offloadingObserverThread = offloadingObserverThread(initialCpuStepThread);

        initialCpuStepThread.start();
        offloadingObserverThread.start();

        try {

            LOG.info("Wait for offloading to take place");
            initialCpuStepThread.join();
            offloadingObserverThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        Duration remaining = Duration.ofMinutes(30).minus(elapsed.plus(Duration.ofMinutes(5)));

        adaptHighPrioPipelineCpuLoad();

        try {
            LOG.info("Sleep for remaining duration of offloading interval: {} min", remaining.toMinutes());
            Thread.sleep(remaining.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adaptHighLowPrioPipelineCpuLoad();

        Duration finalSegment =  Duration.ofMinutes(5);
        try {
            LOG.info("Sleep for final segment of offloading test: {} min", finalSegment.toMinutes());
            Thread.sleep(finalSegment.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Stop Pipeline
        if (stopPipeline && pipelineLowPrio.isRunning() && pipelineHighPrio.isRunning()) {
            stoppingPipeline(pipelineLowPrio);
            stoppingPipeline(pipelineHighPrio);
        }
    }


    // Helpers

    private void adaptHighPrioPipelineCpuLoad() {

        // Adjust high prio pipeline CPU load, leave low prio pipeline at given CPU load
        pipelineHighPrio = findPipelineByName(pipelineNameHigh);

        LOG.info("Prepare and reconfigure high prio pipeline {} with {} on node {}",
                pipelineHighPrio.getName(),
                "CPU Burner",
                findDeploymentTargetByProcessorName(pipelineHighPrio, "CPU Burner"));
        String highPrioCpuStepBeforeOffloading = cpuSteps.get(1).b;
        prepareAndReconfigurePipeline(pipelineHighPrio, "load", highPrioCpuStepBeforeOffloading);
    }

    private void adaptHighLowPrioPipelineCpuLoad() {
        LOG.info("Reduce CPU load for both high and low prio pipeline");

        // Adjust high prio pipeline CPU load, leave low prio pipeline at given CPU load
        pipelineLowPrio = findPipelineByName(pipelineNameLow);
        pipelineHighPrio = findPipelineByName(pipelineNameHigh);

        LOG.info("Prepare and reconfigure low prio pipeline {} with {} on node {}",
                pipelineLowPrio.getName(),
                "CPU Burner",
                findDeploymentTargetByProcessorName(pipelineLowPrio, "CPU Burner"));
        String lowPrioCpuStepBeforeOffloading = cpuSteps.get(0).a;
        prepareAndReconfigurePipeline(pipelineLowPrio, "load", lowPrioCpuStepBeforeOffloading);

        LOG.info("Prepare and reconfigure high prio pipeline {} with {} on node {}",
                pipelineHighPrio.getName(),
                "CPU Burner",
                findDeploymentTargetByProcessorName(pipelineHighPrio, "CPU Burner"));
        String highPrioCpuStepBeforeOffloading = cpuSteps.get(0).b;
        prepareAndReconfigurePipeline(pipelineHighPrio, "load", highPrioCpuStepBeforeOffloading);
    }

    private void startingPipelines() {
        PipelineOperationStatus startStatusLowPrio = client.pipelines().start(pipelineLowPrio);
        PipelineOperationStatus startStatusHighPrio = client.pipelines().start(pipelineHighPrio);

        String lowPrioNode = findDeploymentTargetByProcessorName(pipelineLowPrio, "CPU Burner");
        String highPrioNode = findDeploymentTargetByProcessorName(pipelineHighPrio, "CPU Burner");

        LOG.info("Start status low prio pipeline {} with {} on node {}",
                startStatusLowPrio.getTitle(),
                "CPU Burner",
                lowPrioNode);
        if (startStatusLowPrio.isSuccess()) {
            pipelineLowPrio.setRunning(true);
        }

        LOG.info("Start status high prio pipeline {} with {} on node {}",
                startStatusHighPrio.getTitle(),
                "CPU Burner",
                highPrioNode);
        if (startStatusHighPrio.isSuccess()) {
            pipelineHighPrio.setRunning(true);
        }
    }

    private StreamPipesClient createStreamPipesClient() {
        StreamPipesCredentials credentials = StreamPipesCredentials.from(
                System.getenv("SP_USER"),
                System.getenv("SP_API_KEY"));

        return StreamPipesClient
                .create(System.getenv("SP_HOST"),
                        Integer.parseInt(System.getenv("SP_PORT")),
                        credentials, true);
    }

    private void prepareAndReconfigurePipeline(Pipeline pipeline,
                                               String fstInternalName,
                                               String fstReconfigurationValue) {
        preparingPipeline(pipeline, fstInternalName, fstReconfigurationValue);
        reconfiguringPipeline(pipeline, fstReconfigurationValue);
    }

    private void preparingPipeline(Pipeline pipeline, String fstInternalName, String fstReconfigurationValue) {
        LOG.info("Set CPU load for {} pipeline to: {} percent",
                pipeline.getName(),
                Float.parseFloat(fstReconfigurationValue) * 100);

        pipeline.getSepas().forEach(processor -> processor.getStaticProperties().stream()
                .filter(FreeTextStaticProperty.class::isInstance)
                .map(FreeTextStaticProperty.class::cast)
                .filter(FreeTextStaticProperty::isReconfigurable)
                .forEach(fsp -> {
                    if (fsp.getInternalName().equals(fstInternalName)) {
                        fsp.setValue(fstReconfigurationValue);
                    }
                })
        );
    }

    private void reconfiguringPipeline(Pipeline pipeline, String fstReconfigurationValue) {
        LOG.info("Reconfiguration triggered with value " + fstReconfigurationValue);
        PipelineOperationStatus statusReconfiguration = client.pipelines().reconfigure(pipeline);
        if (!statusReconfiguration.isSuccess()) {
            LOG.info("Pipeline {} successfully reconfigured", pipeline.getName());
        }
    }

    private void stoppingPipeline(Pipeline pipeline) {
        PipelineOperationStatus stopStatus = client.pipelines().stop(pipeline);
        if (stopStatus.isSuccess()) {
            LOG.info("Stop status pipeline: " + stopStatus.getTitle());
            pipelineLowPrio.setRunning(false);
        } else {
            LOG.info("Pipeline {} could not be stopped." + stopStatus.getPipelineName());
        }
    }

    private Pipeline findPipelineByName(String pipelineName) {
        List<Pipeline> pipelines = client.pipelines().all();
        return pipelines.stream()
                .filter(p -> p.getName().equals(pipelineName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));
    }

    private String findDeploymentTargetByProcessorName(Pipeline pipeline, String processorName) {
        DataProcessorInvocation graph = pipeline.getSepas().stream()
                .filter(processor -> processor.getName().equals(processorName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Processor not found"));
        return graph.getDeploymentTargetNodeId();
    }

    private Thread offloadingObserverThread(Thread cpuStepThread) {
        LOG.info("Offloading observer thread started");
        return new Thread() {
            public synchronized void run() {
                String mqttLoggingUrlString = System.getenv("SP_LOGGING_MQTT_URL");
                try {
                    MqttTransportProtocol transportProtocol = makeMqttTransportProtocol(
                            new URI(mqttLoggingUrlString),
                            "Offloading");

                    MqttConsumer consumer = new MqttConsumer();
                    consumer.connect(transportProtocol, event -> {
                        String[] eventArray = new String(event, StandardCharsets.UTF_8).split(",");
                        boolean offloaded = Arrays.asList(eventArray).contains("offloading done");
                        if (offloaded) {
                            LOG.info("Low prio pipeline element offloaded {}", eventArray[4]);
                            cpuStepThread.interrupt();
                            try {
                                consumer.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Thread getInitialCpuStepThread() {
        LOG.info("Initial CPU step thread started");
        return new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int counter = 0;
                    if (cpuSteps.size() > 1) {
                        for (Tuple2<String, String> cpuStep : cpuSteps) {

                            if (counter > 0) {
                                LOG.info("Update pipeline locations");
                                pipelineLowPrio = findPipelineByName(pipelineNameLow);
                                pipelineHighPrio = findPipelineByName(pipelineNameHigh);
                            }

                            String lowPrioCpuStep = cpuStep.a;
                            String highPrioCpuStep = cpuStep.b;

                            String lowPrioNode = findDeploymentTargetByProcessorName(pipelineLowPrio, "CPU Burner");
                            String highPrioNode = findDeploymentTargetByProcessorName(pipelineHighPrio, "CPU Burner");

                            LOG.info("Prepare and reconfigure low prio pipeline {} with {} on node {}",
                                    pipelineLowPrio.getName(),
                                    "CPU Burner",
                                    lowPrioNode);
                            prepareAndReconfigurePipeline(pipelineLowPrio, "load", lowPrioCpuStep);

                            LOG.info("Prepare and reconfigure high prio pipeline {} with {} on node {}",
                                    pipelineHighPrio.getName(),
                                    "CPU Burner",
                                    highPrioNode);
                            prepareAndReconfigurePipeline(pipelineHighPrio, "load", highPrioCpuStep);

                            counter++;

                            Thread.sleep(timeToSleepBetweenSteps);
                        }
                    }
                } catch (InterruptedException e) {
                    LOG.info("Interrupted cpu steps thread");
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private MqttTransportProtocol makeMqttTransportProtocol(URI uri, String topic) {
        MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
        TopicDefinition simpleTopicDefinition = new SimpleTopicDefinition(topic);
        transportProtocol.setBrokerHostname(uri.getHost());
        transportProtocol.setPort(uri.getPort());
        transportProtocol.setTopicDefinition(simpleTopicDefinition);
        return transportProtocol;
    }

    private List<Tuple2<String, String>> loadFromEnv() {
        String[] cpuLoadStepsLowPrio = System.getenv("OFFLOADING_CPU_LOAD_STEPS_LOW_PRIO").split(";");
        String[] cpuLoadStepsHighPrio = System.getenv("OFFLOADING_CPU_LOAD_STEPS_HIGH_PRIO").split(";");

        if (cpuLoadStepsLowPrio.length != cpuLoadStepsHighPrio.length) {
            throw new RuntimeException("CPU load steps array must be of equal size");
        }

        List<Tuple2<String, String>> cpuSteps = new ArrayList<>();
        int length = cpuLoadStepsLowPrio.length;
        for (int i = 0; i < length; i++) {
            cpuSteps.add(new Tuple2<>(cpuLoadStepsLowPrio[i], cpuLoadStepsHighPrio[i]));
        }
        return cpuSteps;
    }

}
