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

package org.streampipes.connect.adapter.generic.format;


import org.apache.commons.io.IOUtils;
import org.streampipes.connect.GetNEvents;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.connect.EmitBinaryEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class Parser {

    public abstract Parser getInstance(FormatDescription formatDescription);

    public abstract void parse(InputStream data, EmitBinaryEvent emitBinaryEvent);

    public List<byte[]> parseNEvents(InputStream data, int n) throws AdapterException {
        GetNEvents gne = new GetNEvents(n);


        // TODO: Decide if want use this
        // Clone inputstream to print data if there is an parse exception
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AdapterException("");
        }
        data = new ByteArrayInputStream(byteArray);


        try {
            parse(data, gne);
        } catch (Exception e) {
            try {
                // TODO: print all?
                throw new AdapterException("Error while parse following data with the "
                        + this.getClass().getSimpleName()
                        + " : " + IOUtils.toString(byteArray));
            } catch (IOException e1) {
                throw new AdapterException("Error while parse the data with the "
                        + this.getClass().getSimpleName());
            }
        }


        return gne.getEvents();
    }

    /**
     * Pass one event to Parser to get the event schema
     * @param oneEvent
     * @return
     */
    public abstract EventSchema getEventSchema(List<byte[]> oneEvent);
}
