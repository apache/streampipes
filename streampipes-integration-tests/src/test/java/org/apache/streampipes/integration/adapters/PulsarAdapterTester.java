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
package org.apache.streampipes.integration.adapters;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.streampipes.connect.adapter.format.json.arraykey.JsonFormat;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.integration.containers.PulsarContainer;
import org.apache.streampipes.integration.containers.PulsarDevContainer;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.rules.DebugSinkRuleDescription;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.junit.Assert;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class PulsarAdapterTester extends AdapterTesterBase {
    PulsarContainer pulsarContainer;
    private final static String TOPIC = "test-topic";

    @Override
    public void startAdapterService() {
        if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
            pulsarContainer = new PulsarDevContainer();
        } else {
            pulsarContainer = new PulsarContainer();
        }
        pulsarContainer.start();
    }

    @Override
    public AdapterDescription prepareAdapter() {
        GenericAdapterStreamDescription description = new GenericAdapterStreamDescription();

        // Configure format
        FormatDescription formatDescription = new FormatDescription();
        formatDescription.setAppId(JsonFormat.ID);
        var f = new FreeTextStaticProperty("key", "", "");
        f.setValue("timestamp");
        formatDescription.addConfig(f);
        description.setFormatDescription(formatDescription);

        // Configure protocol
        ProtocolDescription protocolDescription = new ProtocolDescription();
        protocolDescription.addConfig(FreeTextStaticProperty.of(PulsarProtocol.PULSAR_BROKER_HOST,
                pulsarContainer.getBrokerHost()));
        protocolDescription.addConfig(FreeTextStaticProperty.of(PulsarProtocol.PULSAR_BROKER_PORT,
                pulsarContainer.getBrokerPort().toString()));
        protocolDescription.addConfig(FreeTextStaticProperty.of(PulsarProtocol.PULSAR_TOPIC, TOPIC));
        protocolDescription.setAppId(PulsarProtocol.ID);
        description.setProtocolDescription(protocolDescription);

        // Configure debug sink rule
        description.setRules(Collections.singletonList(new DebugSinkRuleDescription()));

        return description;
    }

    @Override
    public List<Map<String, Object>> generateData() throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try (PulsarClient client = PulsarClient.builder().serviceUrl(
                        String.format("pulsar://%s:%s", pulsarContainer.getBrokerHost(),
                                pulsarContainer.getBrokerPort()))
                .build();
             Producer<byte[]> producer = client.newProducer().topic(TOPIC).create()) {
            ObjectMapper objectMapper = new ObjectMapper();

            for (int i = 0; i < 3; i++) {
                var dataMap = new HashMap<String, Object>();
                dataMap.put("timestamp", i);
                dataMap.put("value", "test-data");
                byte[] data = objectMapper.writeValueAsBytes(dataMap);
                producer.sendAsync(data);
                result.add(dataMap);
            }
            producer.flush();
        }
        return result;
    }

    @Override
    public void validateData(List<Map<String, Object>> expectedData) throws Exception {
        for (Map<String, Object> expected : expectedData) {
            Map<String, Object> actual = takeEvent();
            Assert.assertTrue(Maps.difference(expected, actual).areEqual());
        }
    }

    @Override
    public void close() {
        if (pulsarContainer != null) {
            pulsarContainer.stop();
        }
        try {
            stopAdapter();
        } catch (AdapterException e) {
            throw new RuntimeException(e);
        }
    }
}
