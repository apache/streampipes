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
package org.streampipes.manager.template;

import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.manager.matching.DataSetGroundingSelector;
import org.streampipes.manager.matching.v2.ElementVerification;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.DataSetModificationMessage;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.sdk.builder.BoundPipelineElementBuilder;
import org.streampipes.sdk.builder.PipelineTemplateBuilder;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.management.StorageDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PipelineTemplateGenerator {

  private List<PipelineTemplateDescription> availableDescriptions = new ArrayList<>();

  public List<PipelineTemplateDescription> makeExampleTemplates() {

    try {
      availableDescriptions.add(makeExampleTemplate());
      System.out.println(availableDescriptions.get(0).getElementId());
      System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(availableDescriptions.get(0))));
    } catch (URISyntaxException | IllegalAccessException | InvocationTargetException | InvalidRdfException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return availableDescriptions;
  }

  public List<PipelineTemplateDescription> getCompatibleTemplates(String streamId) {
    List<PipelineTemplateDescription> compatibleTemplates = new ArrayList<>();
    ElementVerification verifier = new ElementVerification();
    SpDataStream streamOffer = getStream(streamId);
    if (streamOffer instanceof SpDataSet) {
      streamOffer = new SpDataSet((SpDataSet) prepareStream((SpDataSet) streamOffer));
    } else {
      streamOffer = new SpDataStream(streamOffer);
    }
    if (streamOffer != null) {
      for(PipelineTemplateDescription pipelineTemplateDescription : makeExampleTemplates()) {
        // TODO make this work for 2+ input streams
        InvocableStreamPipesEntity entity = cloneInvocation(pipelineTemplateDescription.getConnectedTo().get(0).getPipelineElementTemplate());
        if (verifier.verify(streamOffer, entity)) {
          compatibleTemplates.add(pipelineTemplateDescription);
        }
      }
    }

    return compatibleTemplates;
  }

  private InvocableStreamPipesEntity cloneInvocation(InvocableStreamPipesEntity pipelineElementTemplate) {
    if (pipelineElementTemplate instanceof DataProcessorInvocation) {
      return new DataProcessorInvocation((DataProcessorInvocation) pipelineElementTemplate);
    } else {
      return new DataSinkInvocation((DataSinkInvocation) pipelineElementTemplate);
    }
  }

  private SpDataStream prepareStream(SpDataSet stream) {
    DataSetModificationMessage message = new DataSetGroundingSelector(stream).selectGrounding();
    stream.setEventGrounding(message.getEventGrounding());
    stream.setDatasetInvocationId(message.getInvocationId());
    return stream;
  }


  private PipelineTemplateDescription makeExampleTemplate() throws URISyntaxException {
    return new PipelineTemplateDescription(PipelineTemplateBuilder.create("distance-kvi","Distance KVI",
            "Calculates the distance between two locations")
            .boundPipelineElementTemplate(BoundPipelineElementBuilder
                    .create(getProcessor("http://pe-jvm:8090/sepa/google-routing"))
//                    .withPredefinedFreeTextValue("timeWindow", "30")
//                    .withPredefinedSelection("operation", Collections.singletonList("Average"))
//                    .withOverwrittenLabel("aggregate", "Select a field you'd like to use for the KVI calculation")
                    .connectTo(BoundPipelineElementBuilder
                            .create(getSink("http://pe-sinks:8090/sec/dashboard_sink"))
                            .build())
                    .build())
            .build());
  }

  private SpDataStream getStream(String streamId) {
    return getStorage()
            .getEventStreamById(streamId);
  }

  private DataProcessorDescription getProcessor(String id) throws URISyntaxException {
    return getStorage()
            .getSEPAById(id);
  }

  private DataSinkDescription getSink(String id) throws URISyntaxException {
    return getStorage()
            .getSECById(id);
  }

  private IPipelineElementDescriptionStorage getStorage() {
    return StorageDispatcher
            .INSTANCE
            .getTripleStore()
            .getStorageAPI();
  }
}
