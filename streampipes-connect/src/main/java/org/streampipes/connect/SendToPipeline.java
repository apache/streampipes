/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.pipeline.AdapterPipeline;
import org.streampipes.messaging.kafka.SpKafkaProducer;

import java.util.Map;

public class SendToPipeline implements EmitBinaryEvent {

    private Format format;

    private SpKafkaProducer producer;
    private ObjectMapper objectMapper;

    private AdapterPipeline adapterPipeline;

    @Deprecated
    // TODO remove
    public SendToPipeline(Format format, String brokerUrl, String topic) {
        this.format = format;

        producer = new SpKafkaProducer(brokerUrl, topic);
        objectMapper = new ObjectMapper();
    }

    public SendToPipeline(Format format, AdapterPipeline adapterPipeline) {
       this.format = format;
       this.adapterPipeline = adapterPipeline;
    }

    @Override
    public Boolean emit(byte[] event) {

        Map<String, Object> result = format.parse(event);

        adapterPipeline.process(result);

        // TODO Get the rules


        // TODO Apply on events



//        try {
//            if (result != null) {
//                producer.publish(objectMapper.writeValueAsBytes(result));
//                System.out.println("send to kafka: " + result);
//            }
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        return true;
    }
}
