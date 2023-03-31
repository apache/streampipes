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

//public class CoindeskBitcoinAdapter extends PullAdapter {
//
//  public static final String ID = "org.apache.streampipes.connect.adapters.coindesk";
//
//  private static final String CURRENCY_KEY = "currency";
//  private static final String CoindeskUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";
//
//  private Currency currency;
//
//  public CoindeskBitcoinAdapter() {
//
//  }
//
//  public CoindeskBitcoinAdapter(SpecificAdapterStreamDescription adapterDescription) {
//    super(adapterDescription);
//    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
//    this.currency = Currency.valueOf(extractor.selectedSingleValueOption(CURRENCY_KEY));
//
//  }
//
//  @Override
//  protected void pullData() {
//    try {
//      String response = Request.Get(CoindeskUrl).execute().returnContent().asString();
//      CoindeskRawModel rawModel = new Gson().fromJson(response, CoindeskRawModel.class);
//
//      long timestamp = System.currentTimeMillis();
//      Double rate;
//      if (currency == Currency.EUR) {
//        rate = rawModel.getBpi().getEUR().getRateFloat();
//      } else if (currency == Currency.GBP) {
//        rate = rawModel.getBpi().getGBP().getRateFloat();
//      } else {
//        rate = rawModel.getBpi().getUSD().getRateFloat();
//      }
//
//      Map<String, Object> outMap = new HashMap<>();
//      outMap.put("timestamp", timestamp);
//      outMap.put("rate", rate);
//
//      adapterPipeline.process(outMap);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Override
//  protected PollingSettings getPollingInterval() {
//    return PollingSettings.from(TimeUnit.SECONDS, 60);
//  }
//
//  @Override
//  public SpecificAdapterStreamDescription declareModel() {
//    return SpecificDataStreamAdapterBuilder.create(ID)
//        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
//        .withLocales(Locales.EN)
//        .category(AdapterType.Finance)
//        .requiredSingleValueSelection(Labels.withId(CURRENCY_KEY), Options.from("USD", "EUR", "GBP"))
//        .build();
//  }
//
//  @Override
//  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
//    return new CoindeskBitcoinAdapter(adapterDescription);
//  }
//
//  @Override
//  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
//      throws AdapterException, ParseException {
//    return GuessSchemaBuilder.create()
//        .property(EpProperties.timestampProperty("timestamp"))
//        .property(EpProperties.doubleEp(Labels.from("rate-field", "Rate", "The current "
//            + "bitcoin rate"), "rate", SO.PRICE))
//        .build();
//  }
//
//  @Override
//  public String getId() {
//    return ID;
//  }
//
//}
