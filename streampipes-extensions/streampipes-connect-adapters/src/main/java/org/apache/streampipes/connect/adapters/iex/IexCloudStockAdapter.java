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

package org.apache.streampipes.connect.adapters.iex;

//public class IexCloudStockAdapter extends IexCloudAdapter {
//
//  public static final String ID = "org.apache.streampipes.connect.adapters.iex.stocks";
//
//  private static final String Quotes = "/quote";
//  private static final String LatestUpdate = "latestUpdate";
//  private static final String LatestPrice = "latestPrice";
//  private static final String Symbol = "symbol";
//
//  public IexCloudStockAdapter(SpecificAdapterStreamDescription adapterDescription) {
//    super(adapterDescription, Quotes);
//  }
//
//  public IexCloudStockAdapter() {
//    super();
//  }
//
//  @Override
//  public SpecificAdapterStreamDescription declareModel() {
//    return SpecificDataStreamAdapterBuilder.create(ID)
//        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
//        .withLocales(Locales.EN)
//        .category(AdapterType.Finance)
//        .requiredSecret(Labels.withId(TOKEN_KEY))
//        .requiredTextParameter(Labels.withId(STOCK_SYMBOL_KEY))
//        .build();
//
//  }
//
//  @Override
//  protected void pullData() {
//    try {
//      IexStockData rawModel = fetchResult(IexStockData.class);
//
//      Map<String, Object> outMap = new HashMap<>();
//      outMap.put(LatestUpdate, rawModel.getLatestUpdate());
//      outMap.put(Symbol, rawModel.getSymbol());
//      outMap.put(LatestPrice, rawModel.getLatestPrice());
//
//      adapterPipeline.process(outMap);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Override
//  protected PollingSettings getPollingInterval() {
//    return PollingSettings.from(TimeUnit.SECONDS, 5);
//  }
//
//  @Override
//  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
//    return new IexCloudStockAdapter(adapterDescription);
//  }
//
//  @Override
//  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
//      throws AdapterException, ParseException {
//    return GuessSchemaBuilder.create()
//        .property(EpProperties.timestampProperty(LatestUpdate))
//        .property(EpProperties.stringEp(Labels.from("symbol", "Symbol",
//            "The stock symbol"), Symbol, SO.TEXT))
//        .property(EpProperties.doubleEp(Labels.from("latest-price", "Latest price",
//            "The latest stock price"), LatestPrice, SO.NUMBER))
//        .build();
//  }
//
//  @Override
//  public String getId() {
//    return ID;
//  }
//}
