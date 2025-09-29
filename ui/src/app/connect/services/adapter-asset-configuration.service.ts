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

    // Method to save multiple assets and handle asset linkages
    saveSelectedAssets(
        selectedAssets: Asset[],
        linkageData: LinkageData[],
    ): Observable<any>[] {
        const updateObservables: Observable<any>[] = [];
        console.log('selected assets', selectedAssets);

        // Loop through each selected asset and create the observables
        selectedAssets.forEach(asset => {
            const updateObservable = this.assetService
                .getAsset(asset.assetId)
                .pipe(
                    map(current => {
                        this.currentAsset = current; // Store the current asset
                        const links = this.buildLinks(linkageData); // Build linkage data for this asset

                        // Find target asset based on assetId
                        const targetAsset = this.findAssetById(
                            asset.assetId,
                            this.currentAsset,
                        );

                        if (!targetAsset) {
                            console.warn('Asset not found:', asset.assetId);
                            return null; // No need to process further if asset not found
                        }

                        targetAsset.assetLinks = [
                            ...(targetAsset.assetLinks ?? []),
                            ...links,
                        ];

                        // Prepare the observable for updating the asset
                        return targetAsset._id
                            ? this.assetService.updateAsset(targetAsset) // If asset has _id, update it
                            : this.updateNestedAsset(
                                  targetAsset,
                                  this.currentAsset,
                              ); // If no _id, update the nested asset
                    }),
                    catchError(error => {
                        console.error('Error fetching asset:', error);
                        return of(null); // In case of error, return an observable of null
                    }),
                );

            // Push the update observable for this asset to the list
            updateObservables.push(updateObservable);
        });

        return updateObservables; // Return the array of observables after the loop
    }

    // Helper function to build links based on the linkage data
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

    // Helper function to get asset link type by ID
    private getAssetLinkTypeById(linkType: string): AssetLinkType | undefined {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    // Helper function to find the asset by ID
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

    // Function to update a nested asset (if no _id is available)
    private updateNestedAsset(
        assetToUpdate: any,
        currentAsset: SpAssetModel,
    ): Observable<SpAssetModel> | null {
        const index = currentAsset?.assets?.findIndex(
            (asset: any) => asset.assetId === assetToUpdate.assetId,
        );

        if (index === -1 || index === undefined) return null;

        currentAsset.assets[index] = assetToUpdate;
        return this.assetService.updateAsset(currentAsset);
    }
}
