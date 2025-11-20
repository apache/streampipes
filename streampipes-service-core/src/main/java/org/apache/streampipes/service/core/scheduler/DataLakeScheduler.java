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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.export.DataLakeExportManager;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;

@Configuration
public class DataLakeScheduler implements SchedulingConfigurer {

    private static DataLakeExportManager dataLakeExportManager = new DataLakeExportManager();
    private static final Logger LOG = LoggerFactory.getLogger(DataLakeExportManager.class);

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getSchemaManagement();

    public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        LOG.info("GET ALL Measurements");
        for (DataLakeMeasure dataLakeMeasure : allMeasurements) {
            try {
                dataLakeExportManager.cleanupSingleMeasurement(dataLakeMeasure);
            } catch (Exception e) {
                LOG.error(String.format("An unexpected error occurred during export. Data Measure: %s. Error: %s",
                        dataLakeMeasure, e.getMessage()), e);
            }

        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        var env = Environments.getEnvironment();
        LOG.info("Retention CRON Job triggered.");
        taskRegistrar.addTriggerTask(

                this::cleanupMeasurements,

                triggerContext -> new CronTrigger(env.getDatalakeSchedulerCron().getValueOrDefault())
                        .nextExecution(triggerContext)

        );

        LOG.info("Retention CRON Job finished.");

    }
}