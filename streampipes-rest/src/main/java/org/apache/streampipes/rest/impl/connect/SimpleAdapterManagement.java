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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.manager.template.AdapterTemplateHandler;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.simple.SimpleAdapter;
import org.apache.streampipes.model.connect.adapter.simple.SimpleEventProperty;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class SimpleAdapterManagement {
  public AdapterDescription convertToAdapterDescription(SimpleAdapter simpleAdapter)
      throws WorkerAdapterException,
             NoServiceEndpointsAvailableException,
             IOException {

    // Get the adapter description from the registed adapters
    var adapterDescription = findAdapterDescription(simpleAdapter.appId());

    // Set basic configurations
    setNameAndDescription(adapterDescription, simpleAdapter);

    // Fill out user configurations
    applyUserConfiguraition(adapterDescription, simpleAdapter);

    // Set event schema
    addEventSchema(adapterDescription);

    return adapterDescription;
  }

  private void addEventSchema(
      AdapterDescription adapterDescription) throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {

    var guessManagement = new GuessManagement();
    GuessSchema result = guessManagement.guessSchema(adapterDescription);
    adapterDescription.getDataStream().setEventSchema(result.getEventSchema());

//    var eventProperties = simpleAdapter.eventSchema()
//                                       .properties()
//                                       .stream()
//                                       .map(simpleEventProperty -> simpleEventPropertyToEventProperty(
//                                           simpleEventProperty))
//                                       .toList();
//
//    var eventSchema = new EventSchema(eventProperties);
//    adapterDescription.getDataStream()
//                      .setEventSchema(eventSchema);
  }


  private EventProperty simpleEventPropertyToEventProperty(
      SimpleEventProperty simpleEventProperty
  ) {
    var eventPropertyPrimitive = new EventPropertyPrimitive();
    eventPropertyPrimitive.setRuntimeType(simpleEventProperty.runtimeType());
    eventPropertyPrimitive.setLabel(simpleEventProperty.label());
    eventPropertyPrimitive.setDescription(simpleEventProperty.description());
    eventPropertyPrimitive.setRuntimeName(simpleEventProperty.runtimeName());
    eventPropertyPrimitive.setPropertyScope(simpleEventProperty.propertyScope());
    if (simpleEventProperty.domainProperty() != null && !simpleEventProperty.domainProperty()
                                                                            .equals("")) {
      var url = URI.create(simpleEventProperty.domainProperty());
      eventPropertyPrimitive.setDomainProperties(List.of(url));
    }

    return eventPropertyPrimitive;
  }

  private void applyUserConfiguraition(
      AdapterDescription adapterDescription,
      SimpleAdapter simpleAdapter
  ) {

    var adapterTemplate = new PipelineElementTemplate();
    var templateConfigs = simpleAdapter.configuration();

    templateConfigs.values()
                   .forEach((key, value) -> {
                     PipelineElementTemplateConfig config = new PipelineElementTemplateConfig();
                     // Assuming PipelineElementTemplateConfig has a method to set key and value
                     config.setValue(value);
                     adapterTemplate.getTemplateConfigs()
                                    .put(key, config);
                   });

    new AdapterTemplateHandler(
        adapterTemplate, adapterDescription,
        false
    )
        .applyTemplateOnPipelineElement();
  }

  private void setNameAndDescription(
      AdapterDescription adapterDescription,
      SimpleAdapter simpleAdapter
  ) {
    adapterDescription.setName(simpleAdapter.name());
    adapterDescription.setDescription(simpleAdapter.description());
  }

  private AdapterDescription findAdapterDescription(String appId) {
    IAdapterStorage adapterStorage = CouchDbStorageManager.INSTANCE.getAdapterDescriptionStorage();
    return adapterStorage.findAll()
                         .stream()
                         .filter(desc -> desc.getAppId()
                                             .equals(appId))
                         .findFirst()
                         .get();

  }

}
