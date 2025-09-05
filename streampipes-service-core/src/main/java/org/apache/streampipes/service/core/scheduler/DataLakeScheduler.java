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
package org.apache.streampipes.service.core.scheduler;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Component
public class DataLakeScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(DataLakeScheduler.class);

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getSchemaManagement();

    private final IDataExplorerQueryManagement dataExplorerQueryManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getQueryManagement(this.dataExplorerSchemaManagement);

    public void exportMeasurements() {
        // Method body is empty; add functionality as needed
    }

    public void deleteMeasurements(DataLakeMeasure m) {
        Instant now = Instant.now();
        Instant daysAgo = now.minus(m.getRetentionTime().dataRetentionConfig().olderThanDays(), ChronoUnit.DAYS);

        long endDate = daysAgo.toEpochMilli();
        LOG.info("Current time in millis: " + now.toEpochMilli());
        LOG.info("Current time in millis to delete: " + endDate);

        this.dataExplorerQueryManagement.deleteData(m.getMeasureName(), null, endDate);
    }

    @Scheduled(cron = "0 1 0 * * 6") // CronJob Scheduled every Saturday (5) 00:01
    public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        LOG.info("GET ALL Measurements");
        for (DataLakeMeasure m : allMeasurements) {
            if (m.getRetentionTime() != null) {
                LOG.info("Start delete Measurement " + m.getMeasureName());
                deleteMeasurements(m);
                LOG.info("Measurements " + m.getMeasureName() + " successfully deleted");
            }
        }
    }
}