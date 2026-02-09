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
package org.apache.streampipes.connect.shared.preprocessing.utils;

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting and finding properties in event schemas.
 * Handles nested property lookups and provides detailed error logging.
 */
public class ConversionUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ConversionUtils.class);
  private static final String PROPERTY_DELIMITER = ".";

  /**
   * Finds a property in the event schema by its property path.
   * Supports nested property paths like "address.city" where address is a nested property.
   *
   * @param schema The event schema to search in
   * @param propertyPath The property path to find (e.g., "user.address.city")
   * @return The EventProperty found at the given path
   * @throws IllegalArgumentException if the property path is invalid or property not found
   */
  public static EventProperty findProperty(EventSchema schema, String propertyPath) {
    if (propertyPath == null || propertyPath.isEmpty()) {
      throw new IllegalArgumentException("Property path cannot be null or empty");
    }

    if (schema == null || schema.getEventProperties() == null) {
      throw new IllegalArgumentException("Event schema is null or has no properties");
    }

    try {
      String[] pathElements = propertyPath.split("\\" + PROPERTY_DELIMITER);

      if (pathElements.length == 0) {
        throw new IllegalArgumentException("Invalid property path: " + propertyPath);
      }

      // Find the root property
      EventProperty currentProperty = null;
      for (EventProperty prop : schema.getEventProperties()) {
        if (prop.getRuntimeName().equals(pathElements[0])) {
          currentProperty = prop;
          break;
        }
      }

      if (currentProperty == null) {
        List<String> availablePaths = getAvailablePropertyPaths(schema);
        LOG.error("Could not find root property '{}' in schema. Available properties: {}",
            pathElements[0], availablePaths);
        throw new IllegalArgumentException("Could not find property: " + pathElements[0]);
      }

      // Navigate through nested properties
      for (int i = 1; i < pathElements.length; i++) {
        if (!(currentProperty instanceof EventPropertyNested)) {
          String currentPath = String.join(PROPERTY_DELIMITER, pathElements[i]);
          List<String> availablePaths = getAvailablePropertyPaths(schema);
          LOG.error("Property '{}' is not nested, cannot access '{}'",
              pathElements[i - 1], currentPath);
          throw new IllegalArgumentException(
              "Property '" + pathElements[i - 1] + "' is not nested"
          );
        }

        EventPropertyNested nestedProperty = (EventPropertyNested) currentProperty;
        EventProperty nextProperty = null;

        for (EventProperty prop : nestedProperty.getEventProperties()) {
          if (prop.getRuntimeName().equals(pathElements[i])) {
            nextProperty = prop;
            break;
          }
        }

        if (nextProperty == null) {
          List<String> availablePaths = getAvailableNestedPropertyPaths(nestedProperty, pathElements[i - 1]);
          LOG.error("Could not find nested property '{}' under '{}'. Available properties: {}",
              pathElements[i], pathElements[i - 1], availablePaths);
          throw new IllegalArgumentException("Could not find property: " + pathElements[i]);
        }

        currentProperty = nextProperty;
      }

      return currentProperty;

    } catch (Exception e) {
      List<String> availablePaths = getAvailablePropertyPaths(schema);
      LOG.error("Could not find property '{}' in schema. Available properties: {}",
          propertyPath, availablePaths, e);
      if (e instanceof IllegalArgumentException) {
        throw e;
      }
      throw new IllegalArgumentException("Could not find property: " + propertyPath, e);
    }
  }

  /**
   * Checks if a property exists in the event schema.
   *
   * @param schema The event schema to search in
   * @param propertyPath The property path to check
   * @return true if the property exists, false otherwise
   */
  public static boolean propertyExists(EventSchema schema, String propertyPath) {
    try {
      findProperty(schema, propertyPath);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Gets all available property paths in the schema for debugging purposes.
   *
   * @param schema The event schema
   * @return A list of all available property paths
   */
  private static List<String> getAvailablePropertyPaths(EventSchema schema) {
    List<String> paths = new ArrayList<>();
    if (schema != null && schema.getEventProperties() != null) {
      for (EventProperty prop : schema.getEventProperties()) {
        paths.add(prop.getRuntimeName());
        if (prop instanceof EventPropertyNested) {
          paths.addAll(getNestedPropertyPaths((EventPropertyNested) prop, prop.getRuntimeName()));
        }
      }
    }
    return paths;
  }

  /**
   * Gets all available property paths under a nested property.
   *
   * @param nestedProperty The nested property
   * @param parentPath The path to the parent property
   * @return A list of all available nested property paths
   */
  private static List<String> getAvailableNestedPropertyPaths(
      EventPropertyNested nestedProperty,
      String parentPath
  ) {
    List<String> paths = new ArrayList<>();
    if (nestedProperty != null && nestedProperty.getEventProperties() != null) {
      for (EventProperty prop : nestedProperty.getEventProperties()) {
        String fullPath = parentPath + PROPERTY_DELIMITER + prop.getRuntimeName();
        paths.add(fullPath);
        if (prop instanceof EventPropertyNested) {
          paths.addAll(getNestedPropertyPaths((EventPropertyNested) prop, fullPath));
        }
      }
    }
    return paths;
  }

  /**
   * Recursively gets all nested property paths.
   *
   * @param nestedProperty The nested property to traverse
   * @param currentPath The current path prefix
   * @return A list of all nested property paths
   */
  private static List<String> getNestedPropertyPaths(
      EventPropertyNested nestedProperty,
      String currentPath
  ) {
    List<String> paths = new ArrayList<>();
    if (nestedProperty != null && nestedProperty.getEventProperties() != null) {
      for (EventProperty prop : nestedProperty.getEventProperties()) {
        String fullPath = currentPath + PROPERTY_DELIMITER + prop.getRuntimeName();
        paths.add(fullPath);
        if (prop instanceof EventPropertyNested) {
          paths.addAll(getNestedPropertyPaths((EventPropertyNested) prop, fullPath));
        }
      }
    }
    return paths;
  }
}
