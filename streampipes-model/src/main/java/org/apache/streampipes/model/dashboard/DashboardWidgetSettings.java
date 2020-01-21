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

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.vocabulary.StreamPipes;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DASHBOARD_WIDGET_SETTINGS)
@Entity
public class DashboardWidgetSettings extends UnnamedStreamPipesEntity {

  @RdfProperty(StreamPipes.HAS_DASHBOARD_WIDGET_LABEL)
  private String widgetLabel;

  @RdfProperty(StreamPipes.HAS_DASHBOARD_WIDGET_NAME)
  private String widgetName;

  @OneToMany(fetch = FetchType.EAGER,
          cascade = {CascadeType.ALL})
  @RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
  private List<StaticProperty> config;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_SCHEMA)
  private EventSchema requiredSchema;

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
}
