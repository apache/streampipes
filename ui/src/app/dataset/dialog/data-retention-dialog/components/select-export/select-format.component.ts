/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    DataExplorerDataConfig,
    ExportProviderService,
    ExportProviderSettings,
    RetentionTimeConfig,
} from '@streampipes/platform-services';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-export',
    templateUrl: './select-format.component.html',
    styleUrls: ['./select-format.component.scss'],
    imports: [
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        MatFormField,
        MatSelect,
        MatOption,
        TranslatePipe,
        SplitSectionComponent,
        FormFieldComponent,
        SpAlertBannerComponent,
    ],
})
export class SelectDataExportComponent implements OnInit {
    @Input()
    dataExplorerDataConfig: DataExplorerDataConfig;
    @Input()
    dataRetentionConfig: RetentionTimeConfig;

    exportProviderRestService = inject(ExportProviderService);

    exportProvider: ExportProviderSettings;

    availableExportProvider: ExportProviderSettings[] = [];
    availableS3ExportProvider: ExportProviderSettings[] = [];
    availableFolderExportProvider: ExportProviderSettings[] = [];
    providerType: string[] = ['Folder', 'S3'];
    selectedProviderType: string;
    selectedProviderId: string;

    ngOnInit() {
        this.loadAvailableExportProvider();
        this.selectedProviderType = 'FOLDER';

        if (
            this.dataRetentionConfig.retentionExportConfig.exportProviderId !==
            ''
        ) {
            this.exportProviderRestService
                .getExportProviderById(
                    this.dataRetentionConfig.retentionExportConfig
                        .exportProviderId,
                )
                .subscribe(exportProvider => {
                    this.exportProvider = exportProvider;
                    if (this.exportProvider) {
                        this.selectedProviderType =
                            this.exportProvider.providerType;

                        this.selectedProviderId =
                            this.exportProvider.providerId;
                        this.dataRetentionConfig.retentionExportConfig.exportProviderId =
                            this.selectedProviderId;
                    }
                });
        }
    }

    loadAvailableExportProvider() {
        this.availableExportProvider = [];
        this.exportProviderRestService
            .getAllExportProviders()
            .subscribe(allExportProviders => {
                this.availableExportProvider = allExportProviders;

                this.availableS3ExportProvider =
                    this.availableExportProvider.filter(
                        provider => provider.providerType === 'S3',
                    );
                this.availableFolderExportProvider =
                    this.availableExportProvider.filter(
                        provider => provider.providerType === 'FOLDER',
                    );
                // Defualts to Folder
                this.dataRetentionConfig.retentionExportConfig.exportProviderId =
                    this.availableFolderExportProvider[0].providerId;
            });
    }
    onProviderTypeChange(type: string): void {
        this.selectedProviderType = type;
        // sets default
        if (
            type === 'FOLDER' &&
            this.availableFolderExportProvider.length > 0
        ) {
            this.dataRetentionConfig.retentionExportConfig.exportProviderId =
                this.availableFolderExportProvider[0].providerId;
            this.selectedProviderId =
                this.availableFolderExportProvider[0].providerId;
        } else if (type === 'S3' && this.availableS3ExportProvider.length > 0) {
            this.dataRetentionConfig.retentionExportConfig.exportProviderId =
                this.availableS3ExportProvider[0].providerId;
            this.selectedProviderId =
                this.availableS3ExportProvider[0].providerId;
        } else {
            // no providers available for this type, clear the exportProviderId
            this.dataRetentionConfig.retentionExportConfig.exportProviderId =
                '';
            this.selectedProviderId = '';
        }
    }
}
