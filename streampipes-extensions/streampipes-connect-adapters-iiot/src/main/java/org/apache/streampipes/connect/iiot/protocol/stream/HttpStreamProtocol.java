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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class HttpStreamProtocol implements StreamPipesAdapter, IPullAdapter {

  private static final Logger logger = LoggerFactory.getLogger(HttpStreamProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.http";

  private static final String URL_PROPERTY = "url";
  private static final String INTERVAL_PROPERTY = "interval";
  private static final String AUTH_MODE = "auth-mode";
  private static final String AUTH_NONE = "auth-none";
  private static final String AUTH_BEARER = "auth-bearer";
  private static final String BEARER_TOKEN = "bearer-token";

  private String url;
  private String accessToken;

  private PollingSettings pollingSettings;
  private PullAdapterScheduler pullAdapterScheduler;

  private IEventCollector collector;
  private IParser parser;

  public HttpStreamProtocol() {
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.url = extractor.singleValueParameter(URL_PROPERTY, String.class);
    int interval = extractor.singleValueParameter(INTERVAL_PROPERTY, Integer.class);
    this.pollingSettings = PollingSettings.from(TimeUnit.SECONDS, interval);
    String selectedAuthMode = extractor.selectedAlternativeInternalId(AUTH_MODE);
    if (AUTH_BEARER.equals(selectedAuthMode)) {
      this.accessToken = extractor.singleValueParameter(BEARER_TOKEN, String.class);
    } else {
      this.accessToken = null;
    }
  }

  private InputStream getDataFromEndpoint() throws ParseException {

    try {
      Request request = Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000);

      if (this.accessToken != null && !this.accessToken.isEmpty()) {
        request.setHeader("Authorization", "Bearer " + this.accessToken);
      }

      var result = request
          .execute().returnContent().asStream();

      if (result == null) {
        throw new ParseException("Could not receive Data from file: " + url);
      } else {
        return result;
      }

    } catch (IOException e) {
      logger.error("Error while fetching data from URL: " + url, e);
      throw new ParseException("Error while fetching data from URL: " + url);
    }
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, 1, HttpStreamProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId(URL_PROPERTY))
        .requiredIntegerParameter(Labels.withId(INTERVAL_PROPERTY))
        .requiredAlternatives(
            Labels.withId(AUTH_MODE),
            Alternatives.from(Labels.withId(AUTH_NONE), true),
            Alternatives.from(
                Labels.withId(AUTH_BEARER),
                StaticProperties.stringFreeTextProperty(Labels.withId(BEARER_TOKEN))))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.parser = extractor.selectedParser();

    this.collector = collector;
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                      IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    var dataInputStream = getDataFromEndpoint();

    return extractor.selectedParser().getSampleData(dataInputStream);
  }

  @Override
  public void pullData() throws RuntimeException {
    var result = getDataFromEndpoint();
    parser.parse(result, (event ->
        collector.collect(event))
    );
  }

  @Override
  public PollingSettings getPollingInterval() {
    return pollingSettings;
  }
}
