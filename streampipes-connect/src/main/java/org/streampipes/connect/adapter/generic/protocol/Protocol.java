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

package org.streampipes.connect.adapter.generic.protocol;

import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.connect.grounding.ProtocolDescription;

import java.util.List;
import java.util.Map;


public abstract class Protocol {

    protected Parser parser;
    protected Format format;

    public Protocol() {

    }

    public Protocol(Parser parser, Format format) {
        this.parser = parser;
        this.format = format;
    }

    public abstract Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format);

    public abstract ProtocolDescription declareModel();

    public abstract GuessSchema getGuessSchema() throws ParseException;

    public abstract List<Map<String, Object>> getNElements(int n) throws ParseException;

    public abstract void run(AdapterPipeline adapterPipeline);

    /*
       Stops the running protocol. Mainly relevant for streaming protocols
     */
    public abstract void stop();

    public abstract String getId();
}
