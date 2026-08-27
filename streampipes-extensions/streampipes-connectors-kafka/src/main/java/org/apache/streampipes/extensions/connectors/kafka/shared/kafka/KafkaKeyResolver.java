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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Works out the key that is attached to a published Kafka record.
 * <p>
 * Depending on the configured {@link KafkaMessageKeyMode}, the key is left out, a fixed text,
 * the value of a selected event field, or an expression that may contain field placeholders in the
 * form {@code #fieldName#}.
 */
public class KafkaKeyResolver implements Serializable {

  private static final long serialVersionUID = 1L;

  private final KafkaMessageKeyMode mode;
  private final String keyDefinition;

  /**
   * Build a resolver that publishes every record without a key.
   */
  public KafkaKeyResolver() {
    this(KafkaMessageKeyMode.NONE, "");
  }

  /**
   * Build a resolver for a key a user configured.
   *
   * @param mode how the key is put together.
   * @param keyDefinition what the user entered, which is a fixed text, a field selection, or an
   *                      expression.
   */
  public KafkaKeyResolver(KafkaMessageKeyMode mode, String keyDefinition) {
    this.mode = mode;
    this.keyDefinition = Objects.requireNonNullElse(keyDefinition, "");

    if (needsKeyDefinition(mode) && this.keyDefinition.isBlank()) {
      throw new SpRuntimeException(
          "Message key mode " + mode + " needs a key definition but none was configured");
    }
  }

  /**
   * Work out the key for a single event.
   *
   * @param event the event that is about to be published.
   * @return the key, or an empty result if the record is published without one.
   */
  public Optional<String> resolveKey(Event event) {
    return switch (mode) {
      case NONE -> Optional.empty();
      case STATIC -> resolveStaticKey();
      case FIELD -> resolveFieldKey(event);
      case EXPRESSION -> resolveExpressionKey(event);
    };
  }

  /**
   * Take the fixed text a user entered.
   *
   * @return the configured input as key, or an empty result if none was entered.
   */
  private Optional<String> resolveStaticKey() {
    return transformToKey(keyDefinition);
  }

  /**
   * Read the value of the field a user picked as key.
   *
   * @param event the event that is about to be published.
   * @return the value of the picked field as key, or an empty result if the field holds no value.
   */
  private Optional<String> resolveFieldKey(Event event) {
    return Optional.ofNullable(getPrimitiveField(event).getRawValue())
        .map(String::valueOf)
        .flatMap(KafkaKeyResolver::transformToKey);
  }

  /**
   * Fill the placeholders in the expression with values of the current event.
   * A placeholder that matches no field of the event is left as it is.
   *
   * @param event the event that is about to be published.
   * @return the filled expression as key, or an empty result if nothing is left of it.
   */
  private Optional<String> resolveExpressionKey(Event event) {
    try {
      return transformToKey(PlaceholderExtractor.replacePlaceholders(event, keyDefinition));
    } catch (RuntimeException e) {
      throw new SpRuntimeException(
          "Could not work out the Kafka message key from expression " + keyDefinition, e);
    }
  }

  /**
   * Look up the field a user picked and make sure it holds a single value.
   *
   * @param event the event that is about to be published.
   * @return the picked field.
   */
  private PrimitiveField getPrimitiveField(Event event) {
    try {
      var field = event.getFieldBySelector(keyDefinition);

      if (!field.isPrimitive()) {
        throw new SpRuntimeException("Only primitive fields can be used as Kafka message key, "
            + "but field " + keyDefinition + " is not primitive");
      }
      return field.getAsPrimitive();
    } catch (IllegalArgumentException e) {
      throw new SpRuntimeException(
          "Field " + keyDefinition + " which was selected as Kafka message key "
              + "is not part of the event", e);
    }
  }

  /**
   * Turn a piece of text into a key, unless it carries no content.
   *
   * @param value the text that was configured or worked out for the current event.
   * @return the text as key, or an empty result if it carries no content.
   */
  private static Optional<String> transformToKey(String value) {
    return value.isBlank() ? Optional.empty() : Optional.of(value);
  }

  /**
   * Tell whether a mode can do anything at all without a key definition.
   *
   * @param mode how the key is put together.
   * @return {@code true} if the mode is unusable without a key definition.
   * Otherwise, {@code false}.
   */
  private static boolean needsKeyDefinition(KafkaMessageKeyMode mode) {
    return mode == KafkaMessageKeyMode.FIELD || mode == KafkaMessageKeyMode.EXPRESSION;
  }
}
