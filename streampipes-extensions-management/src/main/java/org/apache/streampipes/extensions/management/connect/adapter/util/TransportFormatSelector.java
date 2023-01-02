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
package org.apache.streampipes.extensions.management.connect.adapter.util;

import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.cbor.CborDataFormatDefinition;
import org.apache.streampipes.dataformat.fst.FstDataFormatDefinition;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.dataformat.smile.SmileDataFormatDefinition;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.vocabulary.MessageFormat;

public class TransportFormatSelector {

  private TransportFormat transportFormat;

  public TransportFormatSelector(TransportFormat format) {
    this.transportFormat = format;
  }

  public SpDataFormatDefinition getDataFormatDefinition() {
    if (isJsonFormat(transportFormat)) {
      return new JsonDataFormatDefinition();
    } else if (isCborFormat(transportFormat)) {
      return new CborDataFormatDefinition();
    } else if (isFstFormat(transportFormat)) {
      return new FstDataFormatDefinition();
    } else if (isSmileFormat(transportFormat)) {
      return new SmileDataFormatDefinition();
    } else {
      throw new IllegalArgumentException("Wrong transport format: " + makeError(transportFormat));
    }
  }

  private boolean isSmileFormat(TransportFormat transportFormat) {
    return isFormat(MessageFormat.SMILE, transportFormat);
  }

  private boolean isFstFormat(TransportFormat transportFormat) {
    return isFormat(MessageFormat.FST, transportFormat);
  }

  private boolean isCborFormat(TransportFormat transportFormat) {
    return isFormat(MessageFormat.CBOR, transportFormat);
  }

  private Boolean isJsonFormat(TransportFormat transportFormat) {
    return isFormat(MessageFormat.JSON, transportFormat);
  }

  private boolean isFormat(String format, TransportFormat transportFormat) {
    return transportFormat.getRdfType().stream().anyMatch(tf -> tf.toString().equals(format));
  }

  private String makeError(TransportFormat transportFormat) {
    StringBuilder builder = new StringBuilder();
    transportFormat.getRdfType().forEach(type -> builder.append(type.toString()).append(", "));
    return builder.toString();
  }
}
