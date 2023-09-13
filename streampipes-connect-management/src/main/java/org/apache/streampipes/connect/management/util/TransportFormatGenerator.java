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
package org.apache.streampipes.connect.management.util;

import org.apache.streampipes.model.configuration.SpDataFormat;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.sdk.helpers.SupportedFormats;
import org.apache.streampipes.vocabulary.MessageFormat;

import java.util.Arrays;
import java.util.List;

public class TransportFormatGenerator {

  public static TransportFormat getTransportFormat() {
    var cfg = Utils.getCoreConfigStorage().get();
    List<SpDataFormat> supportedFormats =
        cfg.getMessagingSettings().getPrioritizedFormats();

    if (supportedFormats.size() > 0) {
      return new TransportFormat(supportedFormats.get(0).getMessageFormat());
    } else {
      return new TransportFormat(MessageFormat.JSON);
    }
  }

  public static List<TransportFormat> getAllFormats() {
    return Arrays.asList(SupportedFormats.cborFormat(),
        SupportedFormats.jsonFormat(),
        SupportedFormats.fstFormat(),
        SupportedFormats.smileFormat());
  }
}
