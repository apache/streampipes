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

package org.streampipes.connect.firstconnector.format.csv;


import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.firstconnector.format.Parser;
import org.streampipes.connect.firstconnector.sdk.ParameterExtractor;
import org.streampipes.empire.cp.common.utils.base.Bool;
import org.streampipes.model.modelconnect.FormatDescription;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class CsvParser extends Parser {

    private String delimiter;
    private Boolean header;

    public CsvParser() {
    }

    public CsvParser(String delimiter, Boolean header) {
        this.delimiter = delimiter;
        this.header = header;
    }

    @Override
    public Parser getInstance(FormatDescription formatDescription) {
        ParameterExtractor extractor = new ParameterExtractor(formatDescription.getConfig());

        boolean header = extractor.singleValue(CsvFormat.HEADER_NAME) == null ? false : true;
        String delimiter = extractor.singleValue(CsvFormat.DELIMITER_NAME);

        return new CsvParser(delimiter, header);
    }

    @Override
    public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(data));

        boolean result = true;

        try {
            while (reader.ready() && result) {
                String s = reader.readLine();
                byte[] parseResult = s.getBytes();
                if (parseResult != null) {
                    result = emitBinaryEvent.emit(parseResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public EventSchema getEventSchema(byte[] oneEvent) {

        String headerLine = new String (oneEvent);
        String[] keys = new String(headerLine).split(delimiter);

        EventSchema resultSchema = new EventSchema();

        // TODO add datatype

        if (this.header) {

            for (String key : keys) {
                EventPropertyPrimitive p = new EventPropertyPrimitive();
                p.setRuntimeName(key);
                resultSchema.addEventProperty(p);
            }
        } else {
            for (int i = 0; i < keys.length; i++) {
                 EventPropertyPrimitive p = new EventPropertyPrimitive();
                p.setRuntimeName("key_" + i);
                resultSchema.addEventProperty(p);
            }

        }

        return resultSchema;
    }

}