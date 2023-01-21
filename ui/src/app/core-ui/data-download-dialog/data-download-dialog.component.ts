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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { DialogRef } from '@streampipes/shared-ui';
import { ExportConfig } from './model/export-config.model';
import { DataDownloadDialogModel } from './model/data-download-dialog.model';
import { DataExportService } from './services/data-export.service';

@Component({
    selector: 'sp-data-download-dialog',
    templateUrl: 'data-download-dialog.component.html',
    styleUrls: ['./data-download-dialog.component.scss'],
})
export class DataDownloadDialogComponent implements OnInit {
    @Input() dataDownloadDialogModel: DataDownloadDialogModel;

    @ViewChild('downloadDialogStepper', { static: true })
    downloadDialogStepper: MatStepper;

    exportConfig: ExportConfig;

    constructor(
        public dialogRef: DialogRef<DataDownloadDialogComponent>,
        public dataExportService: DataExportService,
    ) {}

    ngOnInit() {
        const measurementName =
            this.dataDownloadDialogModel.measureName !== undefined
                ? this.dataDownloadDialogModel.measureName
                : this.dataDownloadDialogModel.dataExplorerDataConfig
                      .sourceConfigs[0].measureName;

        this.exportConfig = {
            dataExportConfig: {
                dataRangeConfiguration: 'all',
                missingValueBehaviour: 'ignore',
                measurement: measurementName,
            },
            formatExportConfig: {
                exportFormat: 'csv',
                delimiter: 'comma',
            },
        };
    }

    exitDialog() {
        this.dialogRef.close();
    }

    nextStep() {
        this.downloadDialogStepper.next();
    }

    previousStep() {
        this.downloadDialogStepper.previous();
    }

    downloadData() {
        if (
            this.exportConfig.dataExportConfig.dataRangeConfiguration ===
            'visible'
        ) {
            this.exportConfig.dataExportConfig.dateRange =
                this.dataDownloadDialogModel.dataExplorerDateRange;
        }

        this.dataExportService.downloadData(
            this.exportConfig,
            this.dataDownloadDialogModel,
        );
        this.downloadDialogStepper.next();
    }
}
