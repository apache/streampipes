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

package org.apache.streampipes.sinks.brokers.jvm.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class NatsPublisher implements EventSink<NatsParameters> {

    private String subject;
    private Connection natsConnection;
    private JsonDataFormatDefinition dataFormatDefinition;
    private static Logger LOG;

    public NatsPublisher() {
        this.dataFormatDefinition = new JsonDataFormatDefinition();
    }

    @Override
    public void onInvocation(NatsParameters parameters, EventSinkRuntimeContext runtimeContext)
            throws SpRuntimeException {

        LOG = parameters.getGraph().getLogger(NatsPublisher.class);
        this.subject = parameters.getSubject();
        String natsUrls = parameters.getNatsUrls();
        String propertiesAsString = parameters.getProperties();
        String username = parameters.getUsername();
        String password = parameters.getPassword();

        Properties props = new Properties();

        if (username != null) {
            props.setProperty(Options.PROP_USERNAME, username);
            props.setProperty(Options.PROP_PASSWORD, password);
        }

        if (propertiesAsString != null && !propertiesAsString.isEmpty()) {
            splitNatsProperties(propertiesAsString, props);
        }

        String[] natsServerUrls = natsUrls.split(",");
        Options options;
        if (natsServerUrls.length > 1) {
            options = new Options.Builder(props).servers(natsServerUrls).build();
        } else {
            options = new Options.Builder(props).server(natsUrls).build();
        }

        try {
            this.natsConnection = Nats.connect(options);
        } catch (Exception e) {
            LOG.error("Error when connecting to the Nats broker on " + natsUrls + " . " + e.toString());
        }
    }

    @Override
    public void onEvent(Event inputEvent) {
        try {
            Map<String, Object> event = inputEvent.getRaw();
            natsConnection.publish(subject, dataFormatDefinition.fromMap(event));
        } catch (SpRuntimeException e) {
            LOG.error("Could not publish events to Nats broker. " + e.toString());
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        try {
            natsConnection.flush(Duration.ofMillis(50));
            natsConnection.close();
        } catch (TimeoutException | InterruptedException e) {
            LOG.error("Error when disconnecting with Nats broker. " + e.toString());
        }
    }

    private void splitNatsProperties(String propertiesAsString, Properties properties) {

        String[] optionalProperties = propertiesAsString.split(",");
        if (optionalProperties.length > 0) {
            for (String header : optionalProperties) {
                try {
                    String[] configPropertyWithValue = header.split(":", 2);
                    properties.setProperty(configPropertyWithValue[0].trim(), configPropertyWithValue[1].trim());
                } catch (Exception e) {
                    LOG.warn("Optional property '" + header + "' is not defined in the correct format.");
                }
            }
        }
    }

}
