/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.adapter.specific.coindesk;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.connect.adapter.specific.PullAdapter;
import org.streampipes.connect.adapter.specific.coindesk.model.CoindeskRawModel;
import org.streampipes.connect.adapter.util.PollingSettings;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.vocabulary.SO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoindeskBitcoinAdapter extends PullAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/coindesk/bitcoin";

  private static final String CoindeskUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";

  private Currency currency;

  public CoindeskBitcoinAdapter() {

  }

  public CoindeskBitcoinAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
    this.currency = Currency.valueOf(extractor.selectedSingleValueOption("currency"));

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
    return SpecificDataStreamAdapterBuilder.create(ID, "Coindesk Bitcoin Stream", "The current " +
            "bitcoin price from the Coindesk API.")
            .iconUrl("coindesk.png")
            .requiredSingleValueSelection(Labels.from("currency", "Currency", "The currency of the" +
                    " bitcoin rate"), Options.from("USD", "EUR", "GBP"))
            .build();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new CoindeskBitcoinAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
            .property(EpProperties.timestampProperty("timestamp"))
            .property(EpProperties.doubleEp(Labels.from("rate-field", "Rate", "The current " +
                    "bitcoin rate"), "rate", SO.Price))
            .build();
  }

  @Override
  public String getId() {
    return ID;
  }

}
