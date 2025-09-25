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
    assets?: Asset[];
    id: string;
}

@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements AfterViewInit {
    @Input() adapterDescription: AdapterDescription;
    @Input() linkageData: LinkageData[] = [];
    @Input() stepper: MatStepper;

    assetsData: Asset[] = [];
    selectedAssetIds = { id: '', assetId: '' };
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;

    constructor(
        private assetService: AssetManagementService,
        private storageService: GenericStorageService,
    ) {}

    ngAfterViewInit(): void {
        this.loadAssets();
        this.loadAssetLinkTypes();
    }

    private loadAssets(): void {
        this.assetService.getAllAssets().subscribe({
            next: assets => {
                this.assetsData = this.mapAssets(assets);
            },
        });
    }

    private loadAssetLinkTypes(): void {
        this.storageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(linkTypes => {
                this.assetLinkTypes = linkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );
                this.assetLinksLoaded = true;
            });
    }

    private mapAssets(apiAssets: any[], parentId: string = ''): Asset[] {
        return apiAssets.map(asset => ({
            id: parentId || asset._id,
            assetId: asset.assetId,
            assetName: asset.assetName,
            assets: asset.assets
                ? this.mapAssets(asset.assets, parentId || asset._id)
                : [],
        }));
    }

    private getAssetLinkTypeById(linkType: string): AssetLinkType | undefined {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    private buildLinks(data: LinkageData[]): AssetLink[] {
        return data
            .filter(item => item.selected)
            .map(item => {
                const linkType = this.getAssetLinkTypeById(item.type);
                return {
                    linkLabel: item.name,
                    linkType: item.type,
                    editingDisabled: false,
                    queryHint: item.type,
                    navigationActive: linkType?.navigationActive ?? false,
                    resourceId: item.id,
                };
            });
    }

    private findAssetById(assetId: string): any {
        if (this.currentAsset?.assetId === assetId) return this.currentAsset;
        return this.findSubAssetById(this.currentAsset?.assets ?? [], assetId);
    }

    private findSubAssetById(assets: any[], assetId: string): any {
        for (const asset of assets) {
            if (asset.assetId === assetId) return asset;
            const found = this.findSubAssetById(asset.assets ?? [], assetId);
            if (found) return found;
        }
        return null;
    }

    onCheckboxChange(component: any): void {
        if (!component.selected) component.name = '';
    }

    save(): void {
        this.assetService.getAsset(this.selectedAssetIds.id).subscribe({
            next: current => {
                this.currentAsset = current;

                const links = this.buildLinks(this.linkageData);
                const targetAsset = this.findAssetById(
                    this.selectedAssetIds.assetId,
                );

                if (!targetAsset) return;

                targetAsset.assetLinks = [
                    ...(targetAsset.assetLinks ?? []),
                    ...links,
                ];

                const updateObservable = targetAsset._id
                    ? this.assetService.updateAsset(targetAsset)
                    : this.updateNestedAsset(targetAsset);

                updateObservable?.subscribe({
                    next: updated => {
                        console.log('Asset updated:', updated);
                    },
                });
            },
        });
    }

    private updateNestedAsset(assetToUpdate: any) {
        const index = this.currentAsset?.assets?.findIndex(
            (asset: any) => asset.assetId === assetToUpdate.assetId,
        );

        if (index === -1 || index === undefined) return null;

        this.currentAsset.assets[index] = assetToUpdate;
        return this.assetService.updateAsset(this.currentAsset);
    }
}
