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
package org.apache.streampipes.connect.adapters.coindesk;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.adapters.coindesk.model.CoindeskRawModel;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPollingSettings;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoindeskBitcoinAdapter implements StreamPipesAdapter, IPullAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.coindesk";

  private static final String CURRENCY_KEY = "currency";
  private static final String CoindeskUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";

  private Currency currency;

  private IEventCollector collector;
  private PullAdapterScheduler pullAdapterScheduler;

  private String elementId;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, CoindeskBitcoinAdapter::new)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Finance)
        .requiredSingleValueSelection(Labels.withId(CURRENCY_KEY), Options.from("USD", "EUR", "GBP"))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.elementId = extractor.getAdapterDescription().getElementId();
    this.collector = collector;
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.currency = Currency.valueOf(
        extractor.selectedSingleValue(CURRENCY_KEY, String.class)
    );
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    var schemaBuilder = GuessSchemaBuilder.create()
        .property(EpProperties.timestampProperty("timestamp"))
        .property(EpProperties.doubleEp(Labels.from("rate-field", "Rate", "The current "
            + "bitcoin rate"), "rate", SO.PRICE));

    try {
      var sampleEvent = getDataFromEndpoint();
      sampleEvent.entrySet().forEach(entry -> {
        schemaBuilder.sample(entry.getKey(), entry.getValue());
      });
    } catch (IOException e) {
      // ignore preview
    }

    return schemaBuilder.build();
  }

  @Override
  public void pullData() throws RuntimeException {
    System.out.println("Pulling " + elementId);
    try {
      var outMap = getDataFromEndpoint();
      collector.collect(outMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<String, Object> getDataFromEndpoint() throws IOException {
    String response = Request.Get(CoindeskUrl).execute().returnContent().asString();
    CoindeskRawModel rawModel = new Gson().fromJson(response, CoindeskRawModel.class);

    long timestamp = System.currentTimeMillis();
    Double rate;
    if (currency == Currency.EUR) {
      rate = rawModel.getBpi().getEUR().getRateFloat();
    } else if (currency == Currency.GBP) {
      rate = rawModel.getBpi().getGBP().getRateFloat();
    } else {
      rate = rawModel.getBpi().getUSD().getRateFloat();
    }

    Map<String, Object> outMap = new HashMap<>();
    outMap.put("timestamp", timestamp);
    outMap.put("rate", rate);

    return outMap;
  }

  @Override
  public IPollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, 1);
  }
}
