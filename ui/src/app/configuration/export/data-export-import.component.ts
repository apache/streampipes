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

import { Component, OnInit } from '@angular/core';
import {
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.routes';
import { SpConfigurationTabs } from '../configuration-tabs';
import {
    AssetManagementService,
    SpAsset,
} from '@streampipes/platform-services';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { SpDataExportDialogComponent } from './export-dialog/data-export-dialog.component';
import { SpDataImportDialogComponent } from './import-dialog/data-import-dialog.component';

@Component({
    selector: 'sp-data-export-import',
    templateUrl: './data-export-import.component.html',
    styleUrls: ['./data-export-import.component.scss'],
})
export class SpDataExportImportComponent implements OnInit {
    tabs = SpConfigurationTabs.getTabs();

    assets: SpAsset[];
    selectedAssets: string[] = [];

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        private assetManagementService: AssetManagementService,
        private dialogService: DialogService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: SpConfigurationTabs.getTabs()[3].itemTitle },
        ]);
        this.loadAssets();
    }

    loadAssets(): void {
        this.assetManagementService
            .getAllAssets()
            .subscribe(
                assets =>
                    (this.assets = assets.sort((a, b) =>
                        a.assetName.localeCompare(b.assetName),
                    )),
            );
    }

    handleSelectionChange(event: MatCheckboxChange, assetId: string) {
        if (event.checked) {
            this.selectedAssets.push(assetId);
        } else {
            this.selectedAssets.splice(this.selectedAssets.indexOf(assetId), 1);
        }
    }

    openExportDialog(): void {
        const dialogRef = this.dialogService.open(SpDataExportDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Export resources',
            width: '50vw',
            data: {
                selectedAssets: this.selectedAssets,
            },
        });

        dialogRef.afterClosed().subscribe(() => {});
    }

    openImportDialog(): void {
        const dialogRef = this.dialogService.open(SpDataImportDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Import resources',
            width: '50vw',
            data: {},
        });

        dialogRef.afterClosed().subscribe(() => {});
    }
}
