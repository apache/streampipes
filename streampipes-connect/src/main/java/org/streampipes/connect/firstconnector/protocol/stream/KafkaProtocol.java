package org.streampipes.connect.firstconnector.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.SendToKafka;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.protocol.Protocol;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.modelconnect.GuessSchema;
import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KafkaProtocol extends Protocol {

    Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);

    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/kafka";

    private Parser parser;
    private Format format;
    private String brokerUrl;
    private String topic;

    public KafkaProtocol() {
    }

    public KafkaProtocol(Parser parser, Format format, String brokerUrl, String topic) {
        this.parser = parser;
        this.format = format;
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        String brokerUrl = extractor.singleValue("broker_url");
        String topic = extractor.singleValue("topic");

        return new KafkaProtocol(parser, format, brokerUrl, topic);
    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription pd = new ProtocolDescription(ID,"Apache Kafka (Stream)","This is the " +
                "description for the Apache Kafka protocol");
        FreeTextStaticProperty broker = new FreeTextStaticProperty("broker_url", "Broker URL",
                "This property defines the URL of the Kafka broker.");


        pd.setSourceType("STREAM");

        FreeTextStaticProperty topic = new FreeTextStaticProperty("topic", "Topic",
                "Topic in the broker");

        //TODO remove, just for debugging
        broker.setValue("141.21.42.75:9092");
        topic.setValue("SEPA.SEP.Random.Number.Json");

        pd.addConfig(broker);
        pd.addConfig(topic);
        return pd;
    }

    @Override
    public GuessSchema getSchema() {
        GuessSchema result = new GuessSchema();
        result.setEventSchema(parser.getSchema(null));
        return result;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        return null;
    }

    @Override
    public void run(String broker, String topic) {

        SendToKafka stk = new SendToKafka(format, broker, topic);

//        SpKafkaConsumer kafkaConsumer = new SpKafkaConsumer(brokerUrl, topic, new EventProcessor(stk));
//        kafkaConsumer.run();

        Thread thread = new Thread(new SpKafkaConsumer(this.brokerUrl, this.topic, new EventProcessor(stk)));
        thread.start();

        System.out.println("bl");
    }

    private class EventProcessor implements InternalEventProcessor<byte[]> {
        private SendToKafka stk;
        public EventProcessor(SendToKafka stk) {
           this.stk = stk;
        }

        @Override
        public void onEvent(byte[] payload) {
            try {
                parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
            } catch (IOException e) {
                logger.error("Adapter " + ID + " could not read value!",e);
            }
        }
    }

    @Override
    public String getId() {
        return ID;
    }

}
