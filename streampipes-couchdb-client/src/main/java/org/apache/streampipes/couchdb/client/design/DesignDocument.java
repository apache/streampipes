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

package org.apache.streampipes.couchdb.client.design;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable design document with views.
 *
 * <p>Fields other than {@code language} and {@code views} (filters, validation functions, options) are carried in
 * {@link #otherFields()} so that an update does not drop them.</p>
 */
public final class DesignDocument {

  public static final String PREFIX = "_design/";
  public static final String DEFAULT_LANGUAGE = "javascript";

  private final String id;
  private final String rev;
  private final String language;
  private final Map<String, ViewDefinition> views;
  private final Map<String, JsonNode> otherFields;

  public DesignDocument(String id,
                        String rev,
                        String language,
                        Map<String, ViewDefinition> views,
                        Map<String, JsonNode> otherFields) {
    this.id = toId(id);
    this.rev = rev;
    this.language = language;
    this.views = Collections.unmodifiableMap(new LinkedHashMap<>(views));
    this.otherFields = Collections.unmodifiableMap(new LinkedHashMap<>(otherFields));
  }

  /**
   * Empty design document.
   *
   * @param name name with or without the {@code _design/} prefix
   * @return the design document
   */
  public static DesignDocument named(String name) {
    return new DesignDocument(name, null, DEFAULT_LANGUAGE, Map.of(), Map.of());
  }

  public static String toId(String name) {
    Objects.requireNonNull(name, "name");
    return name.startsWith(PREFIX) ? name : PREFIX + name;
  }

  public DesignDocument withView(String name, String map) {
    return withView(name, ViewDefinition.map(map));
  }

  public DesignDocument withView(String name, ViewDefinition view) {
    var newViews = new LinkedHashMap<>(views);
    newViews.put(name, view);
    return new DesignDocument(id, rev, language, newViews, otherFields);
  }

  public DesignDocument withViews(Map<String, ViewDefinition> views) {
    var newViews = new LinkedHashMap<>(this.views);
    newViews.putAll(views);
    return new DesignDocument(id, rev, language, newViews, otherFields);
  }

  public DesignDocument withLanguage(String language) {
    return new DesignDocument(id, rev, language, views, otherFields);
  }

  public DesignDocument withRev(String rev) {
    return new DesignDocument(id, rev, language, views, otherFields);
  }

  public DesignDocument withOtherFields(Map<String, JsonNode> otherFields) {
    return new DesignDocument(id, rev, language, views, otherFields);
  }

  public String id() {
    return id;
  }

  public String name() {
    return id.substring(PREFIX.length());
  }

  public String rev() {
    return rev;
  }

  public String language() {
    return language;
  }

  public Map<String, ViewDefinition> views() {
    return views;
  }

  public Map<String, JsonNode> otherFields() {
    return otherFields;
  }

  /**
   * Whether language and views equal those of another design document; revision and other fields are ignored.
   *
   * @param other design document to compare with
   * @return true if both define the same views
   */
  public boolean sameDefinitionAs(DesignDocument other) {
    return Objects.equals(effectiveLanguage(), other.effectiveLanguage()) && views.equals(other.views);
  }

  private String effectiveLanguage() {
    return language == null ? DEFAULT_LANGUAGE : language;
  }
}
