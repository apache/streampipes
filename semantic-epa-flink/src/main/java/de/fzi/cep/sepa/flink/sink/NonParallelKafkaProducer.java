package de.fzi.cep.sepa.flink.sink;

import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

/**
 * Created by riemer on 01.10.2016.
 */
public class NonParallelKafkaProducer<IN> extends RichSinkFunction<IN> {

    private SerializationSchema<IN> serializationSchema;
    private String brokerUrl;
    private String producerTopic;

    private EventProducer kafkaPublisher;


    public NonParallelKafkaProducer(String brokerUrl, String producerTopic, SerializationSchema<IN> serializationSchema) {
        this.brokerUrl = brokerUrl;
        this.producerTopic = producerTopic;
        this.serializationSchema = serializationSchema;
    }

    @Override
    public void open(Configuration configuration) throws Exception {
        try {
            this.kafkaPublisher = new StreamPipesKafkaProducer(this.brokerUrl, this.producerTopic);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void invoke(IN in) throws Exception {
        byte[] msg = serializationSchema.serialize(in);
        kafkaPublisher.publish(msg);
    }
}
