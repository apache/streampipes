/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.connect.adapter.generic.format.image;

import org.apache.commons.io.IOUtils;
import org.streampipes.connect.EmitBinaryEvent;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.XSD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


public class ImageParser extends Parser {

    public ImageParser() {

    }

    @Override
    public Parser getInstance(FormatDescription formatDescription) {
        return new ImageParser();
    }

    @Override
    public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(data));

        try {
            byte[] result = IOUtils.toByteArray(data);
            System.out.println("Parser " + result.toString());
            emitBinaryEvent.emit(result);
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        }

    }

    @Override
    public EventSchema getEventSchema(List<byte[]> oneEvent) {
        EventSchema resultSchema = new EventSchema();
        EventPropertyPrimitive p = new EventPropertyPrimitive();
        p.setRuntimeName("image");
        p.setRuntimeType(XSD._string.toString());

        p.setDomainProperties(Arrays.asList(URI.create("https://image.com")));
        resultSchema.addEventProperty(p);
        return resultSchema;
    }
}
