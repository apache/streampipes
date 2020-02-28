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

package org.apache.streampipes.model.datalake;

import org.apache.streampipes.model.dashboard.DashboardEntity;
import org.apache.streampipes.vocabulary.StreamPipes;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.DATA_EXPLORER_WIDGET_MODEL)
@Entity
public class DataExplorerWidgetModel extends DashboardEntity {

  @RdfProperty(StreamPipes.HAS_DASHBOARD_WIDGET_ID)
  private String widgetId;

  @RdfProperty(StreamPipes.HAS_MEASUREMENT_NAME)
  private String measureName;

  @OneToOne(fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @RdfProperty(StreamPipes.HAS_DATA_LAKE_MEASURE)
  private DataLakeMeasure dataLakeMeasure;

  public DataExplorerWidgetModel() {
    super();
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public DataLakeMeasure getDataLakeMeasure() {
    return dataLakeMeasure;
  }

  public void setDataLakeMeasure(DataLakeMeasure dataLakeMeasure) {
    this.dataLakeMeasure = dataLakeMeasure;
  }

  public String getMeasureName() {
    return measureName;
  }

  public void setMeasureName(String measureName) {
    this.measureName = measureName;
  }
}
