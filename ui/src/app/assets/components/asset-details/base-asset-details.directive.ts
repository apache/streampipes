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

import { Directive, OnInit } from '@angular/core';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import {
    AssetConstants,
    AssetSiteDesc,
    GenericStorageService,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { zip } from 'rxjs';
import { SpAssetRoutes } from '../../assets.routes';

@Directive()
export abstract class BaseAssetDetailsDirective implements OnInit {
    asset: SpAssetModel;
    sites: AssetSiteDesc[] = [];

    selectedAsset: SpAsset;
    rootNode = true;

    assetModelId: string;

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        protected genericStorageService: GenericStorageService,
        protected route: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.assetModelId = this.route.snapshot.params.assetId;
        this.loadResources();
    }

    loadResources(): void {
        const assetReq = this.genericStorageService.getDocument(
            AssetConstants.ASSET_APP_DOC_NAME,
            this.assetModelId,
        );
        const locationsReq = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_SITES_APP_DOC_NAME,
        );
        zip([assetReq, locationsReq]).subscribe(res => {
            this.asset = res[0];
            this.sites = res[1];
            if (!this.selectedAsset) {
                this.selectedAsset = this.asset;
            }
            this.breadcrumbService.updateBreadcrumb([
                SpAssetRoutes.BASE,
                { label: this.asset.assetName },
            ]);
            this.onAssetAvailable();
        });
    }

    applySelectedAsset(event: { asset: SpAsset; rootNode: boolean }): void {
        this.selectedAsset = event.asset;
        this.rootNode = event.rootNode;
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

    abstract onAssetAvailable(): void;
}
