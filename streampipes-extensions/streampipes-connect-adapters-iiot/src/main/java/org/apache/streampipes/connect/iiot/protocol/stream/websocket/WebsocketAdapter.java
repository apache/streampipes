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

package org.apache.streampipes.connect.iiot.protocol.stream.websocket;

import org.apache.commons.io.IOUtils;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.api.IAdapterPipeline;
import org.apache.streampipes.connect.api.IFormat;
import org.apache.streampipes.connect.api.IParser;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.connect.iiot.protocol.stream.BrokerProtocol;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebsocketAdapter extends BrokerProtocol {

    public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.websocket";

    public static final String URI_KEY = "uri";
    public static final String AUTH_KEY = "auth-key";
    public static final String MESSAGE_TYPE = "message-type";
    public static final String TEMPERATURE = "temperature-alternative";
    public static final String EULER = "euler-alternative";
    private String authToken = null;
    private String messageType = null;

    private SpWebSocketClient webSocketClient;

    public WebsocketAdapter() {}

    public WebsocketAdapter(IParser parser, IFormat format, String uri, String authToken, String messageType) {
        super(parser, format, uri, "");
        this.authToken = authToken;
        this.messageType = messageType;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
        StaticPropertyExtractor extractor =
                StaticPropertyExtractor.from(protocolDescription.getConfig(), new ArrayList<>());

        String uri = extractor.singleValueParameter(URI_KEY, String.class);
        String authToken = extractor.secretValue(AUTH_KEY);
        String selectedMessageType = extractor.selectedAlternativeInternalId(MESSAGE_TYPE);

        if (selectedMessageType.equals(TEMPERATURE)) {
            return new WebsocketAdapter(parser, format, uri, authToken, "temperature");
        } else {
            return new WebsocketAdapter(parser, format, uri, authToken, "euler");
        }

    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID)
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .category(AdapterType.Generic, AdapterType.Manufacturing)
                .sourceType(AdapterSourceType.STREAM)
                .requiredTextParameter(Labels.withId(URI_KEY))
                .requiredSecret(Labels.withId(AUTH_KEY))
                .requiredAlternatives(Labels.withId(MESSAGE_TYPE), Alternatives.from(Labels.withId(TEMPERATURE)), Alternatives.from(Labels.withId(EULER)))
                .build();
    }

    @Override
    public void run(IAdapterPipeline adapterPipeline) {
        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
        HashMap<String, String> httpHeader = new HashMap<>();
        httpHeader.put("Authorization", "Bearer " + this.authToken);
        this.webSocketClient = new SpWebSocketClient(URI.create(this.brokerUrl), new WebsocketAdapter.EventProcessor(stk, this.messageType), httpHeader);

        this.webSocketClient.connect();

    }

    @Override
    public void stop() {
        this.webSocketClient.close();
    }

    @Override
    public String getId() {
        return WebsocketAdapter.ID;
    }

    @Override
    protected List<byte[]> getNByteElements(int n) throws ParseException {
        List<byte[]> elements = new ArrayList<>();
        InternalEventProcessor<byte[]> eventProcessor = elements::add;

        SpWebSocketClient socketClient = new SpWebSocketClient(URI.create(this.brokerUrl), eventProcessor);

        socketClient.connect();

        while (socketClient.getMessageCount() < n) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        socketClient.close();
        return elements;
    }

    private class EventProcessor implements InternalEventProcessor<byte[]> {
        private SendToPipeline stk;
        private String messageType;

        public EventProcessor(SendToPipeline stk, String messageType) {
            this.stk = stk;
            this.messageType = messageType;
        }

        @Override
        public void onEvent(byte[] payload) {

            String message = new String(payload);

            if ((this.messageType.equals("temperature") && message.contains("temperature")) || (this.messageType.equals("euler") && message.contains("euler"))){
                try {
                    parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
                } catch (ParseException e) {
                    e.printStackTrace();
                    //logger.error("Adapter " + ID + " could not read value!",e);
                }
            }
        }
    }
}
