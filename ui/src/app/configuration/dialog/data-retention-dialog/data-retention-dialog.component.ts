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

import { Component, inject, Input, OnInit, ViewChild } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DataRetentionDialogModel } from './model/data-retention-dialog.model';
import {
    DatalakeRestService,
    ExportProviderSettings,
    RetentionTimeConfig,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-retention-dialog',
    templateUrl: 'data-retention-dialog.component.html',
    styleUrls: ['./data-retention-dialog.component.scss'],
    standalone: false,
})
export class DataRetentionDialogComponent implements OnInit {
    @Input() dataRetentionDialogModel: DataRetentionDialogModel;

    retentionConfig: RetentionTimeConfig;
    exportProvider: ExportProviderSettings;

    @Input()
    measurementIndex: string;

    disableDelete = false;

    dialogRef = inject(DialogRef<DataRetentionDialogComponent>);
    datalakeRestService = inject(DatalakeRestService);

    ngOnInit() {
        this.datalakeRestService
            .getMeasurement(this.measurementIndex)
            .subscribe({
                next: measure => {
                    if (
                        measure?.retentionTime ||
                        measure.retentionTime != null
                    ) {
                        this.disableDelete = true;
                        this.retentionConfig ??= measure.retentionTime;
                    } else {
                        this.retentionConfig ??= RetentionTimeConfig.fromData({
                            dataRetentionConfig: {
                                olderThanDays: 30,
                                interval: 'DAILY',
                                action: 'DELETE',
                            },
                            retentionExportConfig: {
                                exportConfig: {
                                    format: 'csv',
                                    csvDelimiter: 'comma',
                                    missingValueBehaviour: 'ignore',
                                    headerColumnName: 'key',
                                },
                                exportProviderId: '',
                            },
                        } as RetentionTimeConfig);
                    }
                },
                error: err => {
                    console.error('Error loading measurement:', err);
                },
            });
    }

    exitDialog() {
        this.dialogRef.close();
    }

    close(refreshDataLakeIndex: boolean) {
        this.dialogRef.close(refreshDataLakeIndex);
    }

    setCleanUp() {
        this.datalakeRestService
            .cleanup(this.measurementIndex, this.retentionConfig)
            .subscribe({
                next: data => {
                    this.close(true);
                },
                error: err => {
                    this.close(false);
                },
            });
    }

    deleteCleanUp() {
        this.datalakeRestService
            .deleteCleanup(this.measurementIndex)
            .subscribe(data => {
                this.close(true);
            });
    }

    requiresExportValidation(): boolean {
        const action = this.retentionConfig?.dataRetentionConfig?.action;
        return action === 'SAVE' || action === 'SAVEDELETE';
    }

    isExportValid(): boolean {
        const exportConfig =
            this.retentionConfig?.retentionExportConfig?.exportConfig;
        const providerId =
            this.retentionConfig?.retentionExportConfig?.exportProviderId;

        if (!exportConfig?.format) {
            console.error('Export format is required.');
            return false;
        }

        if (exportConfig.format === 'csv' && !exportConfig.csvDelimiter) {
            console.error('CSV delimiter is required for CSV format.');
            return false;
        }

        if (providerId == '') {
            console.error('S3 provider details must be selected.');
            return false;
        }

        return true;
    }
}
