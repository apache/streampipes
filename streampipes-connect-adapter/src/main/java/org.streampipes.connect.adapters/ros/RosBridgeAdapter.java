/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapters.ros;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import org.json.JSONObject;
import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.format.json.object.JsonObjectFormat;
import org.streampipes.connect.adapter.format.json.object.JsonObjectParser;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.connect.adapter.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RosBridgeAdapter extends SpecificDataStreamAdapter {

    public static final String ID = "http://streampipes.org/adapter/specific/ros";

    private static final String ROS_HOST_KEY = "ROS_HOST_KEY";
    private static final String ROS_PORT_KEY = "ROS_PORT_KEY";
    private static final String TOPIC_KEY = "TOPIC_KEY";

    private String topic;
    private String host;
    private int port;

    private Ros ros;

    private JsonObjectParser jsonObjectParser;

    public RosBridgeAdapter() {
    }

    public RosBridgeAdapter(SpecificAdapterStreamDescription adapterDescription) {
        super(adapterDescription);

        getConfigurations(adapterDescription);
        
        this.jsonObjectParser = new JsonObjectParser();
    }

    @Override
    public SpecificAdapterStreamDescription declareModel() {
        SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID, "ROS Bridge", "Connect Robots running on ROS")
                .iconUrl("ros.png")
                .category(AdapterType.Manufacturing)
                .requiredTextParameter(Labels.from(ROS_HOST_KEY, "Ros Bridge", "Example: test-server.com (No protocol) "))
                .requiredTextParameter(Labels.from(ROS_PORT_KEY, "Port", "Example: 9090"))
                .requiredTextParameter(Labels.from(TOPIC_KEY, "Topic", "Example: /battery (Starts with /) "))
                .build();
        description.setAppId(ID);


        return  description;
    }

    @Override
    public void startAdapter() throws AdapterException {
        this.ros = new Ros(this.host, this.port);
        this.ros.connect();

        String topicType = getMethodType(this.ros, this.topic);

        Topic echoBack = new Topic(ros, this.topic, topicType);
        echoBack.subscribe(new TopicCallback() {
            @Override
            public void handleMessage(Message message) {

                InputStream stream = new ByteArrayInputStream(message.toString().getBytes(StandardCharsets.UTF_8));

                jsonObjectParser.parse(stream, new ParseData());
            }
        });


    }

    private class GetNEvents implements Runnable {

        private String topic;
        private String topicType;
        private Ros ros;

        private List<byte[]> events;

        public GetNEvents(String topic, String topicType, Ros ros) {
            this.topic = topic;
            this.topicType = topicType;
            this.ros = ros;
            this.events = new ArrayList<>();
        }

        @Override
        public void run() {
            Topic echoBack = new Topic(ros, this.topic, topicType);
            echoBack.subscribe(new TopicCallback() {
                @Override
                public void handleMessage(Message message) {
                   events.add(message.toString().getBytes());
                }
            });
        }

        public List<byte[]> getEvents() {
            return this.events;
        }
    }

    @Override
    public void stopAdapter() throws AdapterException {
        this.ros.disconnect();
    }

    @Override
    public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
        getConfigurations(adapterDescription);


        Ros ros = new Ros(host, port);

        boolean connect = ros.connect();

        if (!connect) {
            throw new AdapterException("Could not connect to ROS bridge Endpoint: " + host + " with port: " + port);
        }

        String topicType = getMethodType(ros, topic);

        GetNEvents getNEvents = new GetNEvents(topic, topicType, ros);
        Thread t = new Thread(getNEvents);
        t.start();

        while (getNEvents.getEvents().size() < 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        t.interrupt();

        ros.disconnect();

        EventSchema eventSchema = this.jsonObjectParser.getEventSchema(getNEvents.getEvents());

        GuessSchema guessSchema = new GuessSchema();

        guessSchema.setEventSchema(eventSchema);
        guessSchema.setPropertyProbabilityList(new ArrayList<>());
        return guessSchema;
    }

    @Override
    public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
        return new RosBridgeAdapter(adapterDescription);
    }

    @Override
    public String getId() {
        return ID;
    }

    private String getMethodType(Ros ros, String topic) {
        Service addTwoInts = new Service(ros, "/rosapi/topic_type", "rosapi/TopicType");
        ServiceRequest request = new ServiceRequest("{\"topic\": \""+ topic +"\"}");
        ServiceResponse response = addTwoInts.callServiceAndWait(request);

        JSONObject ob = new JSONObject(response.toString());
        return ob.getString("type");
    }

    private class ParseData implements EmitBinaryEvent {

        private JsonObjectFormat jsonObjectFormat;

        public ParseData() {
            this.jsonObjectFormat = new JsonObjectFormat();
        }

        @Override
        public Boolean emit(byte[] event) {
            Map<String, Object> result = this.jsonObjectFormat.parse(event);
            adapterPipeline.process(result);
            return true;
        }
    }

    private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
        ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
        String host = extractor.singleValue(ROS_HOST_KEY, String.class);
        String topic = extractor.singleValue(TOPIC_KEY, String.class);
        int port = extractor.singleValue(ROS_PORT_KEY, Integer.class);
    }

    // Ignore for now, but is interesting for future implementations
    private void getListOfAllTopics() {
        // Get a list of all topics
//        Service addTwoInts = new Service(ros, "/rosapi/topics", "rosapi/Topics");
//        ServiceRequest request = new ServiceRequest();
//        ServiceResponse response = addTwoInts.callServiceAndWait(request);
//        System.out.println(response.toString());
    }


}
