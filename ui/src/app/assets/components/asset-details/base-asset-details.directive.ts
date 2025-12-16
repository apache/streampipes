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

import { Directive, inject, OnInit } from '@angular/core';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import {
    AssetConstants,
    AssetManagementService,
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

    private breadcrumbService = inject(SpBreadcrumbService);
    protected route = inject(ActivatedRoute);
    protected assetService = inject(AssetManagementService);
    private genericStorageService = inject(GenericStorageService);

    ngOnInit(): void {
        this.assetModelId = this.route.snapshot.params.assetId;
        this.loadResources();
    }

    loadResources(): void {
        const assetReq = this.assetService.getAsset(this.assetModelId);
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

            if (!this.asset.assetSite.hasExactLocation) {
                const matchingSite = this.sites.find(
                    site => site._id === this.asset.assetSite.siteId,
                );
                if (matchingSite) {
                    this.asset.assetSite.location = matchingSite.location;
                }
            }

            this.onAssetAvailable();
        });
    }

    applySelectedAsset(event: { asset: SpAsset; rootNode: boolean }): void {
        this.selectedAsset = event.asset;
        this.rootNode = event.rootNode;
    }

    abstract onAssetAvailable(): void;
}
