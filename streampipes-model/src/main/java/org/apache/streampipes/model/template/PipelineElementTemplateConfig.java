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
package org.apache.streampipes.model.template;

public class PipelineElementTemplateConfig {

  private boolean editable;
  private boolean displayed;

  private Object value;

  public PipelineElementTemplateConfig(boolean editable, boolean displayed, Object value) {
    this.editable = editable;
    this.displayed = displayed;
    this.value = value;
  }

  public PipelineElementTemplateConfig() {
  }

  private PipelineElementTemplateConfig(Object value) {
    this.value = value;
  }

  public static PipelineElementTemplateConfig from(Object value) {
    return new PipelineElementTemplateConfig(value);
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public boolean isDisplayed() {
    return displayed;
  }

  public void setDisplayed(boolean displayed) {
    this.displayed = displayed;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
