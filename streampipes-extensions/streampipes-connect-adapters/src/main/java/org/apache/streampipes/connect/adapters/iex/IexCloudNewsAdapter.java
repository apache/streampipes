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

//public class IexCloudNewsAdapter extends IexCloudAdapter {
//
//  public static final String ID = "org.apache.streampipes.connect.adapters.iex.news";
//
//  private static final String News = "/news";
//
//  private static final String Timestamp = "timestamp";
//  private static final String Headline = "headline";
//  private static final String Source = "source";
//  private static final String Url = "url";
//  private static final String Summary = "summary";
//  private static final String Related = "related";
//  private static final String Image = "image";
//  private static final String Lang = "lang";
//  private static final String HasPaywall = "hasPaywall";
//
//  public IexCloudNewsAdapter(SpecificAdapterStreamDescription adapterStreamDescription) {
//    super(adapterStreamDescription, News);
//  }
//
//  public IexCloudNewsAdapter() {
//    super();
//  }
//
//  @Override
//  protected void pullData() {
//    try {
//      IexNewsData[] rawModel = fetchResult(IexNewsData[].class);
//
//      for (IexNewsData newsData : rawModel) {
//        Map<String, Object> outMap = new HashMap<>();
//        outMap.put(Timestamp, newsData.getDatetime());
//        outMap.put(Headline, newsData.getHeadline());
//        outMap.put(Source, newsData.getSource());
//        outMap.put(Url, newsData.getUrl());
//        outMap.put(Summary, newsData.getSummary());
//        outMap.put(Related, newsData.getRelated());
//        outMap.put(Image, newsData.getImage());
//        outMap.put(Lang, newsData.getLang());
//        outMap.put(HasPaywall, newsData.getHasPaywall());
//
//        adapterPipeline.process(outMap);
//      }
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
//        .category(AdapterType.Finance, AdapterType.News)
//        .requiredSecret(Labels.withId(TOKEN_KEY))
//        .requiredTextParameter(Labels.withId(STOCK_SYMBOL_KEY))
//        .build();
//  }
//
//  @Override
//  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
//    return new IexCloudNewsAdapter(adapterDescription);
//  }
//
//  @Override
//  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
//      throws AdapterException, ParseException {
//    return GuessSchemaBuilder.create()
//        .property(EpProperties.timestampProperty(Timestamp))
//        .property(EpProperties.stringEp(Labels.from("headline", "Headline",
//            "The headline of the article"), Headline, SO.TEXT))
//        .property(EpProperties.stringEp(Labels.from("source", "Source",
//            "The source of the article"), Source, SO.TEXT))
//        .property(EpProperties.stringEp(Labels.from("url", "URL",
//            "The URL of the article"), Url, SO.CONTENT_URL))
//        .property(EpProperties.stringEp(Labels.from("summary", "Summary",
//            "A short summary of the article"), Summary, SO.TEXT))
//        .property(EpProperties.stringEp(Labels.from("related", "Related",
//            "A comma-separated list of related stock symbols"), Related, SO.TEXT))
//        .property(EpProperties.stringEp(Labels.from("image", "Image",
//            "Link to an image related to the news article"), Image, SO.IMAGE))
//        .property(EpProperties.stringEp(Labels.from("lang", "Language",
//            "The language the article is writte in"), Lang, SO.IN_LANGUAGE))
//        .property(EpProperties.stringEp(Labels.from("paywall", "Has Paywall",
//                "Indicates whether the article is behind a paywall"), HasPaywall,
//            SO.TEXT))
//        .build();
//  }
//
//  @Override
//  public String getId() {
//    return ID;
//  }
//}
