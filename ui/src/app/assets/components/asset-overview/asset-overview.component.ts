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
import { MatTableDataSource } from '@angular/material/table';
import {
    GenericStorageService,
    SpAssetModel,
} from '@streampipes/platform-services';
import { AssetConstants } from '../../constants/asset.constants';
import {
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { SpAssetRoutes } from '../../assets.routes';
import { AssetUploadDialogComponent } from '../../dialog/asset-upload/asset-upload-dialog.component';
import { Router } from '@angular/router';
import { SpCreateAssetDialogComponent } from '../../dialog/create-asset/create-asset-dialog.component';
import { DataExportService } from '../../../configuration/export/data-export.service';
import { mergeMap } from 'rxjs/operators';
import { saveAs } from 'file-saver';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';
import { SpAssetBrowserService } from '../../../core-ui/asset-browser/asset-browser.service';

@Component({
    selector: 'sp-asset-overview',
    templateUrl: './asset-overview.component.html',
    styleUrls: ['./asset-overview.component.scss'],
})
export class SpAssetOverviewComponent implements OnInit {
    existingAssets: SpAssetModel[] = [];

    displayedColumns: string[] = ['name', 'action'];

    dataSource: MatTableDataSource<SpAssetModel> =
        new MatTableDataSource<SpAssetModel>();

    constructor(
        private genericStorageService: GenericStorageService,
        private breadcrumbService: SpBreadcrumbService,
        private dialogService: DialogService,
        private router: Router,
        private dataExportService: DataExportService,
        private idGeneratorService: IdGeneratorService,
        private assetBrowserService: SpAssetBrowserService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpAssetRoutes.BASE),
        );
        this.loadAssets();
    }

    loadAssets(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_APP_DOC_NAME)
            .subscribe(result => {
                this.existingAssets = result as SpAssetModel[];
                this.dataSource.data = this.existingAssets;
            });
    }

    createNewAsset(assetModel?: SpAssetModel) {
        if (!assetModel) {
            assetModel = {
                assetName: 'New Asset',
                assetDescription: '',
                assetLinks: [],
                assetId: this.idGeneratorService.generate(6),
                _id: this.idGeneratorService.generate(24),
                appDocType: 'asset-management',
                removable: true,
                _rev: undefined,
                assets: [],
                assetType: undefined,
            };
        }
        const dialogRef = this.dialogService.open(
            SpCreateAssetDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Create asset',
                width: '40vw',
                data: {
                    createMode: true,
                    assetModel: assetModel,
                },
            },
        );

        dialogRef.afterClosed().subscribe(() => {
            this.loadAssets();
            this.assetBrowserService.loadAssetData();
        });
    }

    uploadAsset() {
        const dialogRef = this.dialogService.open(AssetUploadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Upload asset model',
            width: '40vw',
        });

        dialogRef.afterClosed().subscribe(reload => {
            if (reload) {
                this.loadAssets();
                this.assetBrowserService.loadAssetData();
            }
        });
    }

    goToDetailsView(asset: SpAssetModel, editMode = false) {
        if (!editMode) {
            this.router.navigate(['assets', 'details', asset._id]);
        } else {
            this.router.navigate(['assets', 'details', asset._id], {
                queryParams: { editMode: editMode },
            });
        }
    }

    deleteAsset(asset: SpAssetModel) {
        this.genericStorageService
            .deleteDocument(
                AssetConstants.ASSET_APP_DOC_NAME,
                asset._id,
                asset._rev,
            )
            .subscribe(result => {
                this.loadAssets();
                this.assetBrowserService.loadAssetData();
            });
    }

    downloadAsset(asset: SpAssetModel) {
        this.dataExportService
            .getExportPreview([asset._id])
            .pipe(
                mergeMap(preview =>
                    this.dataExportService.triggerExport(preview),
                ),
            )
            .subscribe((data: Blob) => {
                const blob = new Blob([data], { type: 'application/zip' });
                saveAs(blob, 'assetExport');
            });
    }
}
