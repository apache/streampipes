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

package org.streampipes.connect.adapter.generic.format.json.arraynokey;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonArrayParserTest {

    @Test
    public void parseOneEvent() {

        String jo = getJsonArrayWithThreeElements();

        JsonArrayParser parser = new JsonArrayParser();

        List<byte[]> parsedEvent = parser.parseNEvents(getInputStream(jo), 1);

        assertEquals(parsedEvent.size(), 1);
        String parsedStringEvent = new String(parsedEvent.get(0), StandardCharsets.UTF_8);

        assertEquals(parsedStringEvent, "{\"one\":1}");
    }


    @Test
    public void parseThreeEvents() {

        String jo = getJsonArrayWithThreeElements();
        JsonArrayParser parser = new JsonArrayParser();


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

        String jo = new JSONArray()
                .put(new JSONObject().put("one", 1))
                .toString();

        JsonArrayParser parser = new JsonArrayParser();

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