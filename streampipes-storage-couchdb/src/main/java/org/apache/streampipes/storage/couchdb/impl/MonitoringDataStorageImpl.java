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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.client.monitoring.JobReport;
import org.apache.streampipes.storage.api.IPipelineMonitoringDataStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoringDataStorageImpl extends AbstractDao<JobReport> implements IPipelineMonitoringDataStorage {

  public MonitoringDataStorageImpl() {
    super(Utils::getCouchDbMonitoringClient, JobReport.class);
  }

  @Override
  public List<JobReport> getAllMonitoringJobReports() {
    return findAll();
  }

  @Override
  public List<JobReport> getAllMonitoringJobReportsByElement(String elementUri) {
    List<JobReport> allReports = findAll();
    return getJobReportStream(elementUri, allReports)
        .collect(Collectors.toList());
  }

  private Stream<JobReport> getJobReportStream(String elementUri, List<JobReport> allReports) {
    return allReports
        .stream()
        .filter(r -> r.getElementId().equals(elementUri));
  }

  @Override
  public JobReport getLatestJobReport(String elementUri) {
    List<JobReport> allReports = findAll();
    return getJobReportStream(elementUri, allReports)
        .sorted(Comparator.comparing(JobReport::getGenerationDate))
        .findFirst()
        .get();
  }

  @Override
  public boolean storeJobReport(JobReport jobReport) {
    return persist(jobReport).k;
  }

}
