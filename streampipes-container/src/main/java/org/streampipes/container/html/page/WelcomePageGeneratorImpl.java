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

package org.streampipes.container.html.page;

import org.streampipes.container.declarer.*;
import org.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.streampipes.container.html.model.Description;
import org.streampipes.model.graph.DataSinkDescription;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WelcomePageGeneratorImpl extends WelcomePageGenerator<Declarer> {


    public WelcomePageGeneratorImpl(String baseUri, List<Declarer> declarers) {
        super(baseUri, declarers);
    }

    @Override
    public List<Description> buildUris() {
        List<Description> descriptions = new ArrayList<>();

        for (Declarer declarer : declarers) {
            if (declarer instanceof InvocableDeclarer) {
                descriptions.add(getDescription((InvocableDeclarer) declarer));
            } else if (declarer instanceof SemanticEventProducerDeclarer) {
                descriptions.add(getDescription((SemanticEventProducerDeclarer) declarer));
            } else if (declarer instanceof PipelineTemplateDeclarer) {
                descriptions.add(getDescription(declarer));
            }
        }
        return descriptions;
    }

    private Description getDescription(Declarer declarer) {
        Description desc = new Description();
        desc.setName(declarer.declareModel().getName());
        desc.setDescription(declarer.declareModel().getDescription());
        desc.setType(getType(declarer));
        String uri = baseUri;
        if (declarer instanceof SemanticEventConsumerDeclarer) {
            uri += "sec/";
        } else if (declarer instanceof SemanticEventProcessingAgentDeclarer) {
            uri += "sepa/";
        } else if (declarer instanceof PipelineTemplateDeclarer) {
            uri += "template/";
        }
        desc.setUri(URI.create(uri +declarer.declareModel().getUri().replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
        return desc;
    }

    private String getType(Declarer declarer) {
        if (declarer.declareModel() instanceof DataSinkDescription) return "action";
        else return "sepa";
    }

    private Description getDescription(SemanticEventProducerDeclarer declarer) {
        List<Description> streams = new ArrayList<>();
        DataSourceDescriptionHtml desc = new DataSourceDescriptionHtml();
        desc.setName(declarer.declareModel().getName());
        desc.setDescription(declarer.declareModel().getDescription());
        desc.setUri(URI.create(baseUri + "sep/" + declarer.declareModel().getUri()));
        desc.setType("source");
        for (DataStreamDeclarer streamDeclarer : declarer.getEventStreams()) {
            Description ad = new Description();
            ad.setDescription(streamDeclarer.declareModel(declarer.declareModel()).getDescription());
            ad.setUri(URI.create(baseUri +"stream/" + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
            ad.setName(streamDeclarer.declareModel(declarer.declareModel()).getName());
            ad.setType("stream");
            streams.add(ad);
        }
        desc.setStreams(streams);
        return desc;
    }
}
