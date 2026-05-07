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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.subscriptions.EventFilterBuilder;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.NodeIds;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.encoding.EncodingContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.FilterOperator;
import org.eclipse.milo.opcua.stack.core.types.structured.ContentFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.ContentFilterElement;
import org.eclipse.milo.opcua.stack.core.types.structured.EventFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.LiteralOperand;
import org.eclipse.milo.opcua.stack.core.types.structured.SimpleAttributeOperand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OpcUaAlarmEventMapper {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUaAlarmEventMapper.class);

  private final NodeId selectedEventTypeId;
  private final List<OpcUaAlarmField> selectedFields;
  private final OpcUaAlarmValueNormalizer valueNormalizer;

  static OpcUaAlarmEventMapper create(OpcUaClient client, OpcUaAlarmAdapterConfig config) {
    var selectedEventTypeId = determineSelectedEventTypeId(config);

    try {
      var fieldProvider = new OpcUaEventFieldProvider(client);
      return new OpcUaAlarmEventMapper(
          selectedEventTypeId,
          fieldProvider.buildSelectedFields(
              selectedEventTypeId.toParseableString(),
              config.getSelectedAdditionalFieldNames())
      );
    } catch (UaException e) {
      LOG.warn(
          "Falling back to BaseEventType fields because reading OPC UA event field declarations failed for selectedEventTypeId={}",
          selectedEventTypeId,
          e
      );
      return new OpcUaAlarmEventMapper(selectedEventTypeId, OpcUaAlarmField.baseEventFieldsOnly());
    }
  }

  OpcUaAlarmEventMapper(NodeId selectedEventTypeId, List<OpcUaAlarmField> selectedFields) {
    this.selectedEventTypeId = selectedEventTypeId;
    this.selectedFields = selectedFields;
    this.valueNormalizer = new OpcUaAlarmValueNormalizer();
  }

  EventFilter makeEventFilter(EncodingContext encodingContext) {
    var filterBuilder = new EventFilterBuilder();
    selectedFields.forEach(field -> filterBuilder.select(toOperand(field)));
    filterBuilder.where(makeOfTypeWhereClause(encodingContext));
    return filterBuilder.build();
  }

  Map<String, Object> toEvent(Variant[] eventValues) {
    var event = new LinkedHashMap<String, Object>();

    for (int i = 0; i < selectedFields.size(); i++) {
      var field = selectedFields.get(i);
      var variantValue = i < eventValues.length ? eventValues[i].getValue() : null;
      event.put(field.outputField(), normalizeFieldValue(field, variantValue));
    }

    return event;
  }

  private Object normalizeFieldValue(OpcUaAlarmField field, Object value) {
    var normalizedValue = valueNormalizer.normalize(value);

    if (field.extractionMode() == OpcUaAlarmField.ExtractionMode.TWO_STATE_LOCALIZED_TEXT) {
      return toTwoStateBoolean(normalizedValue);
    }

    return normalizedValue;
  }

  private Object toTwoStateBoolean(Object normalizedValue) {
    if (normalizedValue instanceof Boolean) {
      return normalizedValue;
    }

    if (normalizedValue instanceof String stringValue) {
      return switch (stringValue.trim().toLowerCase()) {
        case "true", "on", "active", "acknowledged", "confirmed", "enabled" -> true;
        case "false", "off", "inactive", "unacknowledged", "unconfirmed", "disabled" -> false;
        default -> normalizedValue;
      };
    }

    return normalizedValue;
  }

  private SimpleAttributeOperand toOperand(OpcUaAlarmField field) {
    return new SimpleAttributeOperand(
        field.typeDefinitionId(),
        field.eventBrowsePath(),
        AttributeId.Value.uid(),
        null
    );
  }

  private ContentFilter makeOfTypeWhereClause(EncodingContext encodingContext) {
    var ofTypeOperand = ExtensionObject.encode(
        encodingContext,
        new LiteralOperand(new Variant(selectedEventTypeId))
    );

    return new ContentFilter(
        new ContentFilterElement[] {
            new ContentFilterElement(
                FilterOperator.OfType,
                new ExtensionObject[] {ofTypeOperand}
            )
        }
    );
  }

  private static NodeId determineSelectedEventTypeId(OpcUaAlarmAdapterConfig config) {
    if (config.getEventTypeNodeId() == null || config.getEventTypeNodeId().isBlank()) {
      return NodeIds.BaseEventType;
    }

    return NodeId.parse(config.getEventTypeNodeId());
  }
}
