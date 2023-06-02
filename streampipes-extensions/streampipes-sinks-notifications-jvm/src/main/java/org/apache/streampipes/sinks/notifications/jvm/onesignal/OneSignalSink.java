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

package org.apache.streampipes.sinks.notifications.jvm.onesignal;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OneSignalSink extends StreamPipesDataSink {

  private static final String CONTENT_KEY = "content";
  private static final String APP_ID = "app_id";
  private static final String REST_API_KEY = "api_key";

  private String content;
  private String appId;
  private String apiKey;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.onesignal")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredHtmlInputParameter(Labels.withId(CONTENT_KEY))
        .requiredTextParameter(Labels.withId(APP_ID))
        .requiredTextParameter(Labels.withId(REST_API_KEY))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    content = extractor.singleValueParameter(CONTENT_KEY, String.class);
    appId = extractor.singleValueParameter(APP_ID, String.class);
    apiKey = extractor.singleValueParameter(REST_API_KEY, String.class);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    String jsondata =
        "{\"app_id\": \"" + appId + "\",\"contents\": {\"en\": \"" + content + "\"}, \"included_segments\":[\"All\"]}";

    StringEntity jsonparam = null;
    try {
      jsonparam = new StringEntity(jsondata);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    jsonparam.setContentType("application/json;charset=utf-8");
    jsonparam.setChunked(true);

    HttpClient httpclient = HttpClients.createDefault();
    HttpPost httppost = new HttpPost("https://onesignal.com/api/v1/notifications");
    httppost.addHeader("Authorization", "Basic " + this.apiKey);
    httppost.setEntity(jsonparam);

    HttpResponse response = null;
    try {
      response = httpclient.execute(httppost);
    } catch (IOException e) {
      e.printStackTrace();
    }
    HttpEntity entity = response.getEntity();
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
