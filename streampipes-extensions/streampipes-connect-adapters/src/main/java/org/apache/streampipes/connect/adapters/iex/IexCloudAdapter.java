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


//public abstract class IexCloudAdapter extends PullAdapter {
//
//  protected static final String IEX_CLOUD_BASE_URL = "https://cloud.iexapis.com/stable/stock/";
//  protected static final String TOKEN = "?token=";
//
//  protected static final String TOKEN_KEY = "token";
//  protected static final String STOCK_SYMBOL_KEY = "stock-symbol";
//
//  protected String apiToken;
//  protected String stockQuote;
//  private String iexCloudInstanceUrl;
//
//
//  public IexCloudAdapter(SpecificAdapterStreamDescription adapterDescription, String restPath) {
//    super(adapterDescription);
//    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
//    this.apiToken = extractor.secretValue(TOKEN_KEY);
//    this.stockQuote = extractor.singleValue(STOCK_SYMBOL_KEY);
//    this.iexCloudInstanceUrl = IEX_CLOUD_BASE_URL + stockQuote + restPath + TOKEN + apiToken;
//
//  }
//
//  public IexCloudAdapter() {
//    super();
//  }
//
//  protected <T> T fetchResult(Class<T> classToParse) throws IOException {
//    String response = Request.Get(iexCloudInstanceUrl).execute().returnContent().asString();
//    return new Gson().fromJson(response, classToParse);
//  }
//}
