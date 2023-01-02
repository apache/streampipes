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

import org.apache.streampipes.connect.adapters.PullAdapter;
import org.apache.streampipes.connect.adapters.coindesk.model.CoindeskRawModel;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
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

public class CoindeskBitcoinAdapter extends PullAdapter {

  public static final String ID = "org.apache.streampipes.connect.adapters.coindesk";

  private static final String CURRENCY_KEY = "currency";
  private static final String CoindeskUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";

  private Currency currency;

  public CoindeskBitcoinAdapter() {

  }

  public CoindeskBitcoinAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
    this.currency = Currency.valueOf(extractor.selectedSingleValueOption(CURRENCY_KEY));

  }

  @Override
  protected void pullData() {
    try {
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

      adapterPipeline.process(outMap);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, 60);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(AdapterType.Finance)
        .requiredSingleValueSelection(Labels.withId(CURRENCY_KEY), Options.from("USD", "EUR", "GBP"))
        .build();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new CoindeskBitcoinAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
        .property(EpProperties.timestampProperty("timestamp"))
        .property(EpProperties.doubleEp(Labels.from("rate-field", "Rate", "The current "
            + "bitcoin rate"), "rate", SO.PRICE))
        .build();
  }

  @Override
  public String getId() {
    return ID;
  }

}
