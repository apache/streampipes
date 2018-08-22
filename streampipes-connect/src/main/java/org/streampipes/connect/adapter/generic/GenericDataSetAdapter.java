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
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.generic.pipeline.elements.SendToKafkaAdapterSink;
import org.streampipes.connect.adapter.generic.pipeline.elements.TransformSchemaAdapterPipelineElement;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;

public class GenericDataSetAdapter extends Adapter {

    public static final String ID = GenericAdapterSetDescription.ID;

    private Protocol protocol;

    private GenericAdapterSetDescription adapterDescription;

    Logger logger = LoggerFactory.getLogger(Adapter.class);

    public GenericDataSetAdapter() {
        super();
    }


    public GenericDataSetAdapter(GenericAdapterSetDescription adapterDescription, boolean debug) {
        super(debug);
        this.adapterDescription = adapterDescription;
    }

    public GenericDataSetAdapter(GenericAdapterSetDescription adapterDescription) {
        super();
        this.adapterDescription = adapterDescription;
    }



    @Override
    public AdapterDescription declareModel() {
        AdapterDescription adapterDescription = new GenericAdapterSetDescription();
        adapterDescription.setAdapterId(GenericAdapterSetDescription.ID);
        adapterDescription.setUri(GenericAdapterSetDescription.ID);
        return adapterDescription;
    }

    @Override
    public Adapter getInstance(AdapterDescription adapterDescription) {
        return  new GenericDataSetAdapter((GenericAdapterSetDescription) adapterDescription);
    }

    @Override
    public void startAdapter()  throws AdapterException {


        Parser parser = AdapterRegistry.getAllParsers().get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
        Format format = AdapterRegistry.getAllFormats().get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());

        protocol = AdapterRegistry.getAllProtocols().get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);

        logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());


        List<AdapterPipelineElement> pipelineElements = new ArrayList<>();
        pipelineElements.add(new TransformSchemaAdapterPipelineElement(adapterDescription.getRules()));
        pipelineElements.add(new SendToKafkaAdapterSink(this.adapterDescription));

        AdapterPipeline adapterPipeline = new AdapterPipeline(pipelineElements);

        protocol.run(adapterPipeline);
    }


    public GuessSchema getSchema(AdapterDescription adapterDescription) {
//        Parser parser = AdapterRegistry.getAllParsers().get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
//        Format format = AdapterRegistry.getAllFormats().get(adapterDescription.getFormatDescription().getUri()).getInstance(adapterDescription.getFormatDescription());
//
//        Protocol protocol = AdapterRegistry.getAllProtocols().get(adapterDescription.getProtocolDescription().getUri()).getInstance(adapterDescription.getProtocolDescription(), parser, format);
//
//        logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());
//
//        return protocol.getGuessSchema();
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

    public void stopAdapter() {
        protocol.stop();
    }

    public AdapterDescription getAdapterDescription() {
        return adapterDescription;
    }
}
