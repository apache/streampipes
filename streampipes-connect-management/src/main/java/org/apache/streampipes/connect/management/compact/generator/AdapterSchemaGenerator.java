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
import org.apache.streampipes.connect.management.compact.SchemaMetadataEnricher;
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.connect.guess.GuessSchema;

import java.io.IOException;

public class AdapterSchemaGenerator implements AdapterModelGenerator {

  private final SchemaMetadataEnricher enricher;
  private final GuessManagement guessManagement;

  public AdapterSchemaGenerator(SchemaMetadataEnricher enricher,
                                GuessManagement guessManagement) {
    this.enricher = enricher;
    this.guessManagement = guessManagement;
  }

  @Override
  public void apply(AdapterDescription adapterDescription,
                    CompactAdapter compactAdapter)
      throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {

    GuessSchema result = guessManagement.guessSchema(adapterDescription);
    adapterDescription.getDataStream().setEventSchema(result.getEventSchema());

    var schemaDef = compactAdapter.schema();

    if (schemaDef != null) {
      adapterDescription.getDataStream().getEventSchema().getEventProperties().forEach(ep -> {
        if (schemaDef.containsKey(ep.getRuntimeName())) {
          var compactPropertyDef = schemaDef.get(ep.getRuntimeName());
          enricher.enrich(ep, compactPropertyDef);
        }
      });
    }
  }
}
