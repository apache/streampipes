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

    public void exportMeasurements(DataLakeMeasure m, Instant now, long endDate) {
        // Method body is empty; add functionality as needed
        //Prepare Data for export 

        var outputFormat = OutputFormat.fromString(m.getRetentionTime().exportConfig().exportConfig().format());
        Map<String, String> params = new HashMap<>();
        
        params.put("delimiter", m.getRetentionTime().exportConfig().exportConfig().csvDelimiter());
        params.put("format", m.getRetentionTime().exportConfig().exportConfig().format());
        params.put("headerColumnName", "key");
        params.put("missingValueBehaviour", m.getRetentionTime().exportConfig().exportConfig().missingValueBehaviour());
        params.put("endDate",  Long.toString(endDate));

        ProvidedRestQueryParams sanitizedParams = new ProvidedRestQueryParams(m.getMeasureName(), params);//populate(m.getMeasureName(), params);
        
        StreamingResponseBody streamingOutput = output -> dataExplorerQueryManagement.getDataAsStream(
                sanitizedParams,
                outputFormat,
                "ignore".equals(m.getRetentionTime().exportConfig().exportConfig().missingValueBehaviour()),
                output
         );
         try {
            ExportProviderSettings exportProviderSettings = m.getRetentionTime().exportConfig().exportProviderSettings();

            String providerType =  exportProviderSettings.providerType();
        
            IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
                providerType, streamingOutput, exportProviderSettings);
            exportProvider.store();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMeasurements(DataLakeMeasure m, Instant now, long endDate) {
       
     
        LOG.info("Current time in millis: " + now.toEpochMilli());
        LOG.info("Current time in millis to delete: " + endDate);

        this.dataExplorerQueryManagement.deleteData(m.getMeasureName(), null, endDate);
    }

    @Scheduled(cron = "0 */2 * * * *")//@Scheduled(cron = "0 1 0 * * 6")//@Scheduled(cron = "0 */2 * * * *")//@Scheduled(cron = "0 1 0 * * 6") // CronJob Scheduled every Saturday (5) 00:01
    public void cleanupMeasurements() {
        List<DataLakeMeasure> allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();
        LOG.info("GET ALL Measurements");
        for (DataLakeMeasure m : allMeasurements) {
            LOG.info("Measurement " + m.getMeasureName());
            if (m.getRetentionTime() != null) {
               
                Instant now = Instant.now();
                Instant daysAgo = now.minus(m.getRetentionTime().dataRetentionConfig().olderThanDays(), ChronoUnit.DAYS);

                long endDate = daysAgo.toEpochMilli();

                if (m.getRetentionTime().dataRetentionConfig().action() != RetentionAction.DELETE){
                LOG.info("Start saving Measurement " + m.getMeasureName());
                exportMeasurements(m, now,endDate);
                LOG.info("Measurements " + m.getMeasureName() + " successfully saved");
                }
                if (m.getRetentionTime().dataRetentionConfig().action() != RetentionAction.SAVE){
                LOG.info("Start delete Measurement " + m.getMeasureName());
                deleteMeasurements(m,now, endDate);
                LOG.info("Measurements " + m.getMeasureName() + " successfully deleted");
            }
            }
        }
    }
}