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
import { RetentionConfig } from './model/retention-config.model';
import { DataRetentionDialogModel } from './model/data-retention-dialog.model';
//import { DataExportService } from './services/data-export.service';

@Component({
    selector: 'sp-data-retention-dialog',
    templateUrl: 'data-retention-dialog.component.html',
    styleUrls: ['./data-retention-dialog.component.scss'],
    standalone: false,
})
export class DataRetentionDialogComponent implements OnInit {
    @Input() dataRetentionDialogModel: DataRetentionDialogModel;

    @ViewChild('retentionDialogStepper', { static: true })
    retentionDialogStepper: MatStepper;

    @Input()
    retentionConfig: RetentionConfig;

    constructor(
        public dialogRef: DialogRef<DataRetentionDialogComponent>,
        //public dataExportService: DataExportService,
    ) {}

    ngOnInit() {
        console.log('INIT THE RETENTION DIALOG');
        const measurementName =
            this.dataRetentionDialogModel.measureName !== undefined
                ? this.dataRetentionDialogModel.measureName
                : this.dataRetentionDialogModel.dataExplorerDataConfig
                      .sourceConfigs[0].measureName;

        this.retentionConfig ??= {
            dataRetentionConfig: {
                olderThanDays: 30,
                interval: 'daily',
                measurement: measurementName,
            },
            //TODO format Retention export
            //formatExportConfig: {
            //    format: 'csv',
            //    delimiter: 'comma',
            //    headerColumnName: 'key',
            //},
        };
        console.log(this.retentionConfig);
    }

    exitDialog() {
        this.dialogRef.close();
    }

    nextStep() {
        this.retentionDialogStepper.next();
    }

    previousStep() {
        this.retentionDialogStepper.previous();
    }
    //TODO Call retention logic
    //downloadData() {
    //    if (
    //        this.exportConfig.dataExportConfig.dataRangeConfiguration ===
    //        'visible'
    //    ) {
    //        this.exportConfig.dataExportConfig.dateRange =
    //            this.dataDownloadDialogModel.dataExplorerDateRange;
    //    }

    //    this.dataExportService.downloadData(
    //        this.exportConfig,
    //        this.dataDownloadDialogModel,
    //    );
    //    this.downloadDialogStepper.next();
    // }
}
