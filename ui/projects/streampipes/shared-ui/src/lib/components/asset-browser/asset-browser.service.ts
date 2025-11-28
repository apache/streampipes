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

import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, zip } from 'rxjs';
import {
    AssetConstants,
    AssetManagementService,
    AssetSiteDesc,
    GenericStorageService,
    Isa95TypeDesc,
    Isa95TypeService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';
import {
    AssetBrowserData,
    AssetFilter,
    FilterResult,
} from './asset-browser.model';
import { CurrentUserService } from '../../services/current-user.service';

@Injectable({ providedIn: 'root' })
export class SpAssetBrowserService {
    assetData$ = new BehaviorSubject<AssetBrowserData>(undefined);
    expanded$ = new BehaviorSubject<boolean>(true);
    filter$ = new BehaviorSubject<AssetFilter>(undefined);
    currentAssetFilter$ = new BehaviorSubject<FilterResult>({
        filterActive: false,
        filterDisabled: true,
    });

    loadedAssetData: AssetBrowserData;
    activeAssetLink = undefined;

    private genericStorageService = inject(GenericStorageService);
    private typeService = inject(Isa95TypeService);
    private assetService = inject(AssetManagementService);
    private currentUserService = inject(CurrentUserService);

    constructor() {
        this.loadAssetData();
    }

    applyAssetLinkType(assetLink: string) {
        this.activeAssetLink = assetLink;
        if (this.filter$.getValue() !== undefined) {
            this.applyFilters(this.filter$.getValue());
        }
    }

    loadAssetData(): void {
        const assets$ = this.assetService.getAllAssets();
        const assetLinks$ = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
        );
        const sites$ = this.genericStorageService.getAllDocuments(
            AssetConstants.ASSET_SITES_APP_DOC_NAME,
        );
        const labels$ = this.genericStorageService.getAllDocuments('sp-labels');

        zip([assets$, assetLinks$, sites$, labels$]).subscribe(res => {
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
        if (
            this.activeAssetLink !== undefined &&
            this.assetData$.getValue() !== undefined
        ) {
            const data = this.assetData$.getValue();
            const filters: AssetFilter = {
                selectedSites: [...data.sites].sort((a, b) =>
                    a.label.localeCompare(b.label),
                ),
                selectedLabels: [...data.labels],
                selectedTypes: [...this.typeService.getTypeDescriptions()],
                selectedAssetModels: [...data.assets],
            };
            this.filter$.next(filters);
            this.applyFilters(filters);
        }
    }

    resetFilters(): void {
        this.assetData$.next(this.loadedAssetData);
        this.reloadFilters();
    }

    applyFilters(filter: AssetFilter) {
        const clonedLoadedAssetData = JSON.parse(
            JSON.stringify(this.loadedAssetData),
        ) as AssetBrowserData;
        const allAssetsSelected = this.allSelected(
            clonedLoadedAssetData.assets,
            filter.selectedAssetModels,
        );
        const allTypesSelected = this.allSelected(
            this.typeService.getTypeDescriptions(),
            filter.selectedTypes,
        );
        const allSitesSelected = this.allSelected(
            clonedLoadedAssetData.sites,
            filter.selectedSites,
        );
        const allLabelsSelected = this.allSelected(
            clonedLoadedAssetData.labels,
            filter.selectedLabels,
        );

        if (
            allAssetsSelected &&
            allTypesSelected &&
            allSitesSelected &&
            allLabelsSelected
        ) {
            this.currentAssetFilter$.next({
                filterActive: false,
                filterDisabled: false,
                activeElementIds: new Set<string>(),
                selectedAssets: clonedLoadedAssetData.assets,
                currentAssetLink: this.activeAssetLink,
            });
        } else {
            const filteredAssets = clonedLoadedAssetData.assets
                .filter(
                    a =>
                        allAssetsSelected ||
                        this.filterAssetModel(a, filter.selectedAssetModels),
                )
                .filter(
                    a =>
                        allTypesSelected ||
                        this.filterType(a, filter.selectedTypes),
                )
                .filter(
                    a =>
                        allSitesSelected ||
                        this.filterSites(a, filter.selectedSites),
                )
                .map(a =>
                    allLabelsSelected ||
                    this.filterLabels(a, filter.selectedLabels)
                        ? a
                        : null,
                )
                .filter((a: SpAsset | null) => a !== null); // remove nulls from the array

            this.applyAssetFilter(filteredAssets);
        }
    }

    applyAssetFilter(filteredAssets: SpAsset[]) {
        let elementIds: Set<string> | undefined = undefined;

        if (filteredAssets.length === 0) {
            elementIds = undefined;
        } else {
            elementIds = new Set<string>();
            filteredAssets.forEach(asset => {
                this.collectElementIds(asset, this.activeAssetLink, elementIds);
            });
        }
        const currentFilter = {
            filterActive: true,
            activeElementIds: elementIds,
            selectedAssets: filteredAssets,
            currentAssetLink: this.activeAssetLink,
            allAssetCount: this.assetData$.getValue().assets.length,
            selectedAssetCount: filteredAssets.length,
            filterDisabled: false,
        };
        this.currentAssetFilter$.next(currentFilter);
    }

    private allSelected(items: any[], selected: any[]) {
        return items.length === selected.length;
    }

    private filterType(
        asset: SpAsset,
        selectedTypes: Isa95TypeDesc[],
    ): SpAsset | null {
        const matchesSelf = selectedTypes.some(
            type => type.type === asset.assetType?.isa95AssetType,
        );

        if (!matchesSelf) {
            // remove asset Links not of interest
            asset.assetLinks = [];
        }

        if (!matchesSelf && !asset.assets?.length) {
            // If the asset does not match the labels at the current level and recursion is not going to match either, return null
            return null;
        }

        let filteredChildren: SpAsset[] = [];

        if (asset.assets?.length) {
            filteredChildren = asset.assets
                .map(child => ({ ...child }))
                .map(child => this.filterType(child, selectedTypes))
                .filter(child => child !== null)
                .map(child => child as SpAsset);
        }

        if (!matchesSelf && filteredChildren.length === 0) {
            return null;
        }
        asset.assets = filteredChildren;

        return asset;
    }

    private filterAssetModel(
        asset: SpAsset,
        selectedAssets: SpAsset[],
    ): boolean {
        return (
            selectedAssets.find(a => a.assetId === asset.assetId) !== undefined
        );
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

    private filterLabels(
        asset: SpAsset,
        selectedLabels: SpLabel[],
        recursionStep: boolean = false,
        previousMatchesSelf: boolean = false,
    ): SpAsset | null {
        const labelIds = asset.labelIds || [];
        const matchesSelf = selectedLabels.some(label =>
            labelIds.includes(label._id),
        );

        if (matchesSelf) {
            // If it already matches on top level --> labels are inherited
            return asset;
        }

        if (!matchesSelf && !previousMatchesSelf) {
            // remove asset Links not of interest
            asset.assetLinks = [];
        }

        if (!matchesSelf && !asset.assets?.length) {
            // If the asset does not match the labels at the current level and recursion is not going to match either, return null
            return null;
        }

        let filteredChildren: SpAsset[] = [];

        if (asset.assets?.length) {
            filteredChildren = asset.assets
                .map(child => ({ ...child }))
                .map(child =>
                    this.filterLabels(child, selectedLabels, true, matchesSelf),
                )
                .filter(child => child !== null)
                .map(child => child as SpAsset);
        }

        if (
            !matchesSelf &&
            !previousMatchesSelf &&
            filteredChildren.length === 0
        ) {
            return null;
        }
        asset.assets = filteredChildren;

        return asset;
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
        } else {
            elementIds = new Set<string>();
        }
    }

    findAssetLinkValues(asset: SpAsset, filteredLinkType: string): string[] {
        return asset.assetLinks
            .filter(a => a.linkType === filteredLinkType)
            .map(a => a.resourceId);
    }

    hasNoAssetFilterPermission(): boolean {
        return !this.currentUserService.hasAnyRole([
            'ROLE_ADMIN',
            'ROLE_ASSET_ADMIN',
            'ROLE_ASSET_USER',
        ]);
    }
}
