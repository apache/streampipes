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

package org.apache.streampipes.connect.iiot.adapters;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

public abstract class PullRestAdapter extends PullAdapter {

  public PullRestAdapter() {
    super();
  }

  public PullRestAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  protected static String getDataFromEndpointString(String url) throws AdapterException {
    String result = null;


    LOGGER.info("Started Request to open sensemap endpoint: " + url);
    try {
      result = Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();


      if (result.startsWith("ï")) {
        result = result.substring(3);
      }

      LOGGER.info("Received data from request");

    } catch (Exception e) {
      String errorMessage = "Error while connecting to the open sensemap api";
      LOGGER.error(errorMessage, e);
      throw new AdapterException(errorMessage);
    }

    return result;
  }

  protected static <T> T getDataFromEndpoint(String url, Class<T> clazz) throws AdapterException {

    String rawJson = getDataFromEndpointString(url);
    T all = new Gson().fromJson(rawJson, clazz);

    return all;
  }

}
