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

import org.apache.streampipes.messaging.mqtt.MqttConsumer;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(CountBasedOffloadingTest.class.getCanonicalName());
    private static final int maxOffloadingActions = 2;

    private static volatile boolean maxOffloadingsReached = false;
    private static final String[] cpuSteps = new String[]{"0.2","0.5", "0.9"};

    public static void main(String ... args) throws Exception {

        // start offloading observer thread
        Thread initialRampUpThread = getInitialCpuStepThread();
        Thread offloadingObserverThread = offloadingObserverThread(initialRampUpThread);
        initialRampUpThread.start();
        offloadingObserverThread.start();

        try {
            LOG.info("Wait until max. number offloadings is completed: {}", maxOffloadingActions);
            initialRampUpThread.join();
            offloadingObserverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        LOG.info("Continue Outside...");

    }


    private static Thread offloadingObserverThread(Thread initialRampUpThread) {
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
                                maxOffloadingsReached = true;
                                initialRampUpThread.interrupt();
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

    private static MqttTransportProtocol makeMqttTransportProtocol(URI uri, String topic) {
        MqttTransportProtocol transportProtocol = new MqttTransportProtocol();
        TopicDefinition simpleTopicDefinition = new SimpleTopicDefinition(topic);
        transportProtocol.setBrokerHostname(uri.getHost());
        transportProtocol.setPort(uri.getPort());
        transportProtocol.setTopicDefinition(simpleTopicDefinition);
        return transportProtocol;
    }

    private static Thread getInitialCpuStepThread() {
        LOG.info("Initial CPU step thread started");
        return new Thread(() -> {
            int counter = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (cpuSteps.length > 1 & counter != cpuSteps.length) {
                        for (String cpuStep : cpuSteps) {

                            LOG.info("Reconfigure wiht {}", cpuStep);

                            counter++;

                            Thread.sleep(5000);
                        }
                    }
                } catch (InterruptedException e) {
                    LOG.info("Interrupted cpu steps thread");
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

}
