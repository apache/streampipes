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

public class Formats {

  /**
   * Defines the transport format JSON used by a data stream at runtime.
   *
   * @return The {@link org.apache.streampipes.model.grounding.TransportFormat} of type JSON.
   */
  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.JSON);
  }

  /**
   * Defines the transport format CBOR used by a data stream at runtime.
   *
   * @return The {@link org.apache.streampipes.model.grounding.TransportFormat} of type CBOR.
   */
  public static TransportFormat cborFormat() {
    return new TransportFormat(MessageFormat.CBOR);
  }

  /**
   * Defines the transport format Fast-Serializer used by a data stream at runtime.
   *
   * @return The {@link org.apache.streampipes.model.grounding.TransportFormat} of type FST.
   */
  public static TransportFormat fstFormat() {
    return new TransportFormat(MessageFormat.FST);
  }

  /**
   * Defines the transport format SMILE used by a data stream at runtime.
   *
   * @return The {@link org.apache.streampipes.model.grounding.TransportFormat} of type SMILE.
   */
  public static TransportFormat smileFormat() {
    return new TransportFormat(MessageFormat.SMILE);
  }

  /**
   * Defines the transport format Apache Thrift used by a data stream at runtime.
   *
   * @return The {@link org.apache.streampipes.model.grounding.TransportFormat} of type Thrift.
   */
  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.THRIFT);
  }
}
