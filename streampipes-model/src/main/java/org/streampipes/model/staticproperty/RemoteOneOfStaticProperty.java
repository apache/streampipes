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

package org.streampipes.model.staticproperty;


import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.REMOTE_ONE_OF_STATIC_PROPERTY)
@Entity
public class RemoteOneOfStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 3483290363677184344L;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_OPTION)
  private List<Option> options;

  @RdfProperty(StreamPipes.REMOTE_URL)
  private String remoteUrl;

  @RdfProperty(StreamPipes.VALUE_FIELD_NAME)
  private String valueFieldName;

  @RdfProperty(StreamPipes.LABEL_FIELD_NAME)
  private String labelFieldName;

  @RdfProperty(StreamPipes.DESCRIPTION_FIELD_NAME)
  private String descriptionFieldName;

  public RemoteOneOfStaticProperty() {
    super(StaticPropertyType.OneOfStaticProperty);
    this.options = new ArrayList<>();
  }

  public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl, String valueFieldName, String labelFieldName, String descriptionFieldName, boolean valueRequired) {
    super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
    this.remoteUrl = remoteUrl;
    this.valueFieldName = valueFieldName;
    this.labelFieldName = labelFieldName;
    this.descriptionFieldName = descriptionFieldName;
    this.valueRequired = valueRequired;
    this.options = new ArrayList<>();
  }


  public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl, String valueFieldName, String labelFieldName, String descriptionFieldName) {
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
}
