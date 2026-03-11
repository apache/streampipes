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

package org.apache.streampipes.extensions.connectors.camel.kamelet.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Locale;

public class KameletIconDecoder {

  private static final Logger LOG = LoggerFactory.getLogger(KameletIconDecoder.class);

  public byte[] decode(String iconValue) {
    if (iconValue == null || iconValue.isBlank()) {
      return null;
    }

    try {
      DecodedIcon decodedIcon = decodeIcon(iconValue);
      if (decodedIcon == null || decodedIcon.bytes.length == 0) {
        return null;
      }
      return decodedIcon.bytes;
    } catch (Exception e) {
      LOG.debug("Skipping invalid embedded Kamelet icon", e);
      return null;
    }
  }

  private DecodedIcon decodeIcon(String iconValue) {
    String trimmed = iconValue.trim();
    if (trimmed.startsWith("data:")) {
      return decodeDataUri(trimmed);
    }

    byte[] bytes = Base64.getMimeDecoder().decode(trimmed);
    return new DecodedIcon("image/png", bytes);
  }

  private DecodedIcon decodeDataUri(String dataUri) {
    int commaIndex = dataUri.indexOf(',');
    if (commaIndex < 0) {
      throw new IllegalArgumentException("Invalid data URI");
    }

    String metadata = dataUri.substring(5, commaIndex);
    String payload = dataUri.substring(commaIndex + 1);
    String mediaType = metadata.contains(";")
        ? metadata.substring(0, metadata.indexOf(';'))
        : metadata;

    if (!metadata.contains(";base64")) {
      throw new IllegalArgumentException("Only base64-encoded icon data URIs are supported");
    }

    if (mediaType.isBlank()) {
      mediaType = "image/png";
    }

    return new DecodedIcon(mediaType.toLowerCase(Locale.ROOT), Base64.getMimeDecoder().decode(payload));
  }

  private record DecodedIcon(String mediaType, byte[] bytes) {
  }
}
