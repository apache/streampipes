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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;

import java.util.ArrayList;
import java.util.List;

public class RetentionExportConfig {

    private ExportConfig exportConfig;
    private String exportProviderId;
    private String lastExport;
    private List<RetentionLog> retentionLog = new ArrayList<>();

    public RetentionExportConfig(ExportConfig exportConfig, String exportProviderId, String lastExport,
            List<RetentionLog> retentionLog) {
        this.exportConfig = exportConfig;
        this.exportProviderId = exportProviderId;
        this.lastExport = lastExport;
        this.retentionLog = retentionLog;
    }

    public ExportConfig getExportConfig() {
        return exportConfig;
    }

    public void setExportConfig(ExportConfig exportConfig) {
        this.exportConfig = exportConfig;
    }

    public String getExportProviderId() {
        return exportProviderId;
    }

    public void setExportProviderId(String exportProviderId) {
        this.exportProviderId = exportProviderId;
    }

    public void setLastExport(String lastExport) {
        this.lastExport = lastExport;
    }

    public String getLastExport() {
        return lastExport;

    }

    public List<RetentionLog> getRetentionLog() {
        return retentionLog;
    }

    public void setRetentionLog(List<RetentionLog> retentionLog) {
        this.retentionLog = retentionLog;
    }

    public void addRetentionLog(RetentionLog log) {

        Environment env = Environments.getEnvironment();

        int maxSize = env.getDatalakeRetentionLogLength().getValueOrDefault();

        if (this.retentionLog != null) {

            if (this.retentionLog.size() >= maxSize) {
                this.retentionLog.remove(0);
            }

            this.retentionLog.add(log);
        } else {

            this.retentionLog = new ArrayList<>();
            this.retentionLog.add(log);
        }
    }

}