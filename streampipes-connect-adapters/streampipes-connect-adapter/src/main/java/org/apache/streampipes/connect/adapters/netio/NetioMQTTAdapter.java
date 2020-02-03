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

package org.apache.streampipes.connect.adapters.netio;

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.connect.protocol.stream.MqttConfig;
import org.apache.streampipes.connect.protocol.stream.MqttConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetioMQTTAdapter extends SpecificDataStreamAdapter {



    private MqttConsumer mqttConsumer;
    private MqttConfig mqttConfig;
    private Thread thread;

    /**
     * A unique id to identify the adapter type
     */
    public static final String ID = "org.apache.streampipes.connect.adapters.netio.mqtt";

    /**
     * Keys of user configuration parameters
     */
    private static final String ACCESS_MODE = "access-mode";
    private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
    private static final String USERNAME_ACCESS = "username-alternative";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    /**
     * Values of user configuration parameters
     */
    private String ip;

    /**
     * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
     */
    public NetioMQTTAdapter() {
    }

    public NetioMQTTAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);
    }


    /**
     * Describe the adapter adapter and define what user inputs are required. Currently users can just select one node, this will be extended in the future
     * @return
     */
    @Override
    public SpecificAdapterStreamDescription declareModel() {

        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .category(AdapterType.Energy)
                .requiredAlternatives(Labels.from(ACCESS_MODE, "Access Mode", ""),
                        Alternatives.from(Labels.from(ANONYMOUS_ACCESS, "Unauthenticated", "")),
                        Alternatives.from(Labels.from(USERNAME_ACCESS, "Username/Password", ""),
                                StaticProperties.group(Labels.withId("username-group"),
                                        StaticProperties.stringFreeTextProperty(Labels.from(USERNAME,
                                                "Username", "")),
                                        StaticProperties.secretValue(Labels.from(PASSWORD,
                                                "Password", "")))))
                .build();
        description.setAppId(ID);


        return description;
    }

    /**
     * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
     * @param adapterDescription
     * @return
     * @throws AdapterException
     */
    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
        return NetioUtils.getNetioSchema();
    }
    @Override
    public void startAdapter() throws AdapterException {
        this.mqttConsumer = new MqttConsumer(this.mqttConfig, new EventProcessor(adapterPipeline));

        thread = new Thread(this.mqttConsumer);
        thread.start();
    }

    @Override
    public void stopAdapter() throws AdapterException {
        this.mqttConsumer.close();
    }
    /**
     * Required by StreamPipes return a new adapter instance by calling the constructor with SpecificAdapterStreamDescription
     * @param adapterDescription
     * @return
     */
    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new NetioMQTTAdapter(adapterDescription);
    }


    /**
     * Required by StreamPipes. Return the id of the adapter
     * @return
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * Extracts the user configuration from the SpecificAdapterStreamDescription and sets the local variales
     * @param adapterDescription
     */
    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

        String brokerUrl = extractor.singleValueParameter("broker_url", String.class);
        String topic = extractor.singleValueParameter("topic", String.class);
        String selectedAlternative = extractor.selectedAlternativeInternalId("access_mode");

        if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
            mqttConfig = new MqttConfig(brokerUrl, topic);
        } else {
            String username = extractor.singleValueParameter(USERNAME, String.class);
            String password = extractor.secretValue(PASSWORD);
            mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
        }
    }

    private class EventProcessor implements InternalEventProcessor<byte[]> {
        private AdapterPipeline adapterPipeline;

        public EventProcessor(AdapterPipeline adapterpipeline) {
            this.adapterPipeline = adapterpipeline;
        }

        @Override
        public void onEvent(byte[] payload) {
            Map<String, Object> result = parseEvent(new String(payload));
            adapterPipeline.process(result);
        }
    }

    public static Map<String, Object> parseEvent(String s) {
       return new HashMap<>();
    }

}
