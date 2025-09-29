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
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {
    AssetConstants,
    AdapterDescription,
    AssetManagementService,
    AssetLink,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    GenericStorageService,
    PipelineElementAssetService,
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
    constructor(private assetService: AssetManagementService) {}

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    // Method to save multiple assets and handle asset linkages
    saveSelectedAssets(
        selectedAssets: Asset[],
        linkageData: LinkageData[],
    ): void {
        //const updateObservables: Observable<any>[] = [];
        console.log('selected assets', selectedAssets);
        console.log('linkage Data', linkageData);

        // Loop through each selected asset and create the observables
        selectedAssets.forEach(asset => {
            this.assetService.getAsset(asset.id).subscribe({
                next: current => {
                    this.currentAsset = current;

                    const links = this.buildLinks(linkageData);
                    console.log(links);
                    const targetAsset = this.findAssetById(
                        asset.assetId,
                        current,
                    );

                    targetAsset.assetLinks = [
                        ...(targetAsset.assetLinks ?? []),
                        ...links,
                    ];

                    console.log(targetAsset);
                    const updateObservable = targetAsset._id
                        ? this.assetService.updateAsset(targetAsset)
                        : this.updateNestedAsset(targetAsset, current);

                    console.log(updateObservable);

                    updateObservable?.subscribe({
                        next: updated => {
                            console.log('Updated ASSER', updated);
                            this.adapterStartedEmitter.emit();
                        },
                    });
                },
            });

            // Push the update observable for this asset to the list
            //updateObservables.push(updateObservable);
        });

        //return updateObservables; // Return the array of observables after the loop
    }

    // Helper function to build links based on the linkage data
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

    // Helper function to get asset link type by ID
    private getAssetLinkTypeById(linkType: string): AssetLinkType | undefined {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    // Helper function to find the asset by ID
    private findAssetById(assetId: string, currentAsset: SpAssetModel): any {
        console.log('assetID', assetId);
        console.log('current', currentAsset);
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

    // Function to update a nested asset (if no _id is available)
    private updateNestedAsset(assetToUpdate: any, currentAsset: SpAssetModel) {
        const index = currentAsset?.assets?.findIndex(
            (asset: any) => asset.assetId === assetToUpdate.assetId,
        );

        if (index === -1 || index === undefined) return null;

        currentAsset.assets[index] = assetToUpdate;
        console.log('update asset');
        this.assetService.updateAsset(currentAsset).subscribe(() => {
            console.log('Asset 2');
        });
    }
}
