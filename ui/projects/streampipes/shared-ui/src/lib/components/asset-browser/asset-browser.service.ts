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

import { Injectable } from '@angular/core';
import { BehaviorSubject, zip } from 'rxjs';
import {
    AssetConstants,
    AssetSiteDesc,
    GenericStorageService,
    Isa95TypeDesc,
    Isa95TypeService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';
import { AssetBrowserData, AssetFilter } from './asset-browser.model';

@Injectable({ providedIn: 'root' })
export class SpAssetBrowserService {
    assetData$ = new BehaviorSubject<AssetBrowserData>(undefined);
    expanded$ = new BehaviorSubject<boolean>(true);
    filter$ = new BehaviorSubject<AssetFilter>(undefined);

    loadedAssetData: AssetBrowserData;

    constructor(
        private genericStorageService: GenericStorageService,
        private typeService: Isa95TypeService,
    ) {
        this.loadAssetData();
    }

    loadAssetData(): void {
        const assetsReq = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_APP_DOC_NAME,
        );
        const assetLinksReq = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
        );
        const sitesReq = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_SITES_APP_DOC_NAME,
        );
        const labelsReq =
            this.genericStorageService.getAllDocuments('sp-labels');

        zip([assetsReq, assetLinksReq, sitesReq, labelsReq]).subscribe(res => {
            this.loadedAssetData = {
                assets: res[0].sort((a, b) =>
                    a.assetName.localeCompare(b.assetName),
                ),
                assetLinks: res[1],
                sites: res[2],
                labels: res[3].sort((a, b) => a.label.localeCompare(b.label)),
            };
            this.assetData$.next(this.loadedAssetData);
            this.reloadFilters();
        });
    }

    private reloadFilters(): void {
        const data = this.assetData$.getValue();
        const filters: AssetFilter = {
            selectedSites: [...data.sites].sort((a, b) =>
                a.label.localeCompare(b.label),
            ),
            selectedLabels: [],
            selectedTypes: [...this.typeService.getTypeDescriptions()],
        };
        this.filter$.next(filters);
    }

    resetFilters(): void {
        this.assetData$.next(this.loadedAssetData);
        this.reloadFilters();
    }

    applyFilters(filter: AssetFilter) {
        const clonedLoadedAssetData = JSON.parse(
            JSON.stringify(this.loadedAssetData),
        ) as AssetBrowserData;
        const filteredAssets = clonedLoadedAssetData.assets
            .filter(
                a =>
                    this.allSelected(
                        this.typeService.getTypeDescriptions(),
                        filter.selectedTypes,
                    ) || this.filterType(a, filter.selectedTypes),
            )
            .filter(
                a =>
                    this.allSelected(
                        clonedLoadedAssetData.sites,
                        filter.selectedSites,
                    ) || this.filterSites(a, filter.selectedSites),
            )
            .filter(a => this.filterLabels(a, filter.selectedLabels));

        this.assetData$.next({
            assets: filteredAssets,
            assetLinks: clonedLoadedAssetData.assetLinks,
            sites: clonedLoadedAssetData.sites,
            labels: clonedLoadedAssetData.labels,
        });
    }

    private allSelected(items: any[], selected: any[]) {
        return items.length === selected.length;
    }

    private filterType(
        asset: SpAsset,
        selectedTypes: Isa95TypeDesc[],
    ): boolean {
        const matchesSelf = selectedTypes.some(
            type => type.type === asset.assetType?.isa95AssetType,
        );

        if (asset.assets?.length) {
            asset.assets = asset.assets
                .map(a => ({ ...a }))
                .filter(a => this.filterType(a, selectedTypes));
            return matchesSelf || asset.assets.length > 0;
        }

        return matchesSelf;
    }

    private filterSites(
        asset: SpAsset,
        selectedSites: AssetSiteDesc[],
    ): boolean {
        return (
            selectedSites.find(site => asset.assetSite?.siteId === site._id) !==
            undefined
        );
    }

    private filterLabels(asset: SpAsset, selectedLabels: SpLabel[]): boolean {
        const labelIds = asset.labelIds || [];
        const matchesSelf = selectedLabels.every(label =>
            labelIds.includes(label._id),
        );

        if (asset.assets?.length) {
            asset.assets = asset.assets
                .map(a => ({ ...a }))
                .filter(a => this.filterLabels(a, selectedLabels));
            return matchesSelf || asset.assets.length > 0;
        }

        return matchesSelf;
    }

    collectElementIds(
        asset: SpAsset,
        filteredLinkType: string,
        elementIds: Set<string>,
    ): void {
        const assetLinkValues = this.findAssetLinkValues(
            asset,
            filteredLinkType,
        );
        assetLinkValues.forEach(v => elementIds.add(v));
        if (asset.assets !== undefined) {
            asset.assets.forEach((a: SpAsset) => {
                this.collectElementIds(a, filteredLinkType, elementIds);
            });
        }
    }

    findAssetLinkValues(asset: SpAsset, filteredLinkType: string): string[] {
        return asset.assetLinks
            .filter(a => a.linkType === filteredLinkType)
            .map(a => a.resourceId);
    }
}
