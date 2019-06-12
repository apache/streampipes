/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.connect.adapter.util;

import org.streampipes.config.backend.BackendConfig;
import org.streampipes.config.backend.SpDataFormat;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.vocabulary.MessageFormat;

import java.util.Arrays;
import java.util.List;

public class TransportFormatGenerator {

  public static TransportFormat getTransportFormat() {
    List<SpDataFormat> supportedFormats =
            BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedFormats();

    if (supportedFormats.size() > 0) {
      return new TransportFormat(supportedFormats.get(0).getMessageFormat());
    } else {
      return new TransportFormat(MessageFormat.Json);
    }
  }

  public static List<TransportFormat> getAllFormats() {
    return Arrays.asList(SupportedFormats.cborFormat(),
            SupportedFormats.jsonFormat(),
            SupportedFormats.fstFormat(),
            SupportedFormats.smileFormat());
  }
}
