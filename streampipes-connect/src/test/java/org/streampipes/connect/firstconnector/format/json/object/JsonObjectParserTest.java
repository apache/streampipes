package org.streampipes.connect.firstconnector.format.json.object;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonObjectParserTest {

    @Test
    public void parseOneEvent() {

//        String jo = getJsonArrayWithThreeElements();

        String jo = new JSONObject()
                .put("one", 1)
                .toString();


        JsonObjectParser parser = new JsonObjectParser();

        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

        assertEquals(parsedEvent.size(), 1);
        String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

        assertEquals(parsedStringEvent, "{\"one\":1}");
    }


    @Test
    public void parseThreeEvents() {

        String jo = getJsonArrayWithThreeElements();
        JsonObjectParser parser = new JsonObjectParser();

        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 3);

        assertEquals(1, parsedEvent.size());
        String parsedStringEventOne = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

        assertEquals( "{\"one\":1}", parsedStringEventOne);
    }

    private String getJsonArrayWithThreeElements() {

        String jo = new JSONObject()
                .put("one", 1)
                .toString();

        jo += "\n" + new JSONObject()
                .put("one", 2)
                .toString();

        jo += "\n" + new JSONObject()
                .put("one", 3)
                .toString();

        return jo;
    }


    @Test
    public void parseMoreThenExist() {

        String jo = new JSONObject()
                .put("one", 1)
                .toString();

        JsonObjectParser parser = new JsonObjectParser();

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