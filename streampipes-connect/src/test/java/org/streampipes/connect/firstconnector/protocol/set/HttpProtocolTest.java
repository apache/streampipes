package org.streampipes.connect.firstconnector.protocol.set;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.query.algebra.Str;
import org.junit.Rule;
import org.junit.Test;
import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.firstconnector.Mock;
import org.streampipes.connect.firstconnector.format.Format;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.EventSchema;
import sun.nio.ch.IOUtil;

import javax.xml.transform.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class HttpProtocolTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(Mock.PORT);


    @Test
    public void getDataFromEndpointTest() {

        String expected = "Expected String";

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(expected)));


        HttpProtocol httpProtocol = new HttpProtocol(null, null, Mock.HOST + "/");

        InputStream data = httpProtocol.getDataFromEndpoint();

        String resultJson = "";

        try {
            resultJson = IOUtils.toString(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, resultJson);
    }


    @Test
    public void getNElementsTest() {

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Example response")));


        HttpProtocol httpProtocol = new HttpProtocol(new TestParser(""), new TestFormat(), Mock.HOST + "/");

        List<Map<String, Object>> result = httpProtocol.getNElements(1);

        assertEquals(1, result.size());
        assertEquals("value", result.get(0).get("key"));
    }

    private class TestParser extends Parser {
        private byte[] data;
        public TestParser(String data) {
            this.data = data.getBytes();
        }

        @Override
        public Parser getInstance(FormatDescription formatDescription) {
            return null;
        }

        @Override
        public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {
            emitBinaryEvent.emit(this.data);
        }

        @Override
        public EventSchema getEventSchema(byte[] oneEvent) {
            return null;
        }
    }

    private class TestFormat extends Format {

        @Override
        public Format getInstance(FormatDescription formatDescription) {
            return null;
        }

        @Override
        public FormatDescription declareModel() {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public Map<String, Object> parse(byte[] object) {
            Map<String, Object> result = new HashMap<>();
            result.put("key", "value");
            return result;
        }
    }

}