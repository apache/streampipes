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

import { Component } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DataExportService } from '../data-export.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { AssetExportConfiguration } from '../../../../../dist/streampipes/platform-services';

@Component({
    selector: 'sp-data-import-dialog',
    templateUrl: './data-import-dialog.component.html',
    styleUrls: ['./data-import-dialog.component.scss'],
})
export class SpDataImportDialogComponent {
    currentImportStep = 0;

    inputValue: string;
    fileName: string;

    selectedUploadFile: File;
    importConfiguration: AssetExportConfiguration;

    hasInput = false;
    errorMessage = 'Please enter a value';

    uploadStatus = 0;

    constructor(
        private dialogRef: DialogRef<SpDataImportDialogComponent>,
        private dataExportService: DataExportService,
    ) {}

    handleFileInput(files: any) {
        this.hasInput = true;
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;
        this.uploadStatus = 0;
    }

    performPreview(): void {
        this.uploadStatus = 0;
        if (this.selectedUploadFile !== undefined) {
            this.dataExportService
                .getImportPreview(this.selectedUploadFile)
                .subscribe(
                    event => {
                        if (event.type === HttpEventType.UploadProgress) {
                            this.uploadStatus = Math.round(
                                (100 * event.loaded) / event.total,
                            );
                        } else if (event instanceof HttpResponse) {
                            this.importConfiguration =
                                event.body as AssetExportConfiguration;
                            this.importConfiguration.overrideBrokerSettings =
                                true;
                            this.currentImportStep++;
                        }
                    },
                    error => {},
                );
        }
    }

    performImport(): void {
        this.currentImportStep = 2;
        this.dataExportService
            .triggerImport(this.selectedUploadFile, this.importConfiguration)
            .subscribe(result => {
                this.dialogRef.close();
            });
    }

    back(): void {
        this.currentImportStep--;
    }

    close(): void {
        this.dialogRef.close();
    }
}
