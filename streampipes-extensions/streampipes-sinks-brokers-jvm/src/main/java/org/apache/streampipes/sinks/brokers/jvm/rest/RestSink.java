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

package org.apache.streampipes.sinks.brokers.jvm.rest;


import org.apache.http.HttpResponse;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestSink implements IStreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(RestSink.class);

  public static final String ID = "org.apache.streampipes.sinks.brokers.jvm.rest";
  private static final String URL_KEY = "url-key";
  private static final String HEADER_COLLECTION = "header-collection";
  private static final String HEADER_KEY = "header-key";
  private static final String HEADER_VALUE = "header-value";

  private String url;
  private JsonDataFormatDefinition jsonDataFormatDefinition;
  private List<RestHeaderConfiguration> headerConfigurations = new ArrayList<>();
  private static final int MAX_RETRIES = 3;
  private static final int RETRY_DELAY_MS = 1000;
  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(
            RestSink::new,
            DataSinkBuilder.create(ID, 0)
                    .category(DataSinkType.FORWARD)
                    .withAssets(ExtensionAssetType.DOCUMENTATION)
                    .withLocales(Locales.EN)
                    .requiredTextParameter(
                            Labels.from(URL_KEY, "Target URL", "The target URL of the REST endpoint to send events to (e.g., https://api.example.com/data)"),
                            false,
                            false
                    )
                    .requiredStaticProperty(
                            StaticProperties.collection(
                            Labels.from(
                                    HEADER_COLLECTION, "Headers Input", "Optional custom headers to be included with the REST request."),
                                    false,
                            StaticProperties.stringFreeTextProperty(
                                            Labels.from(HEADER_KEY, "Header Key", "The header key value to be included with the request. (e.g. Authorization, Content-Type)")),
                            StaticProperties.stringFreeTextProperty(
                                            Labels.from(HEADER_VALUE, "Header Value", "The header value to be included with the request."))
                            )
                    )
                    .requiredStream(StreamRequirementsBuilder
                            .create()
                            .requiredProperty(EpRequirements.anyProperty())
                            .build())
                    .build()
    );
  }

  private List<RestHeaderConfiguration> getHeaderConfigurations(IDataSinkParameterExtractor extractor) {
    List<RestHeaderConfiguration> headers = new ArrayList<>();
    var csp = (CollectionStaticProperty) extractor.getStaticPropertyByName(HEADER_COLLECTION);

    for (StaticProperty member : csp.getMembers()) {
      var memberExtractor = getMemberExtractor(member);

      var headerConfiguration = getHeaders(memberExtractor);

      headers.add(headerConfiguration);
    }
    return headers;
  }

  private StaticPropertyExtractor getMemberExtractor(StaticProperty member) {
    return StaticPropertyExtractor.from(
            ((StaticPropertyGroup) member).getStaticProperties(), new ArrayList<>()
    );
  }

  private RestHeaderConfiguration getHeaders(StaticPropertyExtractor memberExtractor) {
    var headerKey = memberExtractor.textParameter(HEADER_KEY);
    var headerValue = memberExtractor.textParameter(HEADER_VALUE);

    return new RestHeaderConfiguration(headerKey, headerValue);
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters parameters,
                                EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    jsonDataFormatDefinition = new JsonDataFormatDefinition();
    url = parameters.extractor().singleValueParameter(URL_KEY, String.class);
    headerConfigurations = getHeaderConfigurations(parameters.extractor());
  }

  @Override
  public void onEvent(Event event) {
    try {
      byte[] json = jsonDataFormatDefinition.fromMap(event.getRaw());
      for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        try {
          Request request = Request.Post(url)
                  .bodyByteArray(json, ContentType.APPLICATION_JSON)
                  .connectTimeout(1000)
                  .socketTimeout(100000);

          for(RestHeaderConfiguration header : headerConfigurations) {
            request.addHeader(header.headerKey(), header.headerValue());
          }
          Response response = request.execute();
          HttpResponse httpResponse = response.returnResponse();
          int statusCode = httpResponse.getStatusLine().getStatusCode();
          if (statusCode >= 200 && statusCode < 300) {
            LOG.info("Successfully sent event to {} with status {}", url, statusCode);
            return;
          } else if (statusCode >= 500) {
            LOG.warn("Server error {} from {}, retrying... (attempt {}/{})", statusCode, url, attempt + 1, MAX_RETRIES);
          } else {
            LOG.error("Received status {} from {}, not retrying.", statusCode, url);
            return;
          }
        } catch (IOException e) {
          LOG.warn("IO error when sending to {}, retrying... (attempt {}/{}) : {}", url, attempt + 1, MAX_RETRIES, e.getMessage());
        }
        if (attempt < MAX_RETRIES - 1) {
          try {
            Thread.sleep(RETRY_DELAY_MS);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while waiting to retry.");
            return;
          }
        } else {
          LOG.error("Failed to send event to {} after {} attempts.", url, MAX_RETRIES);
        }
      }
    } catch (SpRuntimeException e) {
      LOG.error("Error while serializing event: {} Exception: {}", event.getSourceInfo().getSourceId(), e);
    }
  }


  @Override
  public void onPipelineStopped() throws SpRuntimeException {
    this.headerConfigurations = null;
  }
}

