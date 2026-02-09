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
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.connect.transformer.api.TransformationEngines;
import org.apache.streampipes.connect.transformer.api.exception.ScriptCompilationException;
import org.apache.streampipes.connect.transformer.api.exception.ScriptExecutionException;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.manager.api.extensions.IExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuessManagement {

  private static final Logger LOG = LoggerFactory.getLogger(GuessManagement.class);
  private final IExtensionsServiceEndpointGenerator endpointGenerator;
  private final ObjectMapper objectMapper;

  public GuessManagement() {
    this.endpointGenerator = new ExtensionsServiceEndpointGenerator();
    this.objectMapper = JacksonSerializer.getObjectMapper();
  }

  /**
   * Guesses the event schema from the adapter description's transformation outputs.
   * Includes proper error handling for transformation rules that may reference missing properties.
   *
   * @param adapterDescription The adapter description containing transformation config
   * @return The inferred event schema
   * @throws AdapterException if schema guessing fails with detailed error information
   */
  public EventSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException {
    try {
      var transformationConfig = adapterDescription.getTransformationConfig();
      
      if (transformationConfig == null) {
        throw new AdapterException("Transformation config is null");
      }

      var outputs = transformationConfig.getOutputs();
      
      if (outputs == null || outputs.isEmpty()) {
        throw new AdapterException("No transformation outputs available to guess schema");
      }

      var event = outputs.get(0);
      
      try {
        return EventSchemaUtils.guessEventSchema(event);
      } catch (IllegalArgumentException e) {
        // This typically happens when a transformation rule references a property that has been moved
        LOG.error("Could not guess schema - possible issue with transformation rules: {}", e.getMessage());
        LOG.debug("Transformation rules that might be affected:", e);
        
        // Check if there are any schema rules that might be causing issues
        var schemaRules = transformationConfig.getSchemaTransformationRuleDescription();
        if (schemaRules != null && !schemaRules.isEmpty()) {
          LOG.warn("Adapter has {} schema transformation rule(s) that may need validation", schemaRules.size());
          throw new AdapterException(
              String.format(
                  "Could not guess schema due to transformation rule error: %s. "
                      + "Please ensure all schema transformation rules reference valid properties.",
                  e.getMessage()
              ),
              e
          );
        }
        throw new AdapterException("Could not guess schema: " + e.getMessage(), e);
      }
    } catch (AdapterException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("Unexpected error while guessing schema", e);
      throw new AdapterException("Unexpected error while guessing schema: " + e.getMessage(), e);
    }
  }

  public Map<String, Object> performAdapterEventPreview(AdapterDescription adapterDescription) {
    return new AdapterEventPreviewPipeline(adapterDescription).makePreview();
  }

  public SampleData getSampleData(AdapterDescription adapterDescription)
      throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {

    SecretProvider.getDecryptionService()
                  .apply(adapterDescription);

    var workerUrl = getWorkerUrl(adapterDescription, WorkerPaths.getSamplePath());

    var adapterDescriptionString = objectMapper.writeValueAsString(adapterDescription);

    LOG.debug("Calling get get sample data at: {}", workerUrl);

    var httpResponse = ExtensionServiceExecutions
        .extServicePostRequest(workerUrl, adapterDescriptionString)
        .execute()
        .returnResponse();

    var responseString = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine()
                    .getStatusCode() == HttpStatus.SC_OK) {
      return objectMapper.readValue(responseString, SampleData.class);
    } else {
      var exception = objectMapper.readValue(responseString, SpLogMessage.class);
      throw new WorkerAdapterException(exception);
    }
  }

  public AdapterDescription transformSampleData(AdapterDescription adapterDescription) throws AdapterException {
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

        var samples = adapterDescription.getTransformationConfig()
                                        .getInputs();
        if (!samples.isEmpty()) {
          List<Map<String, Object>> results = new ArrayList<>();
          compiledScript.transform(samples.get(0), results::add, null);

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

  private String getWorkerUrl(
      AdapterDescription adapterDescription,
      String suffix
  ) throws NoServiceEndpointsAvailableException {
    var baseUrl = endpointGenerator.getEndpointBaseUrl(
        adapterDescription.getAppId(),
        SpServiceUrlProvider.ADAPTER,
        adapterDescription.getDeploymentConfiguration()
                          .getDesiredServiceTags()
    );

    return baseUrl + suffix;
  }

}
