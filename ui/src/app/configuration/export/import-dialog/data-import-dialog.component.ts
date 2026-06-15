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

import { Component, inject } from '@angular/core';
import {
    DialogRef,
    SpAlertBannerComponent,
    SpAssetBrowserService,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { DataExportService } from '../data-export.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import {
    AssetExportConfiguration,
    ExportItem,
} from '@streampipes/platform-services';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    MatError,
    MatFormField,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpDataExportItemComponent } from '../export-dialog/data-export-item/data-export-item.component';
import { GenericStorageItemsComponent } from '../export-dialog/generic-storage-items/generic-storage-items.component';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDivider } from '@angular/material/divider';

@Component({
    selector: 'sp-data-import-dialog',
    templateUrl: './data-import-dialog.component.html',
    styleUrls: ['./data-import-dialog.component.scss'],
    imports: [
        SplitSectionComponent,
        MatFormField,
        MatInput,
        MatProgressBar,
        MatButton,
        MatSuffix,
        MatIcon,
        MatError,
        SpAlertBannerComponent,
        LayoutGapDirective,
        SpDataExportItemComponent,
        GenericStorageItemsComponent,
        LayoutDirective,
        MatCheckbox,
        FormsModule,
        FlexDirective,
        LayoutAlignDirective,
        MatProgressSpinner,
        MatDivider,
        TranslatePipe,
    ],
})
export class SpDataImportDialogComponent {
    private dialogRef =
        inject<DialogRef<SpDataImportDialogComponent>>(DialogRef);
    private dataExportService = inject(DataExportService);
    private assetBrowserService = inject(SpAssetBrowserService);

    private translateService = inject(TranslateService);
    currentImportStep = 0;

    inputValue: string;
    fileName: string;

    selectedUploadFile: File;
    importConfiguration: AssetExportConfiguration;

    hasInput = false;
    errorMessage = this.translateService.instant('Please enter a value');

    uploadStatus = 0;
    uploadError = false;

    handleFileInput(files: any) {
        this.hasInput = true;
        this.uploadError = false;
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
                            this.importConfiguration.overrideBrokerSettings = true;
                            this.currentImportStep++;
                        }
                    },
                    _error => {
                        this.uploadError = true;
                    },
                );
        }
    }

    performImport(): void {
        this.currentImportStep = 2;
        this.dataExportService
            .triggerImport(this.selectedUploadFile, this.importConfiguration)
            .subscribe(_result => {
                this.assetBrowserService.refreshBrowserAssetData();
                this.dialogRef.close();
            });
    }

    back(): void {
        this.currentImportStep--;
    }

    close(): void {
        this.dialogRef.close();
    }

    toggleSelect(select: boolean): void {
        if (this.importConfiguration) {
            this.toggleExportItems(select);
        }
    }

    private toggleExportItems(select: boolean): void {
        this.toggleAllItems(this.importConfiguration.files, select);
        this.toggleAllItems(this.importConfiguration.dataSources, select);
        this.toggleAllItems(this.importConfiguration.adapters, select);
        this.toggleAllItems(this.importConfiguration.assets, select);
        this.toggleAllItems(this.importConfiguration.dashboards, select);
        this.toggleAllItems(this.importConfiguration.dataViews, select);
        this.toggleAllItems(this.importConfiguration.dataLakeMeasures, select);
        this.toggleAllItems(
            this.importConfiguration.genericStorageDocuments,
            select,
        );
        this.toggleAllItems(this.importConfiguration.pipelines, select);
    }

    private toggleAllItems(exportItem: ExportItem[], select: boolean): void {
        exportItem.forEach(e => (e.selected = select));
    }
}
