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

package org.apache.streampipes.model.base;


import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * named pipeline element, can be accessed via the URI provided in @RdfId
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
public abstract class NamedStreamPipesEntity implements Serializable {

  private static final long serialVersionUID = -98951691820519795L;

  protected @SerializedName("_id") String elementId;

  @JsonProperty("_rev")
  protected @SerializedName("_rev") String rev;

  protected String dom;
  protected List<String> connectedTo;
  private String name;
  private String description;
  private String iconUrl;
  private String appId;
  private boolean includesAssets;
  private boolean includesLocales;
  private List<String> includedAssets;
  private List<String> includedLocales;
  private boolean internallyManaged;


  public NamedStreamPipesEntity() {
    super();
    this.includedAssets = new ArrayList<>();
    this.includedLocales = new ArrayList<>();
  }

  public NamedStreamPipesEntity(String elementId) {
    this();
    this.elementId = elementId;
  }

  public NamedStreamPipesEntity(String elementId, String name, String description, String iconUrl) {
    this(elementId, name, description);
    this.iconUrl = iconUrl;
  }

  public NamedStreamPipesEntity(String elementId, String name, String description) {
    super();
    this.elementId = elementId;
    this.name = name;
    this.description = description;
    this.includedAssets = new ArrayList<>();
    this.includedLocales = new ArrayList<>();
  }

  public NamedStreamPipesEntity(NamedStreamPipesEntity other) {
    this.elementId = other.getElementId();
    this.rev = other.getRev();
    this.description = other.getDescription();
    this.name = other.getName();
    this.iconUrl = other.getIconUrl();
    this.elementId = other.getElementId();
    this.dom = other.getDom();
    this.internallyManaged = other.isInternallyManaged();
    this.connectedTo = other.getConnectedTo();
    this.appId = other.getAppId();
    this.includesAssets = other.isIncludesAssets();
    this.includesLocales = other.isIncludesLocales();
    if (other.getIncludedAssets() != null) {
      this.includedAssets = other.getIncludedAssets();
    }
    if (other.getIncludedLocales() != null) {
      this.includedLocales = other.getIncludedLocales();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  @Deprecated
  public String getUri() {
    return elementId;
  }

  @Deprecated
  public void setUri(String uri) {
    this.elementId = uri;
  }

  public String getDom() {
    return dom;
  }

  public void setDom(String dom) {
    this.dom = dom;
  }

  public List<String> getConnectedTo() {
    return connectedTo;
  }

  public void setConnectedTo(List<String> connectedTo) {
    this.connectedTo = connectedTo;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public boolean isIncludesAssets() {
    return includesAssets;
  }

  public void setIncludesAssets(boolean includesAssets) {
    this.includesAssets = includesAssets;
  }

  public List<String> getIncludedAssets() {
    return includedAssets;
  }

  public void setIncludedAssets(List<String> includedAssets) {
    this.includedAssets = includedAssets;
  }

  public boolean isIncludesLocales() {
    return includesLocales;
  }

  public void setIncludesLocales(boolean includesLocales) {
    this.includesLocales = includesLocales;
  }

  public List<String> getIncludedLocales() {
    return includedLocales;
  }

  public void setIncludedLocales(List<String> includedLocales) {
    this.includedLocales = includedLocales;
  }

  public boolean isInternallyManaged() {
    return internallyManaged;
  }

  public void setInternallyManaged(boolean internallyManaged) {
    this.internallyManaged = internallyManaged;
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamedStreamPipesEntity that = (NamedStreamPipesEntity) o;
    return Objects.equals(elementId, that.elementId);
  }
}
