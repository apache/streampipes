package org.streampipes.connect.firstconnector.format.json.arraykey;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class JsonParserTest {

    @Test
    public void parseOneEvent() {

        String jo = getJsonArrayWithThreeElements();

        JsonParser parser = new JsonParser(true, "key0");


        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

        assertEquals(parsedEvent.size(), 1);
        String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

        assertEquals(parsedStringEvent, "{\"one\":1}");
    }


    @Test
    public void parseThreeEvents() {

        String jo = getJsonArrayWithThreeElements();
        JsonParser parser = new JsonParser(true, "key0");


        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 3);

        assertEquals(3, parsedEvent.size());
        String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);
        String parsedStringEventTwo = new String(parsedEvent.get(1), StandardCharsets.UTF_8);
        String parsedStringEventThree = new String(parsedEvent.get(2), StandardCharsets.UTF_8);

        assertEquals( "{\"one\":1}", parsedStringEventOne);
        assertEquals("{\"one\":2}", parsedStringEventTwo);
        assertEquals("{\"one\":3}", parsedStringEventThree);
    }

    private String getJsonArrayWithThreeElements() {
        return new JSONObject()
                .put("key0", new JSONArray()
                        .put(new JSONObject().put("one", 1))
                        .put(new JSONObject().put("one", 2))
                        .put(new JSONObject().put("one", 3))
                ).toString();
    }


    @Test
    public void parseMoreThenExist() {

        String jo = new JSONObject()
                .put("key0", new JSONArray()
                        .put(new JSONObject().put("one", 1))
                ).toString();
        JsonParser parser = new JsonParser(true, "key0");

        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 10);

        assertEquals(1, parsedEvent.size());
        String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

        assertEquals( "{\"one\":1}", parsedStringEventOne);
    }

    private InputStream getInputStream(String s) {

        try {
            return IOUtils.toInputStream(s, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}