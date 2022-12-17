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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.vocabulary.MessageFormat;

public class SupportedFormats {

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in JSON format
   *
   * @return The resulting {@link org.apache.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.JSON);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in Thrift
   * format
   *
   * @return The resulting {@link org.apache.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.THRIFT);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging
   * arriving in Cbor format
   *
   * @return The resulting {@link org.apache.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat cborFormat() {
    return new TransportFormat(MessageFormat.CBOR);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging
   * arriving in fast-serialization format
   *
   * @return The resulting {@link org.apache.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat fstFormat() {
    return new TransportFormat(MessageFormat.FST);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging
   * arriving in smile format
   *
   * @return The resulting {@link org.apache.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat smileFormat() {
    return new TransportFormat(MessageFormat.SMILE);
  }
}
