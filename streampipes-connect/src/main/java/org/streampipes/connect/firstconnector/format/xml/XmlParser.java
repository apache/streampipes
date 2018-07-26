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

package org.streampipes.connect.firstconnector.format.xml;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.format.util.JsonEventProperty;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class XmlParser extends Parser {

    Logger logger = LoggerFactory.getLogger(XmlParser.class);
    private static int MAX_NUM_EVENTS_SCHEMA_GUESS = 20;

    private String tag;

    public XmlParser() {

    }

    public XmlParser(String tag) {
        this.tag = tag;
    }

    @Override
    public Parser getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());
        String tag = extractor.singleValue("tag");

        return new XmlParser(tag);
    }

    @Override
    public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {

        try {
            String dataString = CharStreams.toString(new InputStreamReader(data, Charsets.UTF_8));

            JSONObject xmlJSONObj = XML.toJSONObject(dataString);
            searchAndEmitEvents(xmlJSONObj.toMap(), tag, emitBinaryEvent);

        } catch (JSONException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            logger.error(e.toString());
        }

    }

    @Override
    public EventSchema getEventSchema(List<byte[]> oneEvent) {
        EventSchema resultSchema = new EventSchema();

        JsonDataFormatDefinition jsonDefinition = new JsonDataFormatDefinition();
        List<Map<String, Object>> exampleEvents= new LinkedList<>();
        Map<String, Object> result = new HashMap<>();

        try {
            int i = 1;
            for (Iterator<byte[]> it = oneEvent.iterator(); it.hasNext(); i++) {
                byte[] bytes = it.next();
                Map exampleEvent = jsonDefinition.toMap(bytes);
                exampleEvents.add(exampleEvent);
                if(i >= MAX_NUM_EVENTS_SCHEMA_GUESS ) break;
            }


            for (Iterator<Map<String, Object>> it = exampleEvents.iterator(); it.hasNext(); ) {
                result = mergeEventMaps(result, it.next());
            }

        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Object> entry : result.entrySet())
        { EventProperty p = JsonEventProperty.getEventProperty(entry.getKey(), entry.getValue());
            resultSchema.addEventProperty(p);
        }

        return resultSchema;
    }

    private void searchAndEmitEvents(Map<String, Object> map, String key, EmitBinaryEvent emitBinaryEvent) {
        Gson gson = new Gson();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getKey().equals(key)) {

               if(entry.getValue() instanceof List) {
                    List list = (List) entry.getValue();

                    list.forEach(listEntry -> emitBinaryEvent.emit(gson.toJson(listEntry).getBytes()));

               } else if(entry.getValue() instanceof Map) {
                   byte[] bytes = gson.toJson(entry.getValue()).getBytes();
                   emitBinaryEvent.emit(bytes);

               } else {
                   logger.error("Events are found, but could not disjunct: " + entry.toString());
               }

            } else if (entry.getValue() instanceof Map) {
                searchAndEmitEvents((Map)entry.getValue(), key, emitBinaryEvent);
            } else if (entry.getValue() instanceof List) {
                List list = (List)entry.getValue();
                list.forEach(listEntry -> searchAndEmitEvents((Map)listEntry, key, emitBinaryEvent));
            }
        }
    }

    private Map mergeEventMaps(Map map1, Map map2) {
        Map result = map1;
        for (Object key : map2.keySet()) {
            if (map2.get(key) instanceof Map && result.get(key) instanceof Map) {
                Map originalChild = (Map) result.get(key);
                Map newChild = (Map) map2.get(key);
                result.put(key, mergeEventMaps(originalChild, newChild));
            } else {
                result.put(key, map2.get(key));
            }
        }
        return result;

    }



}
