package org.streampipes.connect.firstconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectFormat;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectParser;
import org.streampipes.connect.firstconnector.protocol.stream.KafkaProtocol;
import org.streampipes.model.modelconnect.AdapterDescription;
import org.streampipes.model.modelconnect.GuessSchema;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.format.csv.CsvFormat;
import org.streampipes.connect.firstconnector.format.csv.CsvParser;
import org.streampipes.connect.firstconnector.format.json.arraykey.JsonFormat;
import org.streampipes.connect.firstconnector.format.json.arraykey.JsonParser;
import org.streampipes.connect.firstconnector.protocol.set.FileProtocol;
import org.streampipes.connect.firstconnector.protocol.set.HttpProtocol;
import org.streampipes.connect.firstconnector.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

public class Adapter {

    private static Map<String, Format> allFormats = new HashMap<>();
    private static Map<String, Protocol> allProtocols = new HashMap<>();
    private static Map<String, Parser> allParsers = new HashMap<>();

    private String kafkaUrl;
    private String topic;

    Logger logger = LoggerFactory.getLogger(Adapter.class);
    private boolean debug;

    public Adapter(String kafkaUrl, String topic, boolean debug) {
        this.kafkaUrl = kafkaUrl;
        this.topic = topic;

        allFormats.put(JsonFormat.ID, new JsonFormat());
        allFormats.put(JsonObjectFormat.ID, new JsonObjectFormat());

        allFormats.put(CsvFormat.ID, new CsvFormat());

        allParsers.put(JsonFormat.ID, new JsonParser());
        allParsers.put(JsonObjectFormat.ID, new JsonObjectParser());
        allParsers.put(CsvFormat.ID, new CsvParser());

        allProtocols.put(HttpProtocol.ID, new HttpProtocol());
        allProtocols.put(FileProtocol.ID, new FileProtocol());
        allProtocols.put(KafkaProtocol.ID, new KafkaProtocol());

        this.debug = debug;
    }

    public Adapter(String kafkaUrl, String topic) {
        this(kafkaUrl, topic, false);
    }


    public void run(AdapterDescription adapterDescription) {

        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        Protocol protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());

        protocol.run(this.kafkaUrl, this.topic);

    }

    public GuessSchema getSchema(AdapterDescription adapterDescription) {
        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        Protocol protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());

        return protocol.getGuessSchema();
    }



    public void stop() {
        //TODO
    }

}
