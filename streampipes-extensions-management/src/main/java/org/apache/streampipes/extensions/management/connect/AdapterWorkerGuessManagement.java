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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.IDeclarersSingleton;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.sdk.extractor.AdapterParameterExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterWorkerGuessManagement {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterWorkerGuessManagement.class);

  private IAdapterGuessSchemaContext guessSchemaContext;

  public AdapterWorkerGuessManagement() {
  }

  public AdapterWorkerGuessManagement(IAdapterGuessSchemaContext guessSchemaContext) {
    this.guessSchemaContext = guessSchemaContext;
  }

  /**
   * Collects sample data from an adapter instance
   */
  public SampleData getSampleData(AdapterDescription adapterDescription) throws AdapterException {
    LOG.debug("Start receiving sample data for adapter: {}", adapterDescription.getAppId());

    var adapter = getAdapterInstanceOrThrowException(adapterDescription.getAppId());

    // get registered parser of adapter
    var registeredParsers = adapter
        .declareConfig()
        .getSupportedParsers();

    var extractor = AdapterParameterExtractor.from(adapterDescription, registeredParsers);

    LOG.debug("Requesting the sample events for: {}", adapterDescription.getAppId());

    return adapter.onSampleDataRequested(extractor, guessSchemaContext);

  }

  private StreamPipesAdapter getAdapterInstanceOrThrowException(String appId) throws AdapterException {
    var adapter = getDeclarerSingleton()
        .getAdapter(appId);

    if (adapter.isPresent()) {
      return adapter.get();
    } else {
      throw new AdapterException("Adapter with app id %s was not be found".formatted(appId));
    }
  }

  public IDeclarersSingleton getDeclarerSingleton() {
    return DeclarersSingleton.getInstance();
  }

}
