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
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.management.AdapterEventPreviewPipeline;
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GuessManagement {

  private static Logger logger = LoggerFactory.getLogger(GuessManagement.class);
  private WorkerUrlProvider workerUrlProvider;

  public GuessManagement() {
    this.workerUrlProvider = new WorkerUrlProvider();
  }

  public GuessSchema guessSchema(AdapterDescription adapterDescription)
      throws ParseException, WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {
    var workerUrl = workerUrlProvider.getWorkerBaseUrl(adapterDescription.getAppId());
    workerUrl = workerUrl + WorkerPaths.getGuessSchemaPath();

    var objectMapper = JacksonSerializer.getObjectMapper();
    var description = objectMapper.writeValueAsString(adapterDescription);
    logger.info("Guess schema at: " + workerUrl);
    Response requestResponse = ExtensionServiceExecutions
        .extServicePostRequest(workerUrl, description)
        .execute();

    var httpResponse = requestResponse.returnResponse();
    var responseString = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return objectMapper.readValue(responseString, GuessSchema.class);
    } else {
      var exception = objectMapper.readValue(responseString, SpConfigurationException.class);
      throw new WorkerAdapterException(exception.getMessage(), exception.getCause());
    }
  }

  public String performAdapterEventPreview(AdapterEventPreview previewRequest) throws JsonProcessingException {
    return new AdapterEventPreviewPipeline(previewRequest).makePreview();
  }
}
