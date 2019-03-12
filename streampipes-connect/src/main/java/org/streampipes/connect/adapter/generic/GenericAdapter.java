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

package org.streampipes.connect.adapter.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;

public abstract class GenericAdapter<T extends AdapterDescription> extends Adapter<T> {

    private static final Logger logger = LoggerFactory.getLogger(Adapter.class);

    public GenericAdapter(T adapterDescription) {
        super(adapterDescription);
    }

    public GenericAdapter(T adapterDescription, boolean debug) {
        super(adapterDescription, debug);
    }

    public GenericAdapter() {
        super();
    }

    public abstract GenericAdapterDescription getAdapterDescription();

    public abstract void setProtocol(Protocol protocol);

    @Override
    public void startAdapter() throws AdapterException {

        GenericAdapterDescription adapterDescription = getAdapterDescription();


        Parser parser = getParser(adapterDescription);
        Format format = getFormat(adapterDescription);

        Protocol protocol = getProtocol(adapterDescription, format, parser);
        setProtocol(protocol);

        logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());

        protocol.run(adapterPipeline);
    }


    @Override
    public GuessSchema getSchema(T adapterDescription) throws AdapterException, ParseException {
        Parser parser = getParser((GenericAdapterDescription) adapterDescription);
        Format format = getFormat((GenericAdapterDescription) adapterDescription);

        Protocol protocol = getProtocol((GenericAdapterDescription) adapterDescription, format, parser);

        logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());

        return protocol.getGuessSchema();
    }

    private Parser getParser(GenericAdapterDescription adapterDescription) throws AdapterException {
         if (adapterDescription.getFormatDescription() == null) throw new AdapterException("Format description of Adapter ist empty");
         return AdapterRegistry.getAllParsers().get(adapterDescription.getFormatDescription().getAppId()).getInstance(adapterDescription.getFormatDescription());
    }

    private Format getFormat(GenericAdapterDescription adapterDescription) {
        return AdapterRegistry.getAllFormats().get(adapterDescription.getFormatDescription().getAppId()).getInstance(adapterDescription.getFormatDescription());
    }

    private Protocol getProtocol(GenericAdapterDescription adapterDescription, Format format, Parser parser) {
        return AdapterRegistry.getAllProtocols().get(adapterDescription.getProtocolDescription().getAppId()).getInstance(adapterDescription.getProtocolDescription(), parser, format);
    }

}
