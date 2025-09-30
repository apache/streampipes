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
    SpAsset,
} from '@streampipes/platform-services';

export interface Asset {
    assetId: string;
    assetName: string;
    assets?: Asset[];
    id: string;
    flattenPath: any[];
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
        console.log('selectedAssets', selectedAssets);
        const uniqueAssetIDsDict = this.getAssetPaths(selectedAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        console.log('rebuildDict', uniqueAssetIDsDict);

        uniqueAssetIDs.forEach(id => {
            this.assetService.getAsset(id).subscribe({
                next: current => {
                    this.currentAsset = current;

                    const links = this.buildLinks(linkageData);

                    uniqueAssetIDsDict[id].forEach(path => {
                        console.log(path);

                        if (path.length === 2) {
                            current.assetLinks = [
                                ...(current.assetLinks ?? []),
                                ...links,
                            ];
                        }
                        if (path.length > 2) {
                            console.log('UpdateDictValue');
                            console.log(path);
                            this.updateDictValue(
                                current,
                                path.splice(2),
                                links,
                            );
                        }
                    });

                    console.log(current);
                    const updateObservable =
                        this.assetService.updateAsset(current);
                    updateObservable?.subscribe({
                        next: updated => {
                            this.adapterStartedEmitter.emit();
                        },
                    });
                },
            });
        });
    }

    private updateDictValue(
        dict: SpAssetModel,
        path: (string | number)[],
        newValue: any,
    ) {
        let result: any = dict;

        console.log('p', path);

        // Iterate through the path, stopping one step before the final key
        for (let i = 0; i < path.length - 1; i++) {
            console.log('i', i);
            const key = path[i];
            result = result.assets[key];
        }
        result.assetLinks = newValue;
        console.log(result);
    }

    private getAssetPaths(apiAssets: Asset[]): {
        [key: string]: Array<Array<string | number>>;
    } {
        // Initialize a dictionary to collect arrays of flattenPath for each id
        const idPaths = {};

        // Iterate through the data and populate the dictionary
        apiAssets.forEach(item => {
            // If the item has assets, loop through them and extract their flattenPath
            item.assets.forEach(asset => {
                if (asset.id) {
                    if (!idPaths[asset.id]) {
                        idPaths[asset.id] = [];
                    }
                    idPaths[asset.id].push(asset.flattenPath);
                }
            });

            // If the item has its own id and flattenPath, add it as well
            if (item.id && item.flattenPath) {
                if (!idPaths[item.id]) {
                    idPaths[item.id] = [];
                }
                idPaths[item.id].push(item.flattenPath);
            }
        });
        return idPaths;
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
