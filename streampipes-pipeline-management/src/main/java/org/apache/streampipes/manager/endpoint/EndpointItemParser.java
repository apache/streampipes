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

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.net.URLDecoder;

public class EndpointItemParser {

  public Message parseAndAddEndpointItem(String url,
                                         String principalSid,
                                         boolean publicElement) {
    try {
      url = URLDecoder.decode(url, "UTF-8");
      String payload = parseURIContent(url);
      return Operations.verifyAndAddElement(payload, principalSid, publicElement);
    } catch (Exception e) {
      e.printStackTrace();
      return Notifications.error(NotificationType.PARSE_ERROR, e.getMessage());

    }
  }

  private String parseURIContent(String url) throws IOException {
    return Request
        .Get(url)
        .addHeader("Accept", "application/json")
        .execute()
        .returnContent()
        .asString();
  }
}
