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

package org.apache.streampipes.model;

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;

public class ApplicationLink extends UnnamedStreamPipesEntity {

  private String applicationName;

  private String applicationDescription;

  private String applicationUrl;

  private String applicationIconUrl;

  private String applicationLinkType;

  public ApplicationLink() {
    super();
  }

  public ApplicationLink(String applicationName, String applicationDescription, String applicationUrl,
                         String applicationIconUrl) {
    super();
    this.applicationName = applicationName;
    this.applicationDescription = applicationDescription;
    this.applicationUrl = applicationUrl;
    this.applicationIconUrl = applicationIconUrl;
  }

  public ApplicationLink(ApplicationLink other) {
    super(other);
    this.applicationName = other.getApplicationName();
    this.applicationDescription = other.getApplicationDescription();
    this.applicationUrl = other.getApplicationUrl();
    this.applicationIconUrl = other.getApplicationIconUrl();
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getApplicationDescription() {
    return applicationDescription;
  }

  public void setApplicationDescription(String applicationDescription) {
    this.applicationDescription = applicationDescription;
  }

  public String getApplicationUrl() {
    return applicationUrl;
  }

  public void setApplicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
  }

  public String getApplicationIconUrl() {
    return applicationIconUrl;
  }

  public void setApplicationIconUrl(String applicationIconUrl) {
    this.applicationIconUrl = applicationIconUrl;
  }

  public String getApplicationLinkType() {
    return applicationLinkType;
  }

  public void setApplicationLinkType(String applicationLinkType) {
    this.applicationLinkType = applicationLinkType;
  }
}
