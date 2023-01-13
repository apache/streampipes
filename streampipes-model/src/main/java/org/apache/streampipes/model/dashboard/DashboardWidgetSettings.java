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

package org.apache.streampipes.model.dashboard;

import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

@TsModel
public class DashboardWidgetSettings {

  private String widgetLabel;

  private String widgetName;

  private List<StaticProperty> config;

  private EventSchema requiredSchema;

  private String widgetIconName;

  private String widgetDescription;

  public DashboardWidgetSettings() {
    super();
  }

  public String getWidgetLabel() {
    return widgetLabel;
  }

  public void setWidgetLabel(String widgetLabel) {
    this.widgetLabel = widgetLabel;
  }

  public String getWidgetName() {
    return widgetName;
  }

  public void setWidgetName(String widgetName) {
    this.widgetName = widgetName;
  }

  public List<StaticProperty> getConfig() {
    return config;
  }

  public void setConfig(List<StaticProperty> config) {
    this.config = config;
  }

  public EventSchema getRequiredSchema() {
    return requiredSchema;
  }

  public void setRequiredSchema(EventSchema requiredSchema) {
    this.requiredSchema = requiredSchema;
  }

  public String getWidgetIconName() {
    return widgetIconName;
  }

  public void setWidgetIconName(String widgetIconName) {
    this.widgetIconName = widgetIconName;
  }

  public String getWidgetDescription() {
    return widgetDescription;
  }

  public void setWidgetDescription(String widgetDescription) {
    this.widgetDescription = widgetDescription;
  }
}
