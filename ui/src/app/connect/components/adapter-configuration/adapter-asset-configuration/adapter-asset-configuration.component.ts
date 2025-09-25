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

import { AfterViewInit, Component, Input } from '@angular/core';

import {
    AssetConstants,
    AdapterDescription,
    AssetManagementService,
    AssetLink,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    GenericStorageService,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';

export interface Asset {
    assetId: string;
    assetName: string;
    assets?: Asset[]; // Sub-assets
    id: string;
}
@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements AfterViewInit {
    /**
     * Adapter description the selected format is added to
     */
    assetsData: Asset[] = [];
    selectedAssetIds: { id: string; assetId: string } = { id: '', assetId: '' };
    currentAsset: SpAssetModel;

    assetLinkTypes: AssetLinkType[];
    assetLinksLoaded = false;
    path_to_asset = [];

    @Input() adapterDescription: AdapterDescription;

    @Input() linkageData: LinkageData[];

    @Input() stepper: MatStepper;

    constructor(
        private assetManagementService: AssetManagementService,
        private genericStorageService: GenericStorageService,
    ) {}

    ngAfterViewInit(): void {
        console.log('SAVE');
        this.getAssets();
        this.getAssetLinks();
    }

    getAssetLinks(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(assetLinkTypes => {
                this.assetLinkTypes = assetLinkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );

                console.log(this.assetLinkTypes);
                this.assetLinksLoaded = true;
            });
    }

    getAssets(): void {
        this.assetManagementService.getAllAssets().subscribe({
            next: data => {
                this.assetsData = this.transformAssetsData(data);
            },
        });
    }

    onCheckboxChange(component: any) {
        if (!component.selected) {
            // Optionally reset the name if checkbox is unchecked (this is optional)
            component.name = '';
        }
    }

    getCurrAssetLinkType(linkType: string): AssetLinkType {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    makeLink(linkageData: LinkageData[]): AssetLink[] {
        const links: AssetLink[] = [];

        // Loop through each item in linkageData
        for (const item of linkageData) {
            if (item.selected) {
                const linkType = this.getCurrAssetLinkType(item.type);
                console.log('Link Type', linkType);
                links.push({
                    linkLabel: item.name,
                    linkType: item.type,
                    editingDisabled: false,
                    queryHint: item.type,
                    navigationActive: linkType.navigationActive,
                    resourceId: item.id,
                });
            }
        }

        return links;
    }

    assignToAssets(linkageData: LinkageData) {
        // Take tthe original data
        // add the links
        //This is the right endpoint
        //this.assetManagementService.updateAsset(asset)
    }

    save(): void {
        console.log('Currently selected Asset ID:', this.selectedAssetIds);
        // Set current Asset
        this.assetManagementService
            .getAsset(this.selectedAssetIds.id)
            .subscribe({
                next: data => {
                    this.currentAsset = data;
                    console.log('Current Asset', this.currentAsset);

                    const links = this.makeLink(this.linkageData);

                    console.log('Links', links);
                    console.log('assetID', this.selectedAssetIds.assetId);
                    const asset_new = this.findAssetById(
                        this.selectedAssetIds.assetId,
                    );
                    console.log('asset_new ', asset_new);
                    for (const link of links) {
                        asset_new.assetLinks.push(link);
                    }
                    console.log('asset_new', asset_new);
                    this.assetManagementService
                        .updateAsset(asset_new)
                        .subscribe({
                            next: data => {
                                console.log(data);
                            },
                        });
                },
            });

        // Add Links
    }

    findAssetById(assetId: string): any {
        if (this.currentAsset.assetId === assetId) {
            return this.currentAsset;
        }
        return this.findSubAssetById(this.currentAsset.assets || [], assetId);
    }

    findSubAssetById(assets: any[], assetId: string): any {
        for (const asset of assets) {
            if (asset.assetId === assetId) {
                return asset;
            }
            if (asset.assets?.length) {
                const found = this.findSubAssetById(asset.assets, assetId);
                if (found) {
                    return found;
                }
            }
        }
        return null;
    }

    transformAssetsData(apiResponse: any[], topLevelId: string = ''): Asset[] {
        if (topLevelId === '') {
            return apiResponse.map(asset => ({
                id: asset._id,
                assetId: asset.assetId,
                assetName: asset.assetName,
                assets: asset.assets
                    ? this.transformAssetsData(asset.assets, asset._id)
                    : [],
            }));
        } else {
            return apiResponse.map(asset => ({
                id: topLevelId,
                assetId: asset.assetId,
                assetName: asset.assetName,
                assets: asset.assets
                    ? this.transformAssetsData(asset.assets, topLevelId)
                    : [],
            }));
        }
    }
}
