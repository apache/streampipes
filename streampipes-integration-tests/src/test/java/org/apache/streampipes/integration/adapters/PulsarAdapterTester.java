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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.streampipes.connect.api.IFormat;
import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.integration.containers.PulsarContainer;
import org.bouncycastle.util.Strings;
import org.junit.Assert;

public class PulsarAdapterTester extends AdapterTesterBase {
    PulsarContainer pulsarContainer;
    PulsarProtocol pulsarProtocol;

    @Override
    public void startAdapterService() throws Exception {
        pulsarContainer = new PulsarContainer();
        pulsarContainer.start();
    }

    private Map<String, Object> convertDataToMap(byte[] data) {
        return new HashMap<>() {{
            put("value", Strings.fromByteArray(data));
        }};
    }

    @Override
    public void prepareAdapter() throws Exception {
        IFormat format = mock(IFormat.class);
        when(format.parse(any(byte[].class))).thenAnswer(invocation ->
                convertDataToMap(invocation.getArgument(0, byte[].class)));
        pulsarProtocol = new PulsarProtocol(null, format, pulsarContainer.getBrokerUrl(), "test-adapter");
    }

    @Override
    public List<Map<String, Object>> generateData() throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try (PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://" + pulsarContainer.getBrokerUrl())
                .build();
             Producer<byte[]> producer = client.newProducer().topic("test-adapter").create()) {
            for (int i = 0; i < 3; i++) {
                byte[] data = ("test-message-" + i).getBytes();
                producer.sendAsync(data);
                result.add(convertDataToMap(data));
            }
            producer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void validateData(List<Map<String, Object>> data) throws Exception {
        List<Map<String, Object>> messages = pulsarProtocol.getNElements(3);
        Assert.assertEquals(data.size(), messages.size());
        for (int i = 0; i < data.size(); i++) {
            Assert.assertEquals(data.get(i).get("value"), messages.get(i).get("value"));
        }
    }

    @Override
    public void close() throws Exception {
        if (pulsarContainer != null) {
            pulsarContainer.stop();
        }

    }
}
