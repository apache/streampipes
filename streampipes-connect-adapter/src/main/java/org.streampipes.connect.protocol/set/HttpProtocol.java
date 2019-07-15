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

package org.streampipes.connect.adapter.generic.protocol.set;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.exception.ParseException;
import org.streampipes.connect.adapter.guess.SchemaGuesser;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Parser;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpProtocol extends Protocol {

    Logger logger = LoggerFactory.getLogger(Protocol.class);

    public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/set/http";

    private String url;

    public HttpProtocol() {
    }

    public HttpProtocol(Parser parser, Format format, String url) {
        super(parser, format);
        this.url = url;
    }

    @Override
    public ProtocolDescription declareModel() {
        return ProtocolDescriptionBuilder.create(ID, "HTTP Set", "Reads the content from an HTTP " +
                "endpoint.")
                .category(AdapterType.Generic)
                .sourceType(AdapterSourceType.SET)
                .iconUrl("rest.png")
                .requiredTextParameter(Labels.from("url", "Url", "Example: http(s)://test-server.com"))
                .build();
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
        String url = extractor.singleValue("url");

        return new HttpProtocol(parser, format, url);
    }

    @Override
    public void run(AdapterPipeline adapterPipeline) {

        // TODO fix this. Currently needed because it must be wait till the whole preprocessing is up and running
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SendToPipeline stk = new SendToPipeline(format, adapterPipeline);

        InputStream data = getDataFromEndpoint();
        try {
            parser.parse(data, stk);

        } catch (ParseException e) {
            logger.error("Error while parsing: " + e.getMessage());
        }
    }

    @Override
    public void stop() {

    }


    @Override
    public GuessSchema getGuessSchema() throws ParseException {

        InputStream dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByte = parser.parseNEvents(dataInputStream, 2);

        EventSchema eventSchema= parser.getEventSchema(dataByte);

        GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(2));

        return result;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) throws ParseException {

        List<Map<String, Object>> result = new ArrayList<>();

        InputStream dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByteArray = parser.parseNEvents(dataInputStream, n);

        // Check that result size is n. Currently just an error is logged. Maybe change to an exception
        if (dataByteArray.size() < n) {
            logger.error("Error in HttpProtocol! User required: " + n + " elements but the resource just had: " +
                    dataByteArray.size());
        }

        for (byte[] b : dataByteArray) {
            result.add(format.parse(b));
        }

        return result;
    }

    public InputStream getDataFromEndpoint() throws ParseException {
        InputStream result = null;

        try {
            result = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asStream();

//            if (s.startsWith("ï")) {
//                s = s.substring(3);
//            }

//            result = IOUtils.toInputStream(s, "UTF-8");

        } catch (IOException e) {
            throw new ParseException("Could not receive Data from: " + url);
        }

        if (result == null)
            throw new ParseException("Could not receive Data from: " + url);

        return result;
    }

    @Override
    public String getId() {
        return ID;
    }
}
