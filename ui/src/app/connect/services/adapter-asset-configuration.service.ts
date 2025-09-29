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

import { Injectable, Output, EventEmitter } from '@angular/core';
import {
    AssetConstants,
    AssetManagementService,
    AssetLink,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    GenericStorageService,
} from '@streampipes/platform-services';

export interface Asset {
    assetId: string;
    assetName: string;
    assets?: Asset[];
    id: string;
}

@Injectable({
    providedIn: 'root',
})
export class AssetSaveService {
    assetLinkTypes: AssetLinkType[] = [];
    currentAsset: SpAssetModel;
    constructor(
        private assetService: AssetManagementService,
        private storageService: GenericStorageService,
    ) {
        this.loadAssetLinkTypes();
    }

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    saveSelectedAssets(
        selectedAssets: Asset[],
        linkageData: LinkageData[],
    ): void {
        selectedAssets.forEach(asset => {
            this.assetService.getAsset(asset.id).subscribe({
                next: current => {
                    this.currentAsset = current;

                    const links = this.buildLinks(linkageData);
                    const targetAsset = this.findAssetById(
                        asset.assetId,
                        current,
                    );

                    targetAsset.assetLinks = [
                        ...(targetAsset.assetLinks ?? []),
                        ...links,
                    ];

                    const updateObservable = targetAsset._id
                        ? this.assetService.updateAsset(targetAsset)
                        : this.updateNestedAsset(targetAsset, current);

                    updateObservable?.subscribe({
                        next: updated => {
                            this.adapterStartedEmitter.emit();
                        },
                    });
                },
            });
        });
    }
    private buildLinks(data: LinkageData[]): AssetLink[] {
        return data.map(item => {
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

    private getAssetLinkTypeById(linkType: string): AssetLinkType | undefined {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    private findAssetById(assetId: string, currentAsset: SpAssetModel): any {
        if (currentAsset?.assetId === assetId) return currentAsset;
        return this.findSubAssetById(currentAsset?.assets ?? [], assetId);
    }

    private findSubAssetById(assets: any[], assetId: string): any {
        for (const asset of assets) {
            if (asset.assetId === assetId) return asset;
            const found = this.findSubAssetById(asset.assets ?? [], assetId);
            if (found) return found;
        }
        return null;
    }

    private updateNestedAsset(assetToUpdate: any, currentAsset: SpAssetModel) {
        const index = currentAsset?.assets?.findIndex(
            (asset: any) => asset.assetId === assetToUpdate.assetId,
        );

        if (index === -1 || index === undefined) return null;

        currentAsset.assets[index] = assetToUpdate;
        this.assetService.updateAsset(currentAsset).subscribe(() => {});
    }

    private loadAssetLinkTypes(): void {
        this.storageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(linkTypes => {
                this.assetLinkTypes = linkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );
            });
    }
}
