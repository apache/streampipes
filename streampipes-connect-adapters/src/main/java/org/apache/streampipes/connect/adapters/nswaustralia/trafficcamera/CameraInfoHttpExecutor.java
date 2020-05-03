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
package org.apache.streampipes.connect.adapters.nswaustralia.trafficcamera;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.connect.adapters.nswaustralia.trafficcamera.model.FeatureCollection;

import java.io.IOException;

public class CameraInfoHttpExecutor {

  private static final String Url = "https://api.transport.nsw.gov.au/v1/live/cameras";

  private String apiKey;

  public CameraInfoHttpExecutor(String apiKey) {
    this.apiKey = apiKey;
  }

  public FeatureCollection getCameraData() throws IOException {

    String response = Request
            .Get(Url)
            .addHeader("Authorization", "apiKey " +this.apiKey)
            .execute()
            .returnContent()
            .asString();

    return new Gson().fromJson(response, FeatureCollection.class);
  }
}
