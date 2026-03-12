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

package org.apache.streampipes.commons.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public final class ImageMimeTypeDetector {

  private static final String DEFAULT_IMAGE_TYPE = "image/png";
  private static final String SVG_IMAGE_TYPE = "image/svg+xml";

  private ImageMimeTypeDetector() {
  }

  public static String detect(byte[] imageBytes) {
    if (imageBytes == null || imageBytes.length == 0) {
      return DEFAULT_IMAGE_TYPE;
    }

    if (isSvg(imageBytes)) {
      return SVG_IMAGE_TYPE;
    }

    try {
      String detected = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(imageBytes));
      return detected == null ? DEFAULT_IMAGE_TYPE : detected;
    } catch (IOException e) {
      return DEFAULT_IMAGE_TYPE;
    }
  }

  private static boolean isSvg(byte[] imageBytes) {
    int length = Math.min(imageBytes.length, 512);
    String prefix = new String(imageBytes, 0, length, StandardCharsets.UTF_8)
        .replace("\uFEFF", "")
        .trim();

    return prefix.startsWith("<svg")
        || prefix.startsWith("<?xml") && prefix.contains("<svg");
  }
}
