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

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.guess.SchemaGuesser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpStreamProtocol extends PullProtocoll {

    Logger logger = LoggerFactory.getLogger(HttpStreamProtocol.class);

    public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/http";
    private static String URL_PROPERTY ="url";
    private static String INTERVAL_PROPERTY ="interval";

    private String url;

    public HttpStreamProtocol() {
    }



    public HttpStreamProtocol(Parser parser, Format format, String url, long interval) {
        super(parser, format, interval);
        this.url = url;
    }

    @Override
    public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
        ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());

        String urlProperty = extractor.singleValue(URL_PROPERTY);
        try {
            long intervalProperty = Long.parseLong(extractor.singleValue(INTERVAL_PROPERTY));
            return new HttpStreamProtocol(parser, format, urlProperty, intervalProperty);
        } catch (NumberFormatException e) {
            logger.error("Could not parse" + extractor.singleValue(INTERVAL_PROPERTY) + "to int");
            return null;
        }

    }

    @Override
    public ProtocolDescription declareModel() {
        ProtocolDescription description = new ProtocolDescription(ID, "HTTP (Stream)", "This is the " +
                "description for the http stream protocol");

        FreeTextStaticProperty urlProperty = new FreeTextStaticProperty(URL_PROPERTY, "URL", "This property " +
                "defines the URL for the http request.");


        FreeTextStaticProperty intervalProperty = new FreeTextStaticProperty(INTERVAL_PROPERTY, "Interval [Sec]", "This property " +
                "defines the pull interval in seconds.");


        description.setSourceType("STREAM");
        description.addConfig(urlProperty);
        description.addConfig(intervalProperty);

        return description;
    }

    @Override
    public GuessSchema getGuessSchema() {
        int n = 1;

        InputStream dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByte = parser.parseNEvents(dataInputStream, n);
        if (dataByte.size() < n) {
            logger.error("Error in HttpStreamProtocol! Required: " + n + " elements but the resource just had: " +
                    dataByte.size());

            dataByte.addAll(dataByte);
        }
        EventSchema eventSchema= parser.getEventSchema(dataByte);
        GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(n));

        return result;
    }

    @Override
    public List<Map<String, Object>> getNElements(int n) {
        List<Map<String, Object>> result = new ArrayList<>();

         InputStream   dataInputStream = getDataFromEndpoint();

        List<byte[]> dataByte = parser.parseNEvents(dataInputStream, n);

        // Check that result size is n. Currently just an error is logged. Maybe change to an exception
        if (dataByte.size() < n) {
            logger.error("Error in HttpStreamProtocol! User required: " + n + " elements but the resource just had: " +
                    dataByte.size());
        }

        for (byte[] b : dataByte) {
            result.add(format.parse(b));
        }

        return result;
    }


    @Override
    public String getId() {
        return ID;
    }

    @Override
    InputStream getDataFromEndpoint() {
        InputStream result = null;

        try {
            String s = Request.Get(url)
                    .setHeader("Authorization", "Bearer AQAAANCMnd8BFdERjHoAwE_Cl-sBAAAAJPZMcAsSSE2WztXm7QlZ-AAAAAACAAAAAAAQZgAAAAEAACAAAAC-VPDG0IiBSCHDXuH_6Gbd0ezdy8sDwYsfihROt5HhlgAAAAAOgAAAAAIAACAAAADuIfZWYSBvra57wJhcx_Fzq8w4bxEKaRPAN_Y5DU4JCUABAABGWKuC4qPdf3OJLQqJouJvPZ-al45xp-7g3R7d7OI3DMcKnuJ4hgOw45SfEAxyuYMXmh2nuKBNlCcAI0HX7HheZ3xAVlZg3HOAy1LjY1PwtBQ2NpnmsQYupMINomfLs-ExW3h5DR7iQp9hrIDtFWeIGbKgaca945CiYNDydOMtwSwWmM_F8gznB7nzSYI75FWS7fpG4a2eS6w62w5mMe0P-r8G5HIvid0BAe8GSqE9y1ClpS2JbQh1yaRgmz3cK-Lctj0fFUUioolsxrsPTTokcSToZzrRBHNZAoYEvWFL8Lt2267SSwdkXAFBicw_IvF_r52dPJC-8-FdGpM-0ECchi0Tm-Ti0Myuiz9wUkc1iDE-bk4Knm1Lt9DCm1pHrppP1aluEAcfm9eFCUzrk6JhDgVBOTv2YFdRErYR2ta0CUAAAAA8vLD3mh23IyN1cjaXzuO6w0i7CVruV0QI4Ga5jv-APDq06dGXJIqIvwJXcGUIFXJ0_Wk9cuDRP7Jh63GiU2A9")
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            if (s.startsWith("ï")) {
                s = s.substring(3);
            }

            result = IOUtils.toInputStream(s, "UTF-8");

        } catch (Exception e) {
            logger.error("Error while fetching data from URL: " + url, e);

//            throw new AdapterException();
        }

        return result;
    }
}
