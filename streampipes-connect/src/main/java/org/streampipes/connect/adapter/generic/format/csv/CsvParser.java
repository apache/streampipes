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

package org.streampipes.connect.adapter.generic.format.csv;


import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        boolean header = extractor.selectedMultiValues(CsvFormat.HEADER_NAME).stream()
                .anyMatch(option -> option.equals("Header"));
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
    public EventSchema getEventSchema(List<byte[]> oneEvent) {
        String[] keys;
        String[] data;

        // TODO handle strings containing seperator chars

        if (this.header) {
            keys = parseLine(new String(oneEvent.get(0)), delimiter);
            data = parseLine(new String (oneEvent.get(1)), delimiter);
        } else {
            data = parseLine(new String (oneEvent.get(0)), delimiter);
            keys = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                keys[i] = "key_" + i;
            }
        }

        EventSchema resultSchema = new EventSchema();
        for (int i = 0; i < keys.length; i++) {
            EventPropertyPrimitive p = new EventPropertyPrimitive();
            p.setRuntimeName(keys[i]);
            p.setRuntimeType(getTypeString(data[i]));
            resultSchema.addEventProperty(p);
        }

        return resultSchema;
    }

    private String getTypeString(String o) {

        try {
            Double.parseDouble(o);
            return SO.Number.toString();
        } catch (NumberFormatException e) {

        }

        if (o.equalsIgnoreCase("true") || o.equalsIgnoreCase("false")) {
            return XSD._boolean.toString();
        }

        return XSD._string.toString();
    }


    public static String[] parseLine(String cvsLine, String separatorString) {

        char separators = separatorString.toCharArray()[0];

        List<String> result = new ArrayList<>();

        char customQuote = '"';

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
//                    if (chars[0] != '"' && customQuote == '\"') {
//                        curVal.append('"');
//                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }
        }

        result.add(curVal.toString());

        return result.toArray(new String[0]);
    }
}