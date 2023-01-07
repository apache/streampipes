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

package org.apache.streampipes.rest.extensions.html.model;

public class Description {

  private String name;
  private String description;
  private String elementId;
  private String descriptionUrl;
  private String type;
  private String appId;

  private boolean editable;
  private boolean includesIcon;
  private boolean includesDocs;

  public Description(String name, String description, String descriptionUrl) {
    this.name = name;
    this.description = description;
    this.descriptionUrl = descriptionUrl;
  }

  public Description() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescriptionUrl() {
    return descriptionUrl;
  }

  public void setDescriptionUrl(String descriptionUrl) {
    this.descriptionUrl = descriptionUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public boolean isIncludesIcon() {
    return includesIcon;
  }

  public void setIncludesIcon(boolean includesIcon) {
    this.includesIcon = includesIcon;
  }

  public boolean isIncludesDocs() {
    return includesDocs;
  }

  public void setIncludesDocs(boolean includesDocs) {
    this.includesDocs = includesDocs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Description that = (Description) o;

    if (!name.equals(that.name)) {
      return false;
    }
    if (!description.equals(that.description)) {
      return false;
    }
    return descriptionUrl.equals(that.descriptionUrl);

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + descriptionUrl.hashCode();
    return result;
  }
}
