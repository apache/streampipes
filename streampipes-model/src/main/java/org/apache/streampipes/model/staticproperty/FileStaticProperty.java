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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.FILE_STATIC_PROPERTY)
@Entity
public class FileStaticProperty extends StaticProperty {

  private static final long serialVersionUID = 1L;

  @RdfProperty(StreamPipes.HAS_ENDPOINT_URL)
  private String endpointUrl;

  @RdfProperty(StreamPipes.HAS_LOCATION_PATH)
  private String locationPath;

  @RdfProperty(StreamPipes.HAS_REQUIRED_FILETYPES)
  private List<String> requiredFiletypes;

  public FileStaticProperty() {
    super(StaticPropertyType.FileStaticProperty);
    this.requiredFiletypes = new ArrayList<>();
  }

  public FileStaticProperty(FileStaticProperty other) {
    super(other);
    this.endpointUrl = other.getEndpointUrl();
    this.locationPath = other.getLocationPath();
    this.requiredFiletypes = other.getRequiredFiletypes();
  }

  public FileStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.FileStaticProperty, internalName, label, description);
    this.requiredFiletypes = new ArrayList<>();
  }

  public String getEndpointUrl() {
    return endpointUrl;
  }

  public void setEndpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
  }

  public String getLocationPath() {
    return locationPath;
  }

  public void setLocationPath(String locationPath) {
    this.locationPath = locationPath;
  }

  public List<String> getRequiredFiletypes() {
    return requiredFiletypes;
  }

  public void setRequiredFiletypes(List<String> requiredFiletypes) {
    this.requiredFiletypes = requiredFiletypes;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }
}
