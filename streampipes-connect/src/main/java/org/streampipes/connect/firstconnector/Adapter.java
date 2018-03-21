package org.streampipes.connect.firstconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.format.csv.CsvFormat;
import org.streampipes.connect.firstconnector.format.csv.CsvParser;
import org.streampipes.connect.firstconnector.format.json.JsonFormat;
import org.streampipes.connect.firstconnector.format.json.JsonParser;
import org.streampipes.connect.firstconnector.protocol.FileProtocol;
import org.streampipes.connect.firstconnector.protocol.HttpProtocol;
import org.streampipes.connect.firstconnector.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

public class Adapter {

    private static Map<String, Format> allFormats = new HashMap<>();
    private static Map<String, Protocol> allProtocols = new HashMap<>();
    private static Map<String, Parser> allParsers = new HashMap<>();


    Logger logger = LoggerFactory.getLogger(Adapter.class);
    private SpKafkaProducer producer;
    private boolean debug;

    public Adapter(String kafkaUrl, String topic, boolean debug) {
        allFormats.put(JsonFormat.ID, new JsonFormat());

        allFormats.put(CsvFormat.ID, new CsvFormat());

        allParsers.put(JsonFormat.ID, new JsonParser());
        allParsers.put(CsvFormat.ID, new CsvParser());

        allProtocols.put(HttpProtocol.ID, new HttpProtocol());
        allProtocols.put(FileProtocol.ID, new FileProtocol());

        this.debug = debug;

        if (!debug) {
            producer = new SpKafkaProducer(kafkaUrl, topic);
        }
    }

    public Adapter(String kafkaUrl, String topic) {
        this(kafkaUrl, topic, false);
    }


    public void run(AdapterDescription adapterDescription) {

        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        Protocol protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());

        protocol.run();

    }

    public EventSchema getSchema(AdapterDescription adapterDescription) {
        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        Protocol protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());

        return protocol.getSchema();
    }



    public void stop() {
        //TODO
    }

}
