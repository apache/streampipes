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

import { EventEmitter, inject, Injectable, Output } from '@angular/core';
import {
    AssetConstants,
    AssetLink,
    AssetLinkType,
    AssetManagementService,
    GenericStorageService,
    LinkageData,
    SpAssetModel,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { firstValueFrom } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class AssetSaveService {
    assetLinkTypes: AssetLinkType[] = [];
    currentAsset: SpAssetModel;

    private assetService = inject(AssetManagementService);
    private storageService = inject(GenericStorageService);

    constructor() {
        this.loadAssetLinkTypes();
    }

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    async saveSelectedAssets(
        selectedAssets: SpAssetTreeNode[],
        linkageData: LinkageData[],
        deselectedAssets: SpAssetTreeNode[] = [],
        originalAssets: SpAssetTreeNode[] = [],
    ): Promise<void> {
        const links = this.buildLinks(linkageData);

        if (deselectedAssets.length > 0) {
            await this.deleteLinkOnDeselectAssets(deselectedAssets, links);
        }
        if (selectedAssets.length > 0) {
            await this.setLinkOnSelectAssets(selectedAssets, links);
        }

        if (originalAssets.length > 0) {
            //filter is necessary, otherwise conflicting database instances are produced
            const filteredOriginal = this.filterAssets(
                originalAssets,
                deselectedAssets,
                selectedAssets,
            );

            if (filteredOriginal.length > 0) {
                this.renameLinkage(filteredOriginal, links);
            }
        }
    }
    private filterAssets(
        originalAssets: SpAssetTreeNode[],
        deselectedAssets: SpAssetTreeNode[],
        selectedAssets: SpAssetTreeNode[],
    ): SpAssetTreeNode[] {
        const deselectedAssetIds = new Set(
            deselectedAssets.map(asset => asset.assetId),
        );
        const selectedAssetIds = new Set(
            selectedAssets.map(asset => asset.assetId),
        );

        return originalAssets.filter(
            asset =>
                !deselectedAssetIds.has(asset.assetId) &&
                !selectedAssetIds.has(asset.assetId),
        );
    }

    renameLinkage(originalAssets, links) {
        const uniqueAssetIDsDict = this.getAssetPaths(originalAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        uniqueAssetIDs.forEach(spAssetModelId => {
            this.assetService.getAsset(spAssetModelId).subscribe({
                next: current => {
                    this.currentAsset = current;

                    uniqueAssetIDsDict[spAssetModelId].forEach(path => {
                        if (path.length === 2) {
                            current.assetLinks = (current.assetLinks ?? []).map(
                                (link: any) => {
                                    const matchedLink = links.find(
                                        l => l.resourceId === link.resourceId,
                                    );
                                    if (matchedLink) {
                                        link.linkLabel = matchedLink.linkLabel;
                                    }
                                    return link;
                                },
                            );
                        }

                        if (path.length > 2) {
                            links.forEach(linkToUpdate => {
                                this.updateLinkLabelInDict(
                                    current as unknown as SpAssetTreeNode,
                                    path,
                                    linkToUpdate,
                                );
                            });
                        }
                    });

                    const updateObservable =
                        this.assetService.updateAsset(current);

                    updateObservable?.subscribe({
                        next: () => {
                            this.adapterStartedEmitter.emit();
                        },
                    });
                },
            });
        });
    }

    private updateLinkLabelInDict(
        dict: SpAssetTreeNode,
        path: (string | number)[],
        linkToUpdate: any,
    ) {
        let current = dict;

        for (let i = 2; i < path.length; i++) {
            const key = path[i];
            if (i === path.length - 1) {
                if (current.assets?.[key]?.assetLinks) {
                    current.assets[key].assetLinks = current.assets[
                        key
                    ].assetLinks.map((link: any) => {
                        if (link.resourceId === linkToUpdate.resourceId) {
                            link.linkLabel = linkToUpdate.linkLabel;
                        }
                        return link;
                    });
                }
            } else {
                if (Array.isArray(current.assets)) {
                    current = current.assets[key as number];
                }
            }
        }

        return current;
    }
    async setLinkOnSelectAssets(
        selectedAssets: SpAssetTreeNode[],
        links: AssetLink[],
    ): Promise<void> {
        const uniqueAssetIDsDict = this.getAssetPaths(selectedAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        for (const spAssetModelId of uniqueAssetIDs) {
            const current = await firstValueFrom(
                this.assetService.getAsset(spAssetModelId),
            );

            uniqueAssetIDsDict[spAssetModelId].forEach(path => {
                if (path.length === 2) {
                    current.assetLinks = [
                        ...(current.assetLinks ?? []),
                        ...links,
                    ];
                }

                if (path.length > 2) {
                    this.updateDictValue(current, path, links);
                }
            });

            const updateObservable = this.assetService.updateAsset(current);
            await firstValueFrom(updateObservable); // Ensure this completes before continuing
        }
    }

    async deleteLinkOnDeselectAssets(
        deselectedAssets: SpAssetTreeNode[],
        links: AssetLink[],
    ): Promise<void> {
        const uniqueAssetIDsDict = this.getAssetPaths(deselectedAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        for (const spAssetModelId of uniqueAssetIDs) {
            const current = await firstValueFrom(
                this.assetService.getAsset(spAssetModelId),
            );

            uniqueAssetIDsDict[spAssetModelId].forEach(path => {
                if (path.length === 2) {
                    current.assetLinks = (current.assetLinks ?? []).filter(
                        (link: any) =>
                            !links.some(
                                l =>
                                    JSON.stringify(l.resourceId) ===
                                    JSON.stringify(link.resourceId),
                            ),
                    );
                }

                if (path.length > 2) {
                    links.forEach(linkToRemove => {
                        this.deleteDictValue(
                            current as unknown as SpAssetTreeNode,
                            path,
                            linkToRemove,
                        );
                    });
                }
            });

            const updateObservable = this.assetService.updateAsset(current);
            await firstValueFrom(updateObservable); // Ensure this completes before continuing
        }
    }

    private deleteDictValue(
        dict: SpAssetTreeNode,
        path: (string | number)[],
        linkToRemove: any,
    ) {
        let current = dict;

        for (let i = 2; i < path.length; i++) {
            const key = path[i];
            if (i === path.length - 1) {
                if (current.assets?.[key]?.assetLinks) {
                    current.assets[key].assetLinks = current.assets[
                        key
                    ].assetLinks.filter(
                        (link: any) =>
                            JSON.stringify(link.resourceId) !==
                            JSON.stringify(linkToRemove.resourceId),
                    );
                }
            } else {
                if (Array.isArray(current.assets)) {
                    current = current.assets[key as number];
                }
            }
        }

        return current;
    }

    private updateDictValue(
        dict: SpAssetModel,
        path: (string | number)[],
        newValue: any,
    ) {
        const result: any = { ...dict };
        let current = result;
        for (let i = 2; i < path.length; i++) {
            const key = path[i];

            if (i === path.length - 1) {
                current.assets[key].assetLinks = [
                    ...(current.assets[key].assetLinks ?? []),
                    ...newValue,
                ];

                break;
            }

            if (Array.isArray(current.assets)) {
                parent = current;
                current = { ...current.assets[key as number] };
            }
        }

        return result;
    }

    private getAssetPaths(apiAssets: SpAssetTreeNode[]): {
        [key: string]: Array<Array<string | number>>;
    } {
        const idPaths = {};
        apiAssets.forEach(item => {
            if (item.spAssetModelId && item.flattenPath) {
                if (!idPaths[item.spAssetModelId]) {
                    idPaths[item.spAssetModelId] = [];
                }
                idPaths[item.spAssetModelId].push(item.flattenPath);
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
