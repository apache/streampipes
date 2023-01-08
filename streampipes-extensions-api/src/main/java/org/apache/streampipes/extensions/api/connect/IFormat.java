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

package org.apache.streampipes.extensions.api.connect;


import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.model.connect.grounding.FormatDescription;

import java.util.Map;

public interface IFormat {

  IFormat getInstance(FormatDescription formatDescription);

  FormatDescription declareModel();

  String getId();

  /**
   * This method parses a byte[] and transforms the event object into a serialized version of the internal
   * representation
   */
  Map<String, Object> parse(byte[] object) throws ParseException;

  /**
   * Needed for example for the CSV format in iterative protocols to ensure header is not send again
   * When the reset is not required it can be ignored
   */
  default void reset() {

  }

}
