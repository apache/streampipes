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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

/**
 * Defines how the key of a published Kafka record is determined.
 */
public enum KafkaMessageKeyMode {

  /**
   * Records are published without a key.
   */
  NONE,

  /**
   * The same, user-defined key is attached to every record.
   */
  STATIC,

  /**
   * The value of a selected event field is used as key.
   */
  FIELD,

  /**
   * The key is built from a text expression which may contain field placeholders.
   */
  EXPRESSION;

  /**
   * Work out which mode a user picked in the configuration dialog.
   * An unknown id falls back to {@link #NONE}, so records are published without a key.
   *
   * @param id the internal id of the selected alternative.
   * @return the matching mode.
   */
  public static KafkaMessageKeyMode fromSelectedAlternative(String id) {
    return switch (id) {
      case KafkaConfigProvider.STATIC_MESSAGE_KEY -> STATIC;
      case KafkaConfigProvider.FIELD_MESSAGE_KEY -> FIELD;
      case KafkaConfigProvider.EXPRESSION_MESSAGE_KEY -> EXPRESSION;
      default -> NONE;
    };
  }
}
