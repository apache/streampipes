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


import org.streampipes.connect.GetNEvents;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.connect.EmitBinaryEvent;

import java.io.InputStream;
import java.util.List;

public abstract class Parser {

    public abstract Parser getInstance(FormatDescription formatDescription);

    public abstract void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) throws ParseException;

    public List<byte[]> parseNEvents(InputStream data, int n) throws ParseException {
        GetNEvents gne = new GetNEvents(n);

        try {
            parse(data, gne);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (Exception e) {
            throw new ParseException("Error while parse the data with the "
                    + this.getClass().getSimpleName() + ": " + e.getMessage());
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
