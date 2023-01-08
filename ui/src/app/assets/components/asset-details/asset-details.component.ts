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
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { ActivatedRoute } from '@angular/router';
import { AssetConstants } from '../../constants/asset.constants';
import {
    GenericStorageService,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { SpAssetRoutes } from '../../assets.routes';

@Component({
    selector: 'sp-asset-details-component',
    templateUrl: './asset-details.component.html',
    styleUrls: ['./asset-details.component.scss'],
})
export class SpAssetDetailsComponent implements OnInit {
    asset: SpAssetModel;

    selectedAsset: SpAsset;

    editMode: boolean;

    assetModelId: string;

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        private genericStorageService: GenericStorageService,
        private route: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.assetModelId = this.route.snapshot.params.assetId;
        this.editMode = this.route.snapshot.queryParams.editMode;
        this.loadAsset();
    }

    loadAsset(): void {
        this.genericStorageService
            .getDocument(AssetConstants.ASSET_APP_DOC_NAME, this.assetModelId)
            .subscribe(asset => {
                this.asset = asset;
                if (!this.selectedAsset) {
                    this.selectedAsset = this.asset;
                }
                this.breadcrumbService.updateBreadcrumb([
                    SpAssetRoutes.BASE,
                    { label: this.asset.assetName },
                ]);
            });
    }

    updateAsset() {
        this.updateSelected();
    }

    saveAsset() {
        this.genericStorageService
            .updateDocument(AssetConstants.ASSET_APP_DOC_NAME, this.asset)
            .subscribe(res => {
                this.loadAsset();
                this.editMode = false;
            });
    }

    updateSelected() {
        if (this.asset.assetId === this.selectedAsset.assetId) {
            this.asset = this.selectedAsset as SpAssetModel;
        } else {
            this.asset.assets.forEach(a => {
                this.walk(a, this.selectedAsset);
            });
        }
    }

    walk(asset: SpAsset, selectedAsset: SpAsset) {
        if (asset.assetId === selectedAsset.assetId) {
            asset = selectedAsset;
        } else {
            if (asset.assets) {
                asset.assets.forEach(a => {
                    this.walk(a, selectedAsset);
                });
            }
        }
    }
}
