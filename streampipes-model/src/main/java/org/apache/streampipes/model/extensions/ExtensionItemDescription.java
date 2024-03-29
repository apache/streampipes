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

package org.apache.streampipes.model.extensions;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class ExtensionItemDescription {

  private String name;
  private String description;
  private String elementId;
  private SpServiceTagPrefix serviceTagPrefix;
  private String appId;

  private boolean includesIcon;
  private boolean includesDocs;

  private boolean installed;
  private boolean editable;
  private boolean available;

  public ExtensionItemDescription() {

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

  public boolean isInstalled() {
    return installed;
  }

  public void setInstalled(boolean installed) {
    this.installed = installed;
  }

  public SpServiceTagPrefix getServiceTagPrefix() {
    return serviceTagPrefix;
  }

  public void setServiceTagPrefix(SpServiceTagPrefix serviceTagPrefix) {
    this.serviceTagPrefix = serviceTagPrefix;
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

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
}
