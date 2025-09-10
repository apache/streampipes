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
import org.apache.streampipes.dataexplorer.export.ObjectStorge.ExportProviderFactory;
import org.apache.streampipes.dataexplorer.export.ObjectStorge.IObjectStorage;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.ExportProviderSettings;
import org.apache.streampipes.model.datalake.RetentionAction;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLakeScheduler {

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
                .fromString(dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().format());

        Map<String, String> params = new HashMap<>();

        params.put("delimiter", dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().csvDelimiter());
        params.put("format", dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().format());
        params.put("headerColumnName",
                dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().headerColumnName());
        params.put("missingValueBehaviour",
                dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().missingValueBehaviour());
        params.put("endDate", Long.toString(endDate));

        ProvidedRestQueryParams sanitizedParams = new ProvidedRestQueryParams(dataLakeMeasure.getMeasureName(), params);
        StreamingResponseBody streamingOutput = output -> dataExplorerQueryManagement.getDataAsStream(
                sanitizedParams,
                outputFormat,
                "ignore".equals(
                        dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().missingValueBehaviour()),
                output);
        try {
            ExportProviderSettings exportProviderSettings = dataLakeMeasure.getRetentionTime().exportConfig()
                    .exportProviderSettings();

            String providerType = exportProviderSettings.providerType();

            LOG.info("Write to " + System.getenv("SP_RETENTION_LOCAL_DIR"));

            IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
                    providerType, dataLakeMeasure.getMeasureName(), exportProviderSettings,
                    dataLakeMeasure.getRetentionTime().exportConfig().exportConfig().format());
            exportProvider.store(streamingOutput);

        } catch (Exception e) {
            e.printStackTrace();
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

    @Scheduled(cron = "0 1 0 * * 6") // CronJob Scheduled every Saturday (6) 00:01 //@Scheduled(cron = "0 */2 * * *
                                     // *") //Cron Job in Dev Setting; Running every 2 min
    public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        LOG.info("GET ALL Measurements");
        for (DataLakeMeasure dataLakeMeasure : allMeasurements) {
            LOG.info("Measurement " + dataLakeMeasure.getMeasureName());
            if (dataLakeMeasure.getRetentionTime() != null) {

                var result = getStartAndEndTime(
                        dataLakeMeasure.getRetentionTime().dataRetentionConfig().olderThanDays());
                Instant now = (Instant) result.get("now");
                long endDate = (Long) result.get("endDate");

                if (dataLakeMeasure.getRetentionTime().dataRetentionConfig().action() != RetentionAction.DELETE) {
                    LOG.info("Start saving Measurement " + dataLakeMeasure.getMeasureName());
                    exportMeasurement(dataLakeMeasure, now, endDate);
                    LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully saved");
                }
                if (dataLakeMeasure.getRetentionTime().dataRetentionConfig().action() != RetentionAction.SAVE) {
                    LOG.info("Start delete Measurement " + dataLakeMeasure.getMeasureName());
                    deleteMeasurement(dataLakeMeasure, now, endDate);
                    LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully deleted");
                }
            }
        }
    }
}