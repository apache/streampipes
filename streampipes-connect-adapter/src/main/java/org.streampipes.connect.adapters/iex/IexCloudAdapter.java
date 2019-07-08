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

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.streampipes.connect.adapter.specific.PullAdapter;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;

import java.io.IOException;

public abstract class IexCloudAdapter extends PullAdapter {

  protected static final String IexCloudBaseUrl = "https://cloud.iexapis.com/stable/stock/";
  protected static final String Token = "?token=";

  protected String apiToken;
  protected String stockQuote;
  private String iexCloudInstanceUrl;


  public IexCloudAdapter(SpecificAdapterStreamDescription adapterDescription, String restPath) {
    super(adapterDescription);
    ParameterExtractor extractor = new ParameterExtractor(adapterDescription.getConfig());
    this.apiToken = extractor.singleValue("token");
    this.stockQuote = extractor.singleValue("stock");
    this.iexCloudInstanceUrl = IexCloudBaseUrl + stockQuote + restPath + Token + apiToken;

  }

  public IexCloudAdapter() {
    super();
  }

  protected <T> T fetchResult(Class<T> classToParse) throws IOException {
    String response = Request.Get(iexCloudInstanceUrl).execute().returnContent().asString();
    return new Gson().fromJson(response, classToParse);
  }
}
