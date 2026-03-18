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

package org.apache.streampipes.model.extensions.transport;

import java.util.List;
import java.util.Objects;

public record ExtensionServiceBrokerOperation(String operationId,
                                              List<String> topicPrefixSegments) {

  public ExtensionServiceBrokerOperation {
    Objects.requireNonNull(operationId);
    Objects.requireNonNull(topicPrefixSegments);
    topicPrefixSegments = List.copyOf(topicPrefixSegments);
  }

  public String firstTopicSegment() {
    return topicSegment(0);
  }

  public String topicSegment(int index) {
    if (index < 0 || index >= topicPrefixSegments.size()) {
      throw new IllegalArgumentException("Invalid topic segment index " + index + " for operation " + operationId);
    }

    return topicPrefixSegments.get(index);
  }
}
