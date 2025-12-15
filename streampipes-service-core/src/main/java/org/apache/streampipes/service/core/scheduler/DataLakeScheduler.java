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
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.export.ObjectStorge.ExportProviderFactory;
import org.apache.streampipes.dataexplorer.export.ObjectStorge.IObjectStorage;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.configuration.ProviderType;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.RetentionAction;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataLakeScheduler implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(DataLakeScheduler.class);

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getSchemaManagement();

    private final IDataExplorerQueryManagement dataExplorerQueryManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getQueryManagement(this.dataExplorerSchemaManagement);

    private void exportMeasurement(DataLakeMeasure dataLakeMeasure, Instant now, long endDate) {

        if (System.getenv("SP_RETENTION_LOCAL_DIR") == null || System.getenv("SP_RETENTION_LOCAL_DIR").isEmpty()) {
            LOG.error("For Local Retention Storage, please configure the environment variable SP_RETENTION_LOCAL_DIR");
        }

        var outputFormat = OutputFormat
                .fromString(dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().format());

        Map<String, String> params = new HashMap<>();

        params.put("delimiter",
                dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().csvDelimiter());
        params.put("format", dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().format());
        params.put("headerColumnName",
                dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().headerColumnName());
        params.put("missingValueBehaviour",
                dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig()
                        .missingValueBehaviour());
        params.put("endDate", Long.toString(endDate));

        ProvidedRestQueryParams sanitizedParams = new ProvidedRestQueryParams(dataLakeMeasure.getMeasureName(), params);
        StreamingResponseBody streamingOutput = output -> dataExplorerQueryManagement.getDataAsStream(
                sanitizedParams,
                outputFormat,
                "ignore".equals(
                        dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig()
                                .missingValueBehaviour()),
                output);

        String exportProviderId = dataLakeMeasure.getRetentionTime().getRetentionExportConfig()
                .getExportProviderId();
        // FInd Item in Document

        List<ExportProviderSettings> exportProviders = StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getSpCoreConfigurationStorage()
                .get()
                .getExportProviderSettings();

        ExportProviderSettings exportProviderSetting = null;

        for (int i = 0; i < exportProviders.size(); i++) {
            ExportProviderSettings existing = exportProviders.get(i);
            if (existing != null && existing.getProviderId().equals(exportProviderId)) {
                exportProviderSetting = existing;
            }
        }

        if (exportProviderSetting == null) {
            LOG.error("The desired export provider was not found. No export has been done.");
            return;
        }

        ProviderType providerType = exportProviderSetting.getProviderType();

        LOG.info("Write to " + System.getenv("SP_RETENTION_LOCAL_DIR"));

        try {

            IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
                    providerType, dataLakeMeasure.getMeasureName(), exportProviderSetting,
                    dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().format());
            exportProvider.store(streamingOutput);

        } catch (IllegalArgumentException e) {

            LOG.error("Export provider could not be created. Unsupported provider type: {}. Error: {}", providerType,
                    e.getMessage(), e);
        } catch (IOException e) {

            LOG.error("I/O error occurred while trying to store data. Provider Type: {}. Error: {}", providerType,
                    e.getMessage(), e);
        } catch (RuntimeException e) {
            LOG.error("Runtime exception occurred while attempting to store data. Provider Type: {}. Error: {}",
                    providerType, e.getMessage(), e);
        } catch (Exception e) {

            LOG.error("An unexpected error occurred during export. Provider Type: {}. Error: {}", providerType,
                    e.getMessage(), e);
        }
    }

    private void deleteMeasurement(DataLakeMeasure dataLakeMeasure, Instant now, long endDate) {

        LOG.info("Current time in millis: " + now.toEpochMilli());
        LOG.info("Current time in millis to delete: " + endDate);

        this.dataExplorerQueryManagement.deleteData(dataLakeMeasure.getMeasureName(), null, endDate);
    }

    private Map<String, Object> getStartAndEndTime(int olderThanDays) {
        Instant now = Instant.now();
        Instant daysAgo = now.minus(olderThanDays, ChronoUnit.DAYS);

        long endDate = daysAgo.toEpochMilli();

        Map<String, Object> result = new HashMap<>();
        result.put("now", now);
        result.put("endDate", endDate);
        return result;
    }

    public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        LOG.info("GET ALL Measurements");
        for (DataLakeMeasure dataLakeMeasure : allMeasurements) {
            
            if (dataLakeMeasure.getRetentionTime() != null) {
                LOG.info("Measurement " + dataLakeMeasure.getMeasureName());

                var result = getStartAndEndTime(
                        dataLakeMeasure.getRetentionTime().getDataRetentionConfig().olderThanDays());
                Instant now = (Instant) result.get("now");
                long endDate = (Long) result.get("endDate");

                if (dataLakeMeasure.getRetentionTime().getDataRetentionConfig().action() != RetentionAction.DELETE) {
                    LOG.info("Start saving Measurement " + dataLakeMeasure.getMeasureName());
                    exportMeasurement(dataLakeMeasure, now, endDate);
                    LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully saved");
                }
                if (dataLakeMeasure.getRetentionTime().getDataRetentionConfig().action() != RetentionAction.SAVE) {
                    LOG.info("Start delete Measurement " + dataLakeMeasure.getMeasureName());
                    deleteMeasurement(dataLakeMeasure, now, endDate);
                    LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully deleted");
                }
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