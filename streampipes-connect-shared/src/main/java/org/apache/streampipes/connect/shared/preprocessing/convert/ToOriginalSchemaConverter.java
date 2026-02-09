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
package org.apache.streampipes.connect.shared.preprocessing.convert;

import org.apache.streampipes.connect.shared.preprocessing.utils.ConversionUtils;
import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor implementation that converts event schemas back to their original form
 * by validating transformation rules before applying them.
 * 
 * This converter is particularly important for handling MoveRuleDescription
 * where properties might have been moved and need validation before schema conversion.
 */
public class ToOriginalSchemaConverter implements ITransformationRuleVisitor {

  private static final Logger LOG = LoggerFactory.getLogger(ToOriginalSchemaConverter.class);
  private static final String PROPERTY_DELIMITER = ".";

  private final EventSchema eventSchema;

  public ToOriginalSchemaConverter(EventSchema eventSchema) {
    this.eventSchema = eventSchema;
  }

  /**
   * Visits and processes a MoveRuleDescription with validation.
   * Validates that the property exists before attempting to move it.
   *
   * @param rule The move rule to process
   */
  @Override
  public void visit(MoveRuleDescription rule) {
    if (rule == null) {
      LOG.warn("MoveRuleDescription is null, skipping");
      return;
    }

    try {
      String oldPropertyPath = rule.getOldRuntimeKey();
      String newPropertyPath = rule.getNewRuntimeKey();

      // Validate that both paths are provided
      if (oldPropertyPath == null || oldPropertyPath.isEmpty()) {
        LOG.warn("MoveRuleDescription has empty old runtime key, skipping");
        return;
      }

      if (newPropertyPath == null || newPropertyPath.isEmpty()) {
        LOG.warn("MoveRuleDescription has empty new runtime key, skipping");
        return;
      }

      // Check if the property exists before attempting transformation
      if (!propertyExists(oldPropertyPath)) {
        LOG.warn("Skipping move rule - source property not found: '{}'. "
            + "Source property may have already been moved or deleted.", oldPropertyPath);
        return;
      }

      // Check if target path already exists (to avoid conflicts)
      if (propertyExists(newPropertyPath)) {
        LOG.warn("Skipping move rule - target property already exists: '{}'. "
            + "A property may have already been moved to this location.", newPropertyPath);
        return;
      }

      // Apply the move transformation
      applyMoveRule(oldPropertyPath, newPropertyPath);

    } catch (IllegalArgumentException e) {
      LOG.error("Failed to apply move rule from '{}' to '{}': {}",
          rule.getOldRuntimeKey(), rule.getNewRuntimeKey(), e.getMessage());
      LOG.debug("Stack trace:", e);
      // Don't fail completely, just skip this rule to allow other rules to be processed
    } catch (Exception e) {
      LOG.error("Unexpected error while applying move rule from '{}' to '{}'",
          rule.getOldRuntimeKey(), rule.getNewRuntimeKey(), e);
      // Don't fail completely, just skip this rule to allow other rules to be processed
    }
  }

  @Override
  public void visit(EventRateTransformationRuleDescription rule) {
    // Stream transformation rules are not handled in schema conversion
    LOG.debug("EventRate transformation rule not applicable to schema conversion");
  }

  @Override
  public void visit(RemoveDuplicatesTransformationRuleDescription rule) {
    // Stream transformation rules are not handled in schema conversion
    LOG.debug("RemoveDuplicates transformation rule not applicable to schema conversion");
  }

  @Override
  public void visit(ChangeDatatypeTransformationRuleDescription rule) {
    // Value transformation rules might be handled in future enhancements
    LOG.debug("ChangeDatatype transformation rule processing not yet implemented");
  }

  @Override
  public void visit(UnitTransformRuleDescription rule) {
    // Value transformation rules might be handled in future enhancements
    LOG.debug("UnitTransform transformation rule processing not yet implemented");
  }

