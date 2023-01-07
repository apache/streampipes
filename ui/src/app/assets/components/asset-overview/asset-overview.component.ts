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

@Component({
    selector: 'sp-asset-overview-component',
    templateUrl: './asset-overview.component.html',
    styleUrls: ['./asset-overview.component.scss'],
})
export class SpAssetOverviewComponent implements OnInit {
    existingAssets: SpAssetModel[] = [];

    displayedColumns: string[] = ['name', 'action'];

    dataSource: MatTableDataSource<SpAssetModel>;

    constructor(
        private genericStorageService: GenericStorageService,
        private breadcrumbService: SpBreadcrumbService,
        private dialogService: DialogService,
        private router: Router,
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
                this.dataSource = new MatTableDataSource<SpAssetModel>(
                    this.existingAssets,
                );
            });
    }

    createNewAsset(assetModel?: SpAssetModel) {
        if (!assetModel) {
            assetModel = {
                assetName: 'New Asset',
                assetDescription: '',
                assetLinks: [],
                assetId: this.generateId(6),
                _id: this.generateId(24),
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
        });
    }

    private generateId(length): string {
        const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        let result = '';
        for (let i = length; i > 0; --i) {
            result += chars[Math.round(Math.random() * (chars.length - 1))];
        }
        return result;
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
            });
    }
}
