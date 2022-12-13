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

import java.util.Objects;

public class AssetLink {

  private String resourceId;
  private String linkType;
  private String linkLabel;
  private String queryHint;
  private boolean editingDisabled;

  public AssetLink() {
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getLinkType() {
    return linkType;
  }

  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  public String getLinkLabel() {
    return linkLabel;
  }

  public void setLinkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
  }

  public boolean isEditingDisabled() {
    return editingDisabled;
  }

  public void setEditingDisabled(boolean editingDisabled) {
    this.editingDisabled = editingDisabled;
  }

  public String getQueryHint() {
    return queryHint;
  }

  public void setQueryHint(String queryHint) {
    this.queryHint = queryHint;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssetLink assetLink = (AssetLink) o;
    return resourceId.equals(assetLink.resourceId) && queryHint.equals(assetLink.queryHint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceId, queryHint);
  }
}
