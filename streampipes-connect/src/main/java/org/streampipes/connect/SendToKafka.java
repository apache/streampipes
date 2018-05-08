package org.streampipes.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.messaging.kafka.SpKafkaProducer;

import java.util.Map;

public class SendToKafka implements EmitBinaryEvent {

    private Format format;

    private SpKafkaProducer producer;
    private ObjectMapper objectMapper;

    public SendToKafka(Format format, String brokerUrl, String topic) {
        this.format = format;

        producer = new SpKafkaProducer(brokerUrl, topic);
        objectMapper = new ObjectMapper();

    }

    @Override
    public Boolean emit(byte[] event) {

        Map<String, Object> result = format.parse(event);
        System.out.println("send to kafka: " + result);

        try {
            producer.publish(objectMapper.writeValueAsBytes(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return true;
    }
}
