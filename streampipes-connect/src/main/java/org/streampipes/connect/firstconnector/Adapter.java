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

package org.streampipes.connect.firstconnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectFormat;
import org.streampipes.connect.firstconnector.format.json.object.JsonObjectParser;
import org.streampipes.connect.firstconnector.pipeline.AdapterPipeline;
import org.streampipes.connect.firstconnector.pipeline.AdapterPipelineElement;
import org.streampipes.connect.firstconnector.pipeline.elements.SendToKafkaAdapterSink;
import org.streampipes.connect.firstconnector.pipeline.elements.TransformSchemaAdapterPipelineElement;
import org.streampipes.connect.firstconnector.protocol.stream.KafkaProtocol;
import org.streampipes.connect.firstconnector.protocol.stream.MqttProtocol;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter {

    private static Map<String, Format> allFormats = new HashMap<>();
    private static Map<String, Protocol> allProtocols = new HashMap<>();
    private static Map<String, Parser> allParsers = new HashMap<>();

    private String kafkaUrl;
    private String topic;
    private Protocol protocol;

    private AdapterDescription adapterDescription;

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
        allProtocols.put(MqttProtocol.ID, new MqttProtocol());

        this.debug = debug;
    }

    public Adapter(String kafkaUrl, String topic) {
        this(kafkaUrl, topic, false);
    }


    public void run(AdapterDescription adapterDescription) {

        this.adapterDescription = adapterDescription;

        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());


        List<AdapterPipelineElement> pipelineElements = new ArrayList<>();
        pipelineElements.add(new TransformSchemaAdapterPipelineElement(adapterDescription.getRules()));
        pipelineElements.add(new SendToKafkaAdapterSink(this.kafkaUrl, this.topic));

        AdapterPipeline adapterPipeline = new AdapterPipeline(pipelineElements);

        protocol.run(adapterPipeline);

//        protocol.run(this.kafkaUrl, this.topic);

    }

    public GuessSchema getSchema(AdapterDescription adapterDescription) {
        Parser parser = allParsers.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = allFormats.get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        Protocol protocol = allProtocols.get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());

        return protocol.getGuessSchema();
    }

    public void stop() {
        protocol.stop();
    }

    public AdapterDescription getAdapterDescription() {
        return adapterDescription;
    }
}
