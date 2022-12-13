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

package org.apache.streampipes.model.client.monitoring;

import java.util.Date;
import java.util.List;

public class JobReport {

  // contains report of a single job
  // TODO needed?

  //TODO store in database and create queries

  private String elementId;
  private Date generationDate;
  private List<TaskReport> taskReports;

  public JobReport(String elementId, Date generationDate, List<TaskReport> taskResults) {
    this.generationDate = generationDate;
    this.taskReports = taskResults;
    this.elementId = elementId;
  }

  public Date getGenerationDate() {
    return generationDate;
  }

  public void setGenerationDate(Date generationDate) {
    this.generationDate = generationDate;
  }

  public List<TaskReport> getTaskReports() {
    return taskReports;
  }

  public void setTaskReports(List<TaskReport> taskReports) {
    this.taskReports = taskReports;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
