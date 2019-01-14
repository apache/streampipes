/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.manager.endpoint;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.Notifications;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

public class EndpointItemParser {

  public Message parseAndAddEndpointItem(String uri, String username, boolean publicElement) {
    try {
      uri = URLDecoder.decode(uri, "UTF-8");
      String payload = parseURIContent(uri, null);
      return Operations.verifyAndAddElement(payload, username, publicElement);
    } catch (Exception e) {
      e.printStackTrace();
      return Notifications.error(NotificationType.PARSE_ERROR, e.getMessage());

    }
  }

  private String parseURIContent(String payload, String mediaType) throws URISyntaxException, IOException {
    URI uri = new URI(payload);
    return HttpJsonParser.getContentFromUrl(uri, mediaType);
  }
}
