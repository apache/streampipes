package org.streampipes.connect.firstconnector.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.SendToKafka;
import org.streampipes.connect.events.Event;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectFormat;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectParser;
import org.streampipes.connect.firstconnector.guess.SchemaGuesser;
import org.streampipes.connect.firstconnector.protocol.Protocol;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;
import org.streampipes.model.modelconnect.GuessSchema;
import org.streampipes.model.modelconnect.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class KafkaProtocol extends Protocol {

    Logger logger = LoggerFactory.getLogger(KafkaProtocol.class);

    public static String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/kafka";

    private Parser parser;
    private Format format;
    private String brokerUrl;
    private String topic;

    private Thread thread;
    private SpKafkaConsumer kafkaConsumer;

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
//        broker.setValue("141.21.42.75:9092");
//        topic.setValue("SEPA.SEP.Random.Number.Json");

        pd.addConfig(broker);
        pd.addConfig(topic);
        return pd;
    }

    @Override
    public GuessSchema getGuessSchema() {

        byte[] eventByte = getNByteElements(1).get(0);
        EventSchema eventSchema = parser.getEventSchema(eventByte);
        GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(20));

        return result;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        List<byte[]> resultEventsByte = getNByteElements(n);
        List<Map<String, Object>> result = new ArrayList<>();
        for (byte[] event : resultEventsByte) {
            result.add(format.parse(event));
        }

        return result;
    }

    private List<byte[]> getNByteElements(int n) {
        final Consumer<Long, String> consumer = createConsumer(this.brokerUrl, this.topic);

        consumer.subscribe(Arrays.asList(this.topic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                consumer.seekToBeginning(collection);
            }
        });

        List<byte[]> nEventsByte = new ArrayList<>();
        List<byte[]> resultEventsByte = new ArrayList<>();


        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            consumerRecords.forEach(record -> {
                try {
                    InputStream inputStream = IOUtils.toInputStream(record.value(), "UTF-8");

                    nEventsByte.addAll(parser.parseNEvents(inputStream, n));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if (nEventsByte.size() > n) {
                resultEventsByte = nEventsByte.subList(0, n);
                break;
            } else if (nEventsByte.size() == n) {
                resultEventsByte = nEventsByte;
                break;
            }

            consumer.commitAsync();
        }

        consumer.close();

        return resultEventsByte;
    }

    public static void main(String... args) {
        Parser parser = new JsonObjectParser();
        Format format = new JsonObjectFormat();
        KafkaProtocol kp = new KafkaProtocol(parser, format, "localhost:9092", "org.streampipes.examples.flowrate-1");
        GuessSchema gs = kp.getGuessSchema();

        System.out.println(gs);

    }


    private static Consumer<Long, String> createConsumer(String broker, String topic) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                broker);

        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "KafkaExampleConsumer" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, String> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(topic));

        return consumer;
    }


    @Override
    public void run(String broker, String topic) {
        SendToKafka stk = new SendToKafka(format, broker, topic);
        this.kafkaConsumer = new SpKafkaConsumer(this.brokerUrl, this.topic, new EventProcessor(stk));

        thread = new Thread(this.kafkaConsumer);
        thread.start();
    }

    @Override
    public void stop() {
        try {
            kafkaConsumer.disconnect();
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Kafka Adapter was sucessfully stopped");
        thread.interrupt();
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
