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

import { Component, Input, OnInit, inject } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { DataExportService } from '../data-export.service';
import {
    ExportConfiguration,
    ExportItem,
} from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpDataExportItemComponent } from './data-export-item/data-export-item.component';
import { GenericStorageItemsComponent } from './generic-storage-items/generic-storage-items.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-export-dialog',
    templateUrl: './data-export-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        SpDataExportItemComponent,
        GenericStorageItemsComponent,
        LayoutAlignDirective,
        MatProgressSpinner,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class SpDataExportDialogComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<SpDataExportDialogComponent>>(DialogRef);
    private dataExportService = inject(DataExportService);

    @Input()
    selectedAssets: string[];

    @Input()
    referencedLabels: ExportItem[] = [];

    @Input()
    referencedSites: ExportItem[] = [];

    preview: ExportConfiguration;
    exportInProgress = false;

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
        this.addReferencedGenericStorageDocuments(this.preview);
        this.dataExportService.triggerExport(this.preview).subscribe(result => {
            this.downloadFile(result);
        });
    }

    downloadFile(data: any) {
        const blob = new Blob([data], { type: 'application/zip' });
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');

        anchor.href = url;
        anchor.download = 'data_export';
        anchor.style.display = 'none';
        document.body.appendChild(anchor);

        anchor.click();

        window.URL.revokeObjectURL(url);
        this.dialogRef.close();
    }

    private addReferencedGenericStorageDocuments(
        preview: ExportConfiguration,
    ): void {
        const firstAssetExportConfig = preview.assetExportConfiguration?.[0];
        const referencedGenericStorageDocuments = [
            ...this.referencedLabels,
            ...this.referencedSites,
        ];

        if (
            firstAssetExportConfig &&
            referencedGenericStorageDocuments.length > 0
        ) {
            firstAssetExportConfig.genericStorageDocuments ??= [];
            const existingDocumentIds = new Set(
                firstAssetExportConfig.genericStorageDocuments.map(
                    item => item.resourceId,
                ),
            );
            const missingDocuments = referencedGenericStorageDocuments.filter(
                item => !existingDocumentIds.has(item.resourceId),
            );

            firstAssetExportConfig.genericStorageDocuments = [
                ...firstAssetExportConfig.genericStorageDocuments,
                ...missingDocuments,
            ];
        }
    }
}
