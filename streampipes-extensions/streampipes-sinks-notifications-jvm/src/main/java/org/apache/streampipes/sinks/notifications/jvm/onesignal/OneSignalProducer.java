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
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OneSignalProducer implements EventSink<OneSignalParameters> {

  private String content;
  private String appId;
  private String apiKey;

  @Override
  public void onInvocation(OneSignalParameters parameters, EventSinkRuntimeContext runtimeContext)
      throws SpRuntimeException {
    this.content = parameters.getContent();
    this.appId = parameters.getAppId();
    this.apiKey = parameters.getApiKey();
  }

  @Override
  public void onEvent(Event inputEvent) {

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
