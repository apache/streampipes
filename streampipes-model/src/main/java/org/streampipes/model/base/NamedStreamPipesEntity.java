/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.base;


import org.streampipes.empire.annotations.RdfId;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.model.ApplicationLink;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * named pipeline element, can be accessed via the URI provided in @RdfId
 */
public abstract class NamedStreamPipesEntity extends AbstractStreamPipesEntity {

  private static final long serialVersionUID = -98951691820519795L;

  @RdfProperty(RDFS.LABEL)
  private String name;

  @RdfProperty(RDFS.DESCRIPTION)
  private String description;

  @RdfProperty(StreamPipes.ICON_URL)
  private String iconUrl;

  @RdfProperty(StreamPipes.HAS_URI)
  @RdfId
  protected String elementId;

  @RdfProperty(StreamPipes.HAS_APP_ID)
  private String appId;

  @RdfProperty(StreamPipes.INCLUDES_ASSETS)
  private boolean includesAssets;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_APPLICATION_LINK)
  private List<ApplicationLink> applicationLinks;

  protected String DOM;
  protected List<String> connectedTo;


  public NamedStreamPipesEntity() {
    super();
    this.applicationLinks = new ArrayList<>();
  }

  public NamedStreamPipesEntity(String elementId) {
    super();
    this.elementId = elementId;
    this.applicationLinks = new ArrayList<>();
  }

  public NamedStreamPipesEntity(String elementId, String name, String description, String iconUrl) {
    this(elementId, name, description);
    this.iconUrl = iconUrl;
    this.applicationLinks = new ArrayList<>();
  }

  public NamedStreamPipesEntity(String elementId, String name, String description) {
    super();
    this.elementId = elementId;
    this.name = name;
    this.description = description;
    this.applicationLinks = new ArrayList<>();
  }

  public NamedStreamPipesEntity(NamedStreamPipesEntity other) {
    super();
    this.description = other.getDescription();
    this.name = other.getName();
    this.iconUrl = other.getIconUrl();
    this.elementId = other.getElementId();
    this.DOM = other.getDOM();
    this.connectedTo = other.getConnectedTo();
    if (other.getApplicationLinks() != null) {
      this.applicationLinks = new Cloner().al(other.getApplicationLinks());
    }
    this.appId = other.getAppId();
    this.includesAssets = other.isIncludesAssets();
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

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public void setDOM(String DOM) {
    this.DOM = DOM;
  }

  public String getDOM() {
    return DOM;
  }

  public List<String> getConnectedTo() {
    return connectedTo;
  }

  public void setConnectedTo(List<String> connectedTo) {
    this.connectedTo = connectedTo;
  }

  public List<ApplicationLink> getApplicationLinks() {
    return applicationLinks;
  }

  public void setApplicationLinks(List<ApplicationLink> applicationLinks) {
    this.applicationLinks = applicationLinks;
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

  @Deprecated
  public void changeElementId(String elementId) {
    this.elementId = elementId;
  }
}
