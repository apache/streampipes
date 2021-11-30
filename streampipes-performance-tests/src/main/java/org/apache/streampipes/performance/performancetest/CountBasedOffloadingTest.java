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
import java.util.*;

public class CountBasedOffloadingTest implements Test {

    private static final Logger LOG = LoggerFactory.getLogger(CountBasedOffloadingTest.class.getCanonicalName());

    private boolean stopPipeline;
    private final long timeToSleepBetweenSteps;
    private final StreamPipesClient client;
    private final String pipelineName;
    private Pipeline pipeline;
    private final String[] cpuSteps;
    private final int maxOffloadingActions;

    public CountBasedOffloadingTest(String pipelineName,
                                    boolean stopPipeline,
                                    long timeToSleepBetweenSteps) {

        this.pipelineName = pipelineName;
        this.stopPipeline = stopPipeline;
        this.timeToSleepBetweenSteps = timeToSleepBetweenSteps;

        // Create an instance of the StreamPipes client
        client = createStreamPipesClient();
        pipeline = findPipelineByName(pipelineName);

        this.cpuSteps = loadFromEnv();

        this.maxOffloadingActions =  System.getenv("MAX_OFFLOADING_ACTIONS") != null ?
                Integer.parseInt(System.getenv("MAX_OFFLOADING_ACTIONS")) : 50;
    }

    @Override
    public void setStopPipeline(boolean stopPipeline) {
        this.stopPipeline = stopPipeline;
    }

    @Override
    public void execute(int nrRuns) {

        //Start Pipeline
        if (!pipeline.isRunning()) {
            startPipeline();
            // provide some time prior to reconfigure values
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread rampUpThread = beginRampUpAndOffloadingInterval();

        // start offloading observer thread
        Thread offloadingObserverThread = offloadingObserverThread(rampUpThread);
        rampUpThread.start();
        offloadingObserverThread.start();

        try {
            LOG.info("Wait until max. number offloadings is completed: {}", maxOffloadingActions);
            rampUpThread.join();
            offloadingObserverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // reconfigure to base load and run for 5min
        adaptPipelineCpuLoad();

        Duration finalSegment =  Duration.ofMinutes(5);
        try {
            LOG.info("Sleep for final segment of offloading test: {} min", finalSegment.toMinutes());
            Thread.sleep(finalSegment.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Stop Pipeline
        if (stopPipeline && pipeline.isRunning()) {
            stopPipeline(pipeline);
        }
    }


    // Helpers

    private void adaptPipelineCpuLoad() {
        // Adjust pipeline CPU load
        pipeline = findPipelineByName(pipelineName);

        LOG.info("Prepare and reconfigure pipeline {} with {} on node {}",
                pipeline.getName(),
                "CPU Burner",
                findDeploymentTargetByProcessorName(pipeline, "CPU Burner"));

        prepareAndReconfigurePipeline(pipeline, "load", cpuSteps[0]);
    }

    private void startPipeline() {
        PipelineOperationStatus startStatus = client.pipelines().start(pipeline);

        String node = findDeploymentTargetByProcessorName(pipeline, "CPU Burner");

        LOG.info("Start status pipeline {} with {} on node {}",
                startStatus.getTitle(),
                "CPU Burner",
                node);
        if (startStatus.isSuccess()) {
            pipeline.setRunning(true);
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

    private void stopPipeline(Pipeline pipeline) {
        PipelineOperationStatus stopStatus = client.pipelines().stop(pipeline);
        if (stopStatus.isSuccess()) {
            LOG.info("Stop status pipeline: " + stopStatus.getTitle());
            this.pipeline.setRunning(false);
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

    private Thread offloadingObserverThread(Thread rampUpThread) {
        LOG.info("Offloading observer thread started");
        return new Thread() {
            public synchronized void run() {

                Map<String, Integer> offloadingDeviceMap = new HashMap<>();

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
                            LOG.info("Pipeline element offloaded {}", eventArray[4]);

                            String originDeviceId = eventArray[1];
                            if (offloadingDeviceMap.get(originDeviceId) == null) {
                                offloadingDeviceMap.put(originDeviceId, 1);
                                LOG.info("Current number of offloadings for {}: {}/{}",
                                        originDeviceId, 1, maxOffloadingActions);
                            } else {
                                int previousOffloadingCount = offloadingDeviceMap.get(originDeviceId);
                                offloadingDeviceMap.put(originDeviceId, previousOffloadingCount + 1);
                                LOG.info("Current number of offloadings for {}: {}/{}",
                                        originDeviceId, previousOffloadingCount + 1, maxOffloadingActions);
                            }

                            boolean reachedMaxOffloadingActions = offloadingDeviceMap.values()
                                    .stream()
                                    .allMatch(numOffloadingActions -> numOffloadingActions >= maxOffloadingActions);

                            if (reachedMaxOffloadingActions) {
                                LOG.info("Reached max offloading actions");
                                rampUpThread.interrupt();
                                try {
                                    consumer.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Thread beginRampUpAndOffloadingInterval() {
        LOG.info("Initial ramp up interval thread started");
        return new Thread() {
            public synchronized void run() {
                int counter = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (cpuSteps.length > 1 && counter != cpuSteps.length) {
                            for (String cpuStep : cpuSteps) {

                                if (counter > 0) {
                                    LOG.info("Update pipeline locations");
                                    pipeline = findPipelineByName(pipelineName);
                                }

                                String node = findDeploymentTargetByProcessorName(pipeline, "CPU Burner");

                                LOG.info("Prepare and reconfigure pipeline {} with {} on node {}",
                                        pipeline.getName(),
                                        "CPU Burner",
                                        node);
                                prepareAndReconfigurePipeline(pipeline, "load", cpuStep);
                                counter++;

                                Thread.sleep(timeToSleepBetweenSteps);
                            }
                        }
                    } catch (InterruptedException e) {
                        LOG.info("Interrupted cpu steps thread");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }

    private MqttTransportProtocol makeMqttTransportProtocol(URI uri, String topic) {
        MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
        TopicDefinition simpleTopicDefinition = new SimpleTopicDefinition(topic);
        transportProtocol.setBrokerHostname(uri.getHost());
        transportProtocol.setPort(uri.getPort());
        transportProtocol.setTopicDefinition(simpleTopicDefinition);
        return transportProtocol;
    }

    private String[] loadFromEnv() {
        String[] cpuLoadSteps = System.getenv("OFFLOADING_CPU_LOAD_STEPS") != null ? System.getenv(
                "OFFLOADING_CPU_LOAD_STEPS").split(";") : null;
        if (cpuLoadSteps == null) {
            throw new RuntimeException("CPU load steps must be provided");
        }
        return cpuLoadSteps;
    }

}
