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

package org.apache.streampipes.manager.endpoint;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;


@SuppressWarnings("deprecation")
public class HttpJsonParser {

  public static String getContentFromUrl(URI uri) throws ClientProtocolException, IOException {
    return getContentFromUrl(uri, null);
  }

  public static String getContentFromUrl(URI uri, String header) throws ClientProtocolException, IOException {
    HttpGet request = new HttpGet(uri);
    if (header != null) {
      request.addHeader("Accept", header);
    }

    @SuppressWarnings("resource")
    HttpClient client = new DefaultHttpClient();
    HttpResponse response = client.execute(request);

    String pageContent = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

    return pageContent;

  }
}
