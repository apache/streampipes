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
package org.streampipes.connect.adapter.specific.iex;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.iex.model.IexStockData;
import org.streampipes.connect.adapter.util.PollingSettings;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IexCloudStockAdapter extends IexCloudAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/iexcloud/stocks";

  private static final String Quotes = "/quote";
  private static final String LatestUpdate = "latestUpdate";
  private static final String LatestPrice = "latestPrice";
  private static final String Symbol = "symbol";

  public IexCloudStockAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription, Quotes);
  }

  public IexCloudStockAdapter() {
    super();
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID, "IEX Cloud Stock Quotes", "Live stock data" +
            " provided by <a href='https://iexcloud.io'>IEX Cloud</a>")
            .iconUrl("iexcloud.png")
            .category(AdapterType.Finance)
            .requiredTextParameter(Labels.from("token", "API Token", "The IEXCloud API token"))
            .requiredTextParameter(Labels.from("stock", "Stock", "The stock symbol (e.g., AAPL"))
            .build();

  }

  @Override
  protected void pullData() {
    try {
      IexStockData rawModel = fetchResult(IexStockData.class);

      Map<String, Object> outMap = new HashMap<>();
      outMap.put(LatestUpdate, rawModel.getLatestUpdate());
      outMap.put(Symbol, rawModel.getSymbol());
      outMap.put(LatestPrice, rawModel.getLatestPrice());

      adapterPipeline.process(outMap);
    } catch (
            IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, 5);
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new IexCloudStockAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
            .property(EpProperties.timestampProperty(LatestUpdate))
            .property(EpProperties.stringEp(Labels.from("symbol", "Symbol",
                    "The stock symbol"), Symbol, SO.Text))
            .property(EpProperties.doubleEp(Labels.from("latest-price", "Latest price",
                    "The latest stock price"), LatestPrice, SO.Number))
            .build();
  }

  @Override
  public String getId() {
    return ID;
  }
}
