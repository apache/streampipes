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

import java.util.ArrayList;
import java.util.List;

public class SpAsset {

  private String assetId;
  private String assetName;
  private String assetDescription;

  private AssetType assetType;
  private List<AssetLink> assetLinks;

  private List<SpAsset> assets;

  public SpAsset() {
    this.assets = new ArrayList<>();
    this.assetLinks = new ArrayList<>();
  }

  public String getAssetId() {
    return assetId;
  }

  public void setAssetId(String assetId) {
    this.assetId = assetId;
  }

  public String getAssetName() {
    return assetName;
  }

  public void setAssetName(String assetName) {
    this.assetName = assetName;
  }

  public String getAssetDescription() {
    return assetDescription;
  }

  public void setAssetDescription(String assetDescription) {
    this.assetDescription = assetDescription;
  }

  public AssetType getAssetType() {
    return assetType;
  }

  public void setAssetType(AssetType assetType) {
    this.assetType = assetType;
  }

  public List<AssetLink> getAssetLinks() {
    return assetLinks;
  }

  public void setAssetLinks(List<AssetLink> assetLinks) {
    this.assetLinks = assetLinks;
  }

  public List<SpAsset> getAssets() {
    return assets;
  }

  public void setAssets(List<SpAsset> assets) {
    this.assets = assets;
  }
}
