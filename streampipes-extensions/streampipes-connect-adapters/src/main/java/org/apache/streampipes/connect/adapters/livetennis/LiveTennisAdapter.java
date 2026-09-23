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

package org.apache.streampipes.connect.adapters.livetennis;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LiveTennisAdapter implements StreamPipesAdapter, IPullAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.livetennis";
  private static final String API_KEY = "api-key";

  private final LiveTennisClient client;
  private PullAdapterScheduler scheduler;
  private IEventCollector collector;
  private String apiKey;

  public LiveTennisAdapter() {
    this(new LiveTennisClient());
  }

  LiveTennisAdapter(LiveTennisClient client) {
    this.client = client;
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 0, LiveTennisAdapter::new)
        .withLocales(Locales.EN, Locales.DE)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .requiredSecret(Labels.withId(API_KEY))
        .buildConfiguration();
  }

  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, LiveTennisClient.INTERVAL_SECONDS);
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.apiKey = extractor.getStaticPropertyExtractor().secretValue(API_KEY);
    this.collector = collector;
    this.scheduler = new PullAdapterScheduler();
    this.scheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void pullData() throws InterruptedException {
    try {
      for (var event : client.fetch(apiKey)) {
        collector.collect(event);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    if (scheduler != null) {
      scheduler.shutdown();
    }
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                          IAdapterGuessSchemaContext adapterGuessSchemaContext)
      throws AdapterException {
    try {
      var events = client.fetch(extractor.getStaticPropertyExtractor().secretValue(API_KEY));
      if (events.isEmpty()) {
        throw new AdapterException("No live tennis matches found. Request a sample when matches are in progress.");
      }
      return SampleDataBuilder.create().samples(events).fieldStatusInfos(Map.of()).build();
    } catch (IOException e) {
      throw new AdapterException(e.getMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AdapterException("Live tennis sample request was interrupted");
    }
  }
}
