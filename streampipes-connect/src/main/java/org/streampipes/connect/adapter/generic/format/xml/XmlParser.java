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

package org.streampipes.connect.adapter.generic.format.xml;

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
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.format.util.JsonEventProperty;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class XmlParser extends Parser {

    Logger logger = LoggerFactory.getLogger(XmlParser.class);

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

        Map<String, Object> exampleEvent = null;

        try {
            exampleEvent = jsonDefinition.toMap(oneEvent.get(0));
        } catch (SpRuntimeException e) {
            logger.error(e.toString());
        }

        for (Map.Entry<String, Object> entry : exampleEvent.entrySet())
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

}
