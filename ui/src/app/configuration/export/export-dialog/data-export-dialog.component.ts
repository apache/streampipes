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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DataExportService } from '../data-export.service';
import { ExportConfiguration } from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-export-dialog',
    templateUrl: './data-export-dialog.component.html',
    styleUrls: ['./data-export-dialog.component.scss'],
})
export class SpDataExportDialogComponent implements OnInit {
    @Input()
    selectedAssets: string[];

    preview: ExportConfiguration;
    exportInProgress = false;

    constructor(
        private dialogRef: DialogRef<SpDataExportDialogComponent>,
        private dataExportService: DataExportService,
    ) {}

    ngOnInit(): void {
        this.dataExportService
            .getExportPreview(this.selectedAssets)
            .subscribe(preview => {
                this.preview = preview;
            });
    }

    close(): void {
        this.dialogRef.close();
    }

    generateDownloadPackage(): void {
        this.exportInProgress = true;
        this.dataExportService.triggerExport(this.preview).subscribe(result => {
            this.downloadFile(result);
        });
    }

    downloadFile(data: any) {
        const blob = new Blob([data], { type: 'application/zip' });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
        this.dialogRef.close();
    }
}
