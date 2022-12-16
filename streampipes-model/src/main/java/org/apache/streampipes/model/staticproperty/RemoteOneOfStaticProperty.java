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

package org.apache.streampipes.model.staticproperty;


import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class RemoteOneOfStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 3483290363677184344L;

  private List<Option> options;

  private String remoteUrl;

  private String valueFieldName;

  private String labelFieldName;

  private String descriptionFieldName;

  public RemoteOneOfStaticProperty() {
    super(StaticPropertyType.OneOfStaticProperty);
    this.options = new ArrayList<>();
  }

  public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl,
                                   String valueFieldName, String labelFieldName, String descriptionFieldName,
                                   boolean valueRequired) {
    super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
    this.remoteUrl = remoteUrl;
    this.valueFieldName = valueFieldName;
    this.labelFieldName = labelFieldName;
    this.descriptionFieldName = descriptionFieldName;
    this.valueRequired = valueRequired;
    this.options = new ArrayList<>();
  }


  public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl,
                                   String valueFieldName, String labelFieldName, String descriptionFieldName) {
    super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
    this.remoteUrl = remoteUrl;
    this.valueFieldName = valueFieldName;
    this.labelFieldName = labelFieldName;
    this.descriptionFieldName = descriptionFieldName;
    this.options = new ArrayList<>();
  }

  public RemoteOneOfStaticProperty(RemoteOneOfStaticProperty other) {
    super(other);
    this.remoteUrl = other.getRemoteUrl();
    this.valueFieldName = other.getValueFieldName();
    this.labelFieldName = other.getLabelFieldName();
    this.descriptionFieldName = other.getDescriptionFieldName();
    this.options = new Cloner().options(other.getOptions());
  }

  public String getRemoteUrl() {
    return remoteUrl;
  }

  public void setRemoteUrl(String remoteUrl) {
    this.remoteUrl = remoteUrl;
  }

  public String getValueFieldName() {
    return valueFieldName;
  }

  public void setValueFieldName(String valueFieldName) {
    this.valueFieldName = valueFieldName;
  }

  public String getLabelFieldName() {
    return labelFieldName;
  }

  public void setLabelFieldName(String labelFieldName) {
    this.labelFieldName = labelFieldName;
  }

  public String getDescriptionFieldName() {
    return descriptionFieldName;
  }

  public void setDescriptionFieldName(String descriptionFieldName) {
    this.descriptionFieldName = descriptionFieldName;
  }

  public List<Option> getOptions() {
    return options;
  }

  public void setOptions(List<Option> options) {
    this.options = options;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }
}
