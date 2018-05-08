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

package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;

public class SupportedFormats {

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in JSON format
   * @return The resulting {@link org.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.Json);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in Thrift
   * format
   * @return The resulting {@link org.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.Thrift);
  }
}
