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
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.api.connect.exception.WorkerAdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterEventPreviewPipeline;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.AdapterEventPreview;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.guess.GuessTypeInfo;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class GuessManagement {

  private static Logger logger = LoggerFactory.getLogger(GuessManagement.class);
  private WorkerUrlProvider workerUrlProvider;

  public GuessManagement() {
    this.workerUrlProvider = new WorkerUrlProvider();
  }

  public GuessSchema guessSchema(AdapterDescription adapterDescription)
      throws ParseException, WorkerAdapterException, NoServiceEndpointsAvailableException, IOException {
    String workerUrl = workerUrlProvider.getWorkerBaseUrl(adapterDescription.getAppId());

    workerUrl = workerUrl + WorkerPaths.getGuessSchemaPath();

    ObjectMapper mapper = JacksonSerializer.getObjectMapper();
    String ad = mapper.writeValueAsString(adapterDescription);
    logger.info("Guess schema at: " + workerUrl);
    Response requestResponse = Request.Post(workerUrl)
        .bodyString(ad, ContentType.APPLICATION_JSON)
        .connectTimeout(1000)
        .socketTimeout(100000)
        .execute();

    HttpResponse httpResponse = requestResponse.returnResponse();
    String responseString = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
      return mapper.readValue(responseString, GuessSchema.class);
    } else {
      var exception = mapper.readValue(responseString, SpConfigurationException.class);
      throw new WorkerAdapterException(exception.getMessage(), exception.getCause());
    }
  }

  public Map<String, GuessTypeInfo> performAdapterEventPreview(AdapterEventPreview previewRequest) {
    return new AdapterEventPreviewPipeline(previewRequest).makePreview();
  }
}
