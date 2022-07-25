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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.apache.streampipes.commons.constants.GenericDocTypes;

public class AssetLinkType {

  public final String appDocType = GenericDocTypes.DOC_ASSET_LINK_TYPE;

  @JsonProperty("_id")
  private @SerializedName("_id") String id;

  private String linkType;
  private String linkLabel;
  private String linkColor;
  private String linkIcon;
  private String linkQueryHint;

  public AssetLinkType(String linkType, String linkLabel, String linkColor, String linkIcon, String linkQueryHint) {
    this.linkType = linkType;
    this.linkLabel = linkLabel;
    this.linkColor = linkColor;
    this.linkIcon = linkIcon;
    this.linkQueryHint = linkQueryHint;
  }

  public AssetLinkType() {
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

  public String getLinkColor() {
    return linkColor;
  }

  public void setLinkColor(String linkColor) {
    this.linkColor = linkColor;
  }

  public String getLinkIcon() {
    return linkIcon;
  }

  public void setLinkIcon(String linkIcon) {
    this.linkIcon = linkIcon;
  }

  public String getLinkQueryHint() {
    return linkQueryHint;
  }

  public void setLinkQueryHint(String linkQueryHint) {
    this.linkQueryHint = linkQueryHint;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppDocType() {
    return appDocType;
  }
}
