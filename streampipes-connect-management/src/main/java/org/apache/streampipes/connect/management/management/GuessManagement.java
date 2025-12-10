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
import org.apache.streampipes.commons.exceptions.connect.ParseException;
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
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class GuessManagement {

  private static final Logger LOG = LoggerFactory.getLogger(GuessManagement.class);
  private final IExtensionsServiceEndpointGenerator endpointGenerator;
  private final ObjectMapper objectMapper;
  private final ObjectMapper plainObjectMapper;

  public GuessManagement() {
    this.endpointGenerator = new ExtensionsServiceEndpointGenerator();
    this.objectMapper = JacksonSerializer.getObjectMapper();
    this.plainObjectMapper = new ObjectMapper();
  }

  @Deprecated
  public GuessSchema guessSchemaOld(AdapterDescription adapterDescription)
      throws ParseException, WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {
    SecretProvider.getDecryptionService().apply(adapterDescription);
    var workerUrl = getWorkerUrl(adapterDescription, WorkerPaths.getGuessSchemaPath());
    var description = objectMapper.writeValueAsString(adapterDescription);

    LOG.debug("Calling guess schema at: {}", workerUrl);
    Response requestResponse = ExtensionServiceExecutions
        .extServicePostRequest(workerUrl, description)
        .execute();

    var httpResponse = requestResponse.returnResponse();
    var responseString = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return objectMapper.readValue(responseString, GuessSchema.class);
    } else {
      var exception = objectMapper.readValue(responseString, SpLogMessage.class);
      throw new WorkerAdapterException(exception);
    }
  }

  public GuessSchema guessSchema(AdapterDescription adapterDescription) {
    var event = adapterDescription.getSchemaTransformationConfig().getOutputs().get(0);
    return EventSchemaUtils.getGuessSchema(event);
  }

  @Deprecated
  public String performAdapterEventPreview(AdapterEventPreview previewRequest) throws JsonProcessingException {
    return new AdapterEventPreviewPipeline(previewRequest).makePreview();
  }

  public SampleData getSampleData(AdapterDescription adapterDescription)
      throws WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {

    SecretProvider.getDecryptionService().apply(adapterDescription);

    var workerUrl = getWorkerUrl(adapterDescription, WorkerPaths.getSamplePath());

    var adapterDescriptionString = objectMapper.writeValueAsString(adapterDescription);

    LOG.debug("Calling get get sample data at: {}", workerUrl);

    var httpResponse = ExtensionServiceExecutions
        .extServicePostRequest(workerUrl, adapterDescriptionString)
        .execute()
        .returnResponse();

    var responseString = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return plainObjectMapper.readValue(responseString, SampleData.class);
    } else {
      var exception = objectMapper.readValue(responseString, SpLogMessage.class);
      throw new WorkerAdapterException(exception);
    }
  }

  public AdapterDescription transformSampleData(AdapterDescription adapterDescription) throws AdapterException {
    try {
      var transformationScript = adapterDescription.getSchemaTransformationConfig();
      var engine = TransformationEngines.INSTANCE.getTransformationEngine(transformationScript.getLanguage());
      var compiledScript = engine.compile(transformationScript.getScript());

      var samples = adapterDescription.getSchemaTransformationConfig().getInputs();
      if (!samples.isEmpty()) {
        var result = compiledScript.transform(samples.get(0));

        adapterDescription.getSchemaTransformationConfig().setOutputs(List.of(result));
        return adapterDescription;
      } else {
        throw new AdapterException("No samples available to transform");
      }

    } catch (ScriptCompilationException | ScriptExecutionException e) {
      throw new AdapterException(String.format("Could not execute script: %s", e.getMessage()));
    }
  }

  private String getWorkerUrl(AdapterDescription adapterDescription,
                              String suffix) throws NoServiceEndpointsAvailableException {
    var baseUrl = endpointGenerator.getEndpointBaseUrl(
        adapterDescription.getAppId(),
        SpServiceUrlProvider.ADAPTER,
        adapterDescription.getDeploymentConfiguration().getDesiredServiceTags());

    return baseUrl + suffix;
  }

}
