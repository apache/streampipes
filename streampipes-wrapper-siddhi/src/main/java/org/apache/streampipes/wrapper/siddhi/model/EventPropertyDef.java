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
package org.apache.streampipes.wrapper.siddhi.model;

public class EventPropertyDef {

  public static final String WHITESPACE_REPLACEMENT = "__w__";

  private String selectorPrefix;
  private final String fieldName;
  private final String fieldType;

  public static String toOriginalFieldName(String sanitizedFieldName) {
    return sanitizedFieldName.replaceAll(WHITESPACE_REPLACEMENT, " ");
  }

  public EventPropertyDef(String fieldName,
                          String fieldType) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
  }

  public EventPropertyDef(String selectorPrefix, String fieldName, String fieldType) {
    this.selectorPrefix = selectorPrefix;
    this.fieldName = fieldName;
    this.fieldType = fieldType;
  }

  public String getSelectorPrefix() {
    return selectorPrefix;
  }

  public String getFieldName() {
    return fieldName.replaceAll(" ", WHITESPACE_REPLACEMENT);
  }

  public String getFieldType() {
    return fieldType;
  }
}
