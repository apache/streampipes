/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.protocol.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.guess.SchemaGuesser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.protocol.set.FileProtocol;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilePullProtocol extends PullProtocol {

    private static Logger logger = LoggerFactory.getLogger(FilePullProtocol.class);

    public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/pull/file";

    private String fileUrl;

    public FilePullProtocol() {
    }

    public FilePullProtocol(Parser parser, Format format, long interval, String fileUrl) {
        super(parser, format, interval);
        this.fileUrl = fileUrl;
    }

    @Override
    InputStream getDataFromEndpoint() {
        FileReader fr = null;
        InputStream inn = null;

        try {
            URL url = new URL(fileUrl);

            //    fr = new FileReader(fileUri);
            //    BufferedReader br = new BufferedReader(fr);

            //      inn = new FileInputStream(fileUri);
            inn = url.openStream();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inn;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        long intervalProperty = Long.parseLong(extractor.singleValue("interval"));

        String fileUri = extractor.singleValue("fileUrl");

        return new FilePullProtocol(parser, format, intervalProperty, fileUri);    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription pd = new ProtocolDescription(ID,"File Pull","This is the " +
                "description for the File Pull protocol");
        FreeTextStaticProperty urlProperty = new FreeTextStaticProperty("fileUrl", "File URL",
                "This property defines the URL for the http file location.");
        pd.setSourceType("STREAM");
        FreeTextStaticProperty intervalProperty = new FreeTextStaticProperty("interval", "Interval", "This property " +
                "defines the pull interval in seconds.");
        pd.setIconUrl("file.png");
        pd.addConfig(urlProperty);
        pd.addConfig(intervalProperty);
        return pd;
    }

    @Override
    public GuessSchema getGuessSchema() {
        InputStream dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByte = parser.parseNEvents(dataInputStream, 20);

        EventSchema eventSchema= parser.getEventSchema(dataByte);

        GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(20));

        return result;    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        List<Map<String, Object>> result = new ArrayList<>();

        InputStream dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByteArray = parser.parseNEvents(dataInputStream, n);

        // Check that result size is n. Currently just an error is logged. Maybe change to an exception
        if (dataByteArray.size() < n) {
            logger.error("Error in File Protocol! User required: " + n + " elements but the resource just had: " +
                    dataByteArray.size());
        }

        for (byte[] b : dataByteArray) {
            result.add(format.parse(b));
        }

        return result;
    }

    @Override
    public String getId() {
        return ID;
    }
}
