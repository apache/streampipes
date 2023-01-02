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

package org.apache.streampipes.extensions.management.connect.adapter.format.image;

import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.sdk.builder.adapter.FormatDescriptionBuilder;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageFormat implements IFormat {

  public static final String ID = "https://streampipes.org/vocabulary/v1/format/image";

  public ImageFormat() {

  }

  @Override
  public IFormat getInstance(FormatDescription formatDescription) {
    return new ImageFormat();
  }

  @Override
  public FormatDescription declareModel() {
    return FormatDescriptionBuilder.create(ID, "Image", "Processes images and transforms them into events")
        .build();
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Map<String, Object> parse(byte[] object) throws ParseException {
    Map<String, Object> result = new HashMap<>();

    String resultImage = Base64.getEncoder().encodeToString(object);

    result.put("image", resultImage);

    return result;
  }
}