  /**
   * Applies the move rule to restructure the event schema.
   *
   * @param oldPath The current property path
   * @param newPath The target property path
   */
  private void applyMoveRule(String oldPath, String newPath) {
    try {
      // Find and remove the property from its old location
      EventProperty propertyToMove = ConversionUtils.findProperty(eventSchema, oldPath);
      removePropertyFromPath(eventSchema, oldPath);

      // Move the property to its new location
      addPropertyToPath(eventSchema, newPath, propertyToMove);

      LOG.info("Successfully moved property from '{}' to '{}'", oldPath, newPath);

    } catch (IllegalArgumentException e) {
      LOG.error("Failed to apply move rule - property handling error: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * Checks if a property exists at the given path.
   *
   * @param propertyPath The property path to check
   * @return true if the property exists, false otherwise
   */
  private boolean propertyExists(String propertyPath) {
    return ConversionUtils.propertyExists(eventSchema, propertyPath);
  }

  /**
   * Removes a property from the event schema at the specified path.
   *
   * @param schema The event schema
   * @param propertyPath The path to the property to remove
   */
  private void removePropertyFromPath(EventSchema schema, String propertyPath) {
    String[] pathElements = propertyPath.split("\\" + PROPERTY_DELIMITER);

    if (pathElements.length == 1) {
      // Top-level property
      schema.getEventProperties().removeIf(p -> p.getRuntimeName().equals(pathElements[0]));
    } else {
      // Nested property
      navigateAndRemoveNestedProperty(schema.getEventProperties(), pathElements, 0);
    }
  }

  /**
   * Recursively navigates to a nested property and removes it.
   *
   * @param properties The list of properties to search in
   * @param pathElements The path elements to navigate
   * @param depth The current depth in the path
   */
  private void navigateAndRemoveNestedProperty(
      java.util.List<EventProperty> properties,
      String[] pathElements,
      int depth
  ) {
    if (depth >= pathElements.length - 1) {
      // We've reached the parent of the property to remove
      EventProperty parent = null;
      for (EventProperty prop : properties) {
        if (prop.getRuntimeName().equals(pathElements[depth])) {
          parent = prop;
          break;
        }
      }

      if (parent instanceof EventPropertyNested) {
        EventPropertyNested nestedParent = (EventPropertyNested) parent;
        nestedParent.getEventProperties().removeIf(
            p -> p.getRuntimeName().equals(pathElements[pathElements.length - 1])
        );
      }
    } else {
      // Continue navigating
      for (EventProperty prop : properties) {
        if (prop.getRuntimeName().equals(pathElements[depth]) && prop instanceof EventPropertyNested) {
          EventPropertyNested nestedProp = (EventPropertyNested) prop;
          navigateAndRemoveNestedProperty(nestedProp.getEventProperties(), pathElements, depth + 1);
        }
      }
    }
  }

  /**
   * Adds a property to the event schema at the specified path.
   *
   * @param schema The event schema
   * @param propertyPath The path where the property should be added
   * @param propertyToAdd The property to add
   */
  private void addPropertyToPath(EventSchema schema, String propertyPath, EventProperty propertyToAdd) {
    String[] pathElements = propertyPath.split("\\" + PROPERTY_DELIMITER);

    if (pathElements.length == 1) {
      // Top-level property
      propertyToAdd.setRuntimeName(pathElements[0]);
      schema.getEventProperties().add(propertyToAdd);
    } else {
      // Nested property - need to create the nested structure if it doesn't exist
      navigateOrCreateAndAddNestedProperty(schema.getEventProperties(), pathElements, 0, propertyToAdd);
    }
  }

  /**
   * Recursively navigates to or creates nested properties and adds the target property.
   *
   * @param properties The list of properties to search in
   * @param pathElements The path elements to navigate
   * @param depth The current depth in the path
   * @param propertyToAdd The property to add
   */
  private void navigateOrCreateAndAddNestedProperty(
      java.util.List<EventProperty> properties,
      String[] pathElements,
      int depth,
      EventProperty propertyToAdd
  ) {
    if (depth >= pathElements.length - 1) {
      // We've reached the parent of where the property should be added
      EventProperty parent = null;
      for (EventProperty prop : properties) {
        if (prop.getRuntimeName().equals(pathElements[depth])) {
          parent = prop;
          break;
        }
      }

      if (parent == null) {
        // Create the parent if it doesn't exist
        parent = new EventPropertyNested(pathElements[depth]);
        properties.add(parent);
      }

      if (parent instanceof EventPropertyNested) {
        EventPropertyNested nestedParent = (EventPropertyNested) parent;
        propertyToAdd.setRuntimeName(pathElements[pathElements.length - 1]);
        nestedParent.getEventProperties().add(propertyToAdd);
      }
    } else {
      // Continue navigating or creating
      EventProperty nextProp = null;
      for (EventProperty prop : properties) {
        if (prop.getRuntimeName().equals(pathElements[depth])) {
          nextProp = prop;
          break;
        }
      }

      if (nextProp == null) {
        // Create nested property if it doesn't exist
        nextProp = new EventPropertyNested(pathElements[depth]);
        properties.add(nextProp);
      }

      if (nextProp instanceof EventPropertyNested) {
        EventPropertyNested nestedProp = (EventPropertyNested) nextProp;
        navigateOrCreateAndAddNestedProperty(nestedProp.getEventProperties(), pathElements, depth + 1, propertyToAdd);
      }
    }
  }
}
