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

import org.apache.streampipes.commons.constants.GenericDocTypes;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.gson.annotations.SerializedName;

@TsModel
public class SpAssetModel extends SpAsset implements Storable {

  public static final String APP_DOC_TYPE = GenericDocTypes.DOC_ASSET_MANGEMENT;

  private final String appDocType = APP_DOC_TYPE;

  @JsonAlias("_id")
  private @SerializedName("_id") String elementId;

  @JsonAlias("_rev")
  @SerializedName("_rev")
  private String rev;

  private boolean removable;

  public SpAssetModel() {
    super();
  }

  public boolean isRemovable() {
    return removable;
  }

  public void setRemovable(boolean removable) {
    this.removable = removable;
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return elementId;
  }

  @Override
  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public String getAppDocType() {
    return appDocType;
  }
}
