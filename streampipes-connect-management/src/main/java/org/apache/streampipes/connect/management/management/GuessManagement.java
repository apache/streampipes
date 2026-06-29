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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.management.AdapterEventPreviewPipeline;
import org.apache.streampipes.connect.management.util.EventSchemaUtils;
import org.apache.streampipes.connect.transformer.api.TransformationEngines;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.ScriptContextResolver;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.manager.api.extensions.IExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuessManagement {

  private static final Logger LOG = LoggerFactory.getLogger(GuessManagement.class);
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final IExtensionsServiceEndpointGenerator endpointGenerator;
  private final ObjectMapper objectMapper;
  private final SpResourceManager resourceManager;

  public GuessManagement(IExtensionsServiceEndpointGenerator endpointGenerator,
                         ExtensionServiceRequestManager extensionRequestManager,
                         SpResourceManager resourceManager) {
    this.endpointGenerator = endpointGenerator;
    this.extensionRequestManager = extensionRequestManager;
    this.objectMapper = JacksonSerializer.getObjectMapper();
    this.resourceManager = resourceManager;
  }

  public EventSchema guessSchema(AdapterDescription adapterDescription) {
    var event = adapterDescription.getTransformationConfig()
                                  .getOutputs()
                                  .get(0);
    return EventSchemaUtils.guessEventSchema(event);
  }

  public Map<String, Object> performAdapterEventPreview(AdapterDescription adapterDescription) {
    return new AdapterEventPreviewPipeline(adapterDescription).makePreview();
  }

  public SampleData getSampleData(AdapterDescription adapterDescription)
      throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {

    SecretProvider.getDecryptionService()
                  .apply(adapterDescription);

    var requestTarget = getWorkerRequestTarget(adapterDescription);

    var adapterDescriptionString = objectMapper.writeValueAsString(adapterDescription);

    LOG.debug("Calling get sample data at service: {}", requestTarget.serviceId());

    var response = extensionRequestManager.request(
        ExtensionServiceRequests.sampleData(requestTarget, adapterDescriptionString, resourceManager)
    );
    var responseString = response.responseBody();

    if (response.statusCode() == HttpStatus.SC_OK) {
      return objectMapper.readValue(responseString, SampleData.class);
    } else {
      var exception = objectMapper.readValue(responseString, SpLogMessage.class);
      throw new WorkerAdapterException(exception);
    }
  }

  public AdapterDescription transformSampleData(AdapterDescription adapterDescription, String userId) throws AdapterException {
    if (adapterDescription.getTransformationConfig()
                          .getScript() == null || adapterDescription.getTransformationConfig()
                                                                    .getLanguage() == null) {
      adapterDescription.getTransformationConfig()
                        .setOutputs(adapterDescription.getTransformationConfig().getInputs());

    } else {

      try {
        var transformationScript = adapterDescription.getTransformationConfig();
        var engine = TransformationEngines.INSTANCE.getTransformationEngine(transformationScript.getLanguage());
        var compiledScript = engine.compile(transformationScript.getScript());
        var scriptContext = new ScriptContextResolver().resolve(userId, transformationScript.getLanguage());

        var samples = adapterDescription.getTransformationConfig()
                                        .getInputs();
        if (!samples.isEmpty()) {
          List<Map<String, Object>> results = new ArrayList<>();
          compiledScript.transform(samples.get(0), results::add, scriptContext);

          adapterDescription.getTransformationConfig()
                            .setOutputs(results);
        } else {
          throw new AdapterException("No samples available to transform");
        }

      } catch (ScriptCompilationException | ScriptExecutionException e) {
        throw new AdapterException(String.format("Could not execute script: %s", e.getMessage()));
      }
    }

    return adapterDescription;
  }

  private ExtensionServiceRequestTarget getWorkerRequestTarget(
      AdapterDescription adapterDescription
  ) throws NoServiceEndpointsAvailableException {
    var selectedService = endpointGenerator.selectService(
        adapterDescription.getAppId(),
        SpServiceUrlProvider.ADAPTER,
        adapterDescription.getDeploymentConfiguration()
                          .getDesiredServiceTags()
    );

    return ExtensionServiceRequestTargets.adapterSampleData(selectedService);
  }

}
