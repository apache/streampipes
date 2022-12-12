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

package org.apache.streampipes.model.client.deployment;

public class DeploymentConfiguration {

  private String groupId;
  private String artifactId;
  private String classNamePrefix;

  private String elementId;
  private boolean draft;

  private int port;

  private DeploymentType deploymentType;
  private ElementType elementType;
  private OutputType outputType;
  private RuntimeType runtimeType;

  public DeploymentConfiguration() {

  }

  public DeploymentConfiguration(String groupId, String artifactId,
                                 String classNamePrefix, int port) {
    super();
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.classNamePrefix = classNamePrefix;
    this.port = port;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getClassNamePrefix() {
    return classNamePrefix;
  }

  public void setClassNamePrefix(String classNamePrefix) {
    this.classNamePrefix = classNamePrefix;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public boolean isDraft() {
    return draft;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  public DeploymentType getDeploymentType() {
    return deploymentType;
  }

  public void setDeploymentType(DeploymentType deploymentType) {
    this.deploymentType = deploymentType;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public void setElementType(ElementType elementType) {
    this.elementType = elementType;
  }

  public RuntimeType getRuntimeType() {
    return runtimeType;
  }

  public void setRuntimeType(RuntimeType runtimeType) {
    this.runtimeType = runtimeType;
  }

  public OutputType getOutputType() {
    return outputType;
  }

  public void setOutputType(OutputType outputType) {
    this.outputType = outputType;
  }

}
