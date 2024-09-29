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
package org.apache.streampipes.model.assets;

/**
 * Builder class for creating instances of {@link AssetLink}.
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * AssetLink assetLink = AssetLinkBuilder.create().withResourceId("123").withLinkType("type").withLinkLabel("label")
 *         .withQueryHint("hint").withEditingDisabled(false).build();
 * }
 * </pre>
 */
public class AssetLinkBuilder {
  private final AssetLink assetLink;

  /**
   * Constructs a new AssetLinkBuilder.
   */
  private AssetLinkBuilder() {
    this.assetLink = new AssetLink();
  }

  /**
   * Static method to create a new instance of AssetLinkBuilder.
   *
   * @return A new instance of AssetLinkBuilder.
   */
  public static AssetLinkBuilder create() {
    return new AssetLinkBuilder();
  }

  /**
   * Sets the resource ID for the AssetLink being built.
   *
   * @param resourceId
   *          The resource ID to set.
   * @return The AssetLinkBuilder instance for method chaining.
   */
  public AssetLinkBuilder withResourceId(String resourceId) {
    this.assetLink.setResourceId(resourceId);
    return this;
  }

  /**
   * Sets the link type for the AssetLink being built.
   *
   * @param linkType
   *          The link type to set.
   * @return The AssetLinkBuilder instance for method chaining.
   */
  public AssetLinkBuilder withLinkType(String linkType) {
    this.assetLink.setLinkType(linkType);
    return this;
  }

  /**
   * Sets the link label for the AssetLink being built.
   *
   * @param linkLabel
   *          The link label to set.
   * @return The AssetLinkBuilder instance for method chaining.
   */
  public AssetLinkBuilder withLinkLabel(String linkLabel) {
    this.assetLink.setLinkLabel(linkLabel);
    return this;
  }

  /**
   * Sets the query hint for the AssetLink being built.
   *
   * @param queryHint
   *          The query hint to set.
   * @return The AssetLinkBuilder instance for method chaining.
   */
  public AssetLinkBuilder withQueryHint(String queryHint) {
    this.assetLink.setQueryHint(queryHint);
    return this;
  }

  /**
   * Sets whether editing is disabled for the AssetLink being built.
   *
   * @param editingDisabled
   *          Whether editing is disabled.
   * @return The AssetLinkBuilder instance for method chaining.
   */
  public AssetLinkBuilder withEditingDisabled(boolean editingDisabled) {
    this.assetLink.setEditingDisabled(editingDisabled);
    return this;
  }

  /**
   * Builds and returns the final instance of AssetLink.
   *
   * @return The constructed AssetLink instance.
   */
  public AssetLink build() {
    return this.assetLink;
  }
}