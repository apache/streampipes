/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.container.html.page;

import org.apache.streampipes.container.declarer.*;
import org.apache.streampipes.container.html.model.DataSourceDescriptionHtml;
import org.apache.streampipes.container.html.model.Description;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataSinkDescription;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WelcomePageGeneratorImpl extends WelcomePageGenerator<Declarer> {


    public WelcomePageGeneratorImpl(String baseUri, Collection<Declarer> declarers) {
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
        // TODO remove after full internationalization support has been implemented
        updateLabel(declarer.declareModel(), desc);
        desc.setType(getType(declarer));
        desc.setAppId(declarer.declareModel().getAppId());
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
        updateLabel(declarer.declareModel(), desc);
        desc.setUri(URI.create(baseUri + "sep/" + declarer.declareModel().getUri()));
        desc.setType("source");
        desc.setAppId(declarer.declareModel().getAppId());
        for (DataStreamDeclarer streamDeclarer : declarer.getEventStreams()) {
            Description ad = new Description();
            updateLabel(streamDeclarer.declareModel(declarer.declareModel()), ad);
            ad.setUri(URI.create(baseUri +"stream/" + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
            ad.setType("stream");
            streams.add(ad);
        }
        desc.setStreams(streams);
        return desc;
    }

    private void updateLabel(NamedStreamPipesEntity entity, Description desc) {
        if (!entity.isIncludesLocales()) {
            desc.setName(entity.getName());
            desc.setDescription(entity.getDescription());
        } else {
            LabelGenerator lg = new LabelGenerator(entity);
            try {
                desc.setName(lg.getElementTitle());
                desc.setDescription(lg.getElementDescription());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
