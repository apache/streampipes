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

public class AssetType {

  private String assetIcon;
  private String assetIconColor;
  private String assetTypeCategory;
  private String assetTypeLabel;

  public AssetType() {
  }

  public String getAssetIcon() {
    return assetIcon;
  }

  public void setAssetIcon(String assetIcon) {
    this.assetIcon = assetIcon;
  }

  public String getAssetIconColor() {
    return assetIconColor;
  }

  public void setAssetIconColor(String assetIconColor) {
    this.assetIconColor = assetIconColor;
  }

  public String getAssetTypeCategory() {
    return assetTypeCategory;
  }

  public void setAssetTypeCategory(String assetTypeCategory) {
    this.assetTypeCategory = assetTypeCategory;
  }

  public String getAssetTypeLabel() {
    return assetTypeLabel;
  }

  public void setAssetTypeLabel(String assetTypeLabel) {
    this.assetTypeLabel = assetTypeLabel;
  }
}
