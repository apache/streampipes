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
package org.apache.streampipes.export;

import org.apache.streampipes.commons.environment.Environment;
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
import org.apache.streampipes.model.datalake.RetentionLog;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLakeExportManager {

    private static final Logger LOG = LoggerFactory.getLogger(DataLakeExportManager.class);
    private static final Environment env = Environments.getEnvironment();

    private final IDataExplorerSchemaManagement dataExplorerSchemaManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getSchemaManagement();

    private final IDataExplorerQueryManagement dataExplorerQueryManagement = new DataExplorerDispatcher()
            .getDataExplorerManager()
            .getQueryManagement(this.dataExplorerSchemaManagement);

    private String savePath = "";

    private void exportMeasurement(DataLakeMeasure dataLakeMeasure, Instant now, long endDate) throws Exception {

        if (env.getRetentionLocalDir().getValueOrDefault() == null
                || env.getRetentionLocalDir().getValueOrDefault().isEmpty()) {
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
            throw new Exception("The desired export provider was not found. No export has been done.");
        }

        ProviderType providerType = exportProviderSetting.getProviderType();

        try {

            IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
                    providerType, dataLakeMeasure.getMeasureName(), exportProviderSetting,
                    dataLakeMeasure.getRetentionTime().getRetentionExportConfig().getExportConfig().format());
            exportProvider.store(streamingOutput);
            savePath = exportProvider.getFileName();

        } catch (IllegalArgumentException e) {

            String msg = String.format(
                    "Export provider could not be created. Unsupported provider type: %s. Error: %s",
                    providerType, e.getMessage());

            LOG.error(msg, e);
            throw new IllegalArgumentException(msg, e);

        } catch (IOException e) {

            String msg = String.format(
                    "I/O error occurred while trying to store data. Provider Type: %s. Error: %s",
                    providerType, e.getMessage());

            LOG.error(msg, e);
            throw new IOException(msg, e);

        } catch (RuntimeException e) {

            String msg = String.format(
                    "Runtime exception occurred while attempting to store data. Provider Type: %s. Error: %s",
                    providerType, e.getMessage());

            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        } catch (Exception e) {

            LOG.error("An unexpected error occurred during export. Provider Type: {}. Error: {}", providerType,
                    e.getMessage(), e);
            throw new Exception(
                    String.format("An unexpected error occurred during export. Provider Type: %s. Error: %s",
                            providerType, e.getMessage()),
                    e);
        }
    }

    private void updateLastSync(DataLakeMeasure dataLakeMeasure, Instant now, boolean success, String error) {
        dataLakeMeasure.getRetentionTime().getRetentionExportConfig().setLastExport(now.toString());
        dataLakeMeasure.getRetentionTime().getRetentionExportConfig()
                .addRetentionLog(new RetentionLog(success, this.savePath, now.toString(), error));
        this.dataExplorerSchemaManagement.updateMeasurement(dataLakeMeasure);

    }

    private void deleteMeasurement(DataLakeMeasure dataLakeMeasure, Instant now, long endDate) {

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

    public void cleanupSingleMeasurement(DataLakeMeasure dataLakeMeasure) throws Exception {
        boolean success = false;
        Instant now = Instant.now();
        if (dataLakeMeasure.getRetentionTime() != null) {
            LOG.info("Measurement " + dataLakeMeasure.getMeasureName());

            var result = getStartAndEndTime(
                    dataLakeMeasure.getRetentionTime().getDataRetentionConfig().olderThanDays());
            now = (Instant) result.get("now");
            long endDate = (Long) result.get("endDate");

            if (dataLakeMeasure.getRetentionTime().getDataRetentionConfig().action() != RetentionAction.DELETE) {
                try {
                    exportMeasurement(dataLakeMeasure, now, endDate);
                } catch (Exception e) {
                    updateLastSync(dataLakeMeasure, now, false, e.getMessage());
                    throw new Exception(e);

                }
                LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully saved");
            }
            if (dataLakeMeasure.getRetentionTime().getDataRetentionConfig().action() != RetentionAction.SAVE) {
                deleteMeasurement(dataLakeMeasure, now, endDate);
                LOG.info("Measurements " + dataLakeMeasure.getMeasureName() + " successfully deleted");
            }
            success = true;
            updateLastSync(dataLakeMeasure, now, success, "-");

        }

    }

}
