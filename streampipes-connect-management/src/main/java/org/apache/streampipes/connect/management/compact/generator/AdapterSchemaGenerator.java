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

package org.apache.streampipes.connect.management.compact.generator;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.compact.SchemaMetadataEnricher;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;

import java.io.IOException;

public class AdapterSchemaGenerator implements AdapterModelGenerator {

  private final SchemaMetadataEnricher enricher;
  private final GuessManagement guessManagement;

  public AdapterSchemaGenerator(
      SchemaMetadataEnricher enricher,
      GuessManagement guessManagement
  ) {
    this.enricher = enricher;
    this.guessManagement = guessManagement;
  }

  @Override
  public void apply(
      AdapterDescription adapterDescription,
      CompactAdapter compactAdapter,
      String userId
  )
      throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException, AdapterException {

    if (compactAdapter.transformationConfig() != null && compactAdapter.transformationConfig()
                                                                       .getScript() != null) {
      adapterDescription.getTransformationConfig()
                        .setScript(compactAdapter.transformationConfig()
                                                 .getScript());
      adapterDescription.getTransformationConfig()
                        .setScriptActive(compactAdapter.transformationConfig().isScriptActive());
    }

    var sampleData = guessManagement.getSampleData(adapterDescription);
    adapterDescription.getTransformationConfig()
                      .setInputs(sampleData.getSamples());

    setDefaultScriptIfNotSet(adapterDescription);
    setDefaultScriptLanguageIfNotSet(adapterDescription);

    guessManagement.transformSampleData(adapterDescription, userId);

    var eventSchema = guessManagement.guessSchema(adapterDescription);
    if (eventSchema != null) {
      adapterDescription.getDataStream()
                        .setEventSchema(eventSchema);
    }

    var schemaDef = compactAdapter.schema();

    if (schemaDef != null) {
      adapterDescription.getDataStream()
                        .getEventSchema()
                        .getEventProperties()
                        .forEach(ep -> {
                          if (schemaDef.containsKey(ep.getRuntimeName())) {
                            var compactPropertyDef = schemaDef.get(ep.getRuntimeName());
                            enricher.enrich(ep, compactPropertyDef);
                          }
                        });
    }
  }

  private void setDefaultScriptIfNotSet(AdapterDescription adapterDescription) {
    if (adapterDescription.getTransformationConfig()
                          .getScript() == null
        || adapterDescription.getTransformationConfig()
                             .getScript()
                             .isEmpty()) {
      adapterDescription.getTransformationConfig().setScriptActive(true);
      adapterDescription.getTransformationConfig()
                        .setScript("""
                                   function transform(event, out, ctx) {
                                      out.collect(event);
                                    }
                                   """);

    }
  }

  private void setDefaultScriptLanguageIfNotSet(AdapterDescription adapterDescription) {
    if (adapterDescription.getTransformationConfig()
                          .getLanguage() == null
        || adapterDescription.getTransformationConfig()
                             .getLanguage()
                             .isEmpty()) {
      adapterDescription.getTransformationConfig()
                        .setLanguage("javascript");
    }
  }
}
