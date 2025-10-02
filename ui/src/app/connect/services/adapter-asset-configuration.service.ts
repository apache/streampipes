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
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { concatMap, from, Observable } from 'rxjs';

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
        selectedAssets: SpAssetTreeNode[],
        linkageData: LinkageData[],
        deselectedAssets: SpAssetTreeNode[] = [],
        originalAssets: SpAssetTreeNode[] = [],
    ): void {
        const links = this.buildLinks(linkageData);
        console.log('Select', originalAssets);
        if (originalAssets.length > 0) {
            this.renameLinkage(originalAssets, links);
        }
        if (deselectedAssets.length > 0) {
            this.deleteLinkOnDeselectAssets(deselectedAssets, links).subscribe({
                next: () => {
                    console.log('Begin setting');
                    this.setLinkOnSelectAssets(selectedAssets, links);
                },
            });
        } else if (selectedAssets.length > 0) {
            this.setLinkOnSelectAssets(selectedAssets, links);
        }
    }

    private renameLinkage(originalAssets: SpAssetTreeNode[], links: any[]) {
        console.log('renameLinkage');
        console.log('original Assets', originalAssets);
        console.log('Links', links);

        this.deleteLinkOnDeselectAssets(originalAssets, links).subscribe({
            next: () => {
                console.log('Begin setting');
                this.setLinkOnSelectAssets(originalAssets, links);
            },
        });

        //console.log('Begin setting')

        //this.setLinkOnSelectAssets(originalAssets,links)
    }

    private setLinkOnSelectAssets(selectedAssets, links) {
        const uniqueAssetIDsDict = this.getAssetPaths(selectedAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        uniqueAssetIDs.forEach(spAssetModelId => {
            this.assetService.getAsset(spAssetModelId).subscribe({
                next: current => {
                    this.currentAsset = current;

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

    private deleteLinkOnDeselectAssets(deselectedAssets, links) {
        const uniqueAssetIDsDict = this.getAssetPaths(deselectedAssets);
        const uniqueAssetIDs = Object.keys(uniqueAssetIDsDict);

        return new Observable<void>(observer => {
            const deleteObservables = uniqueAssetIDs.map(spAssetModelId => {
                return this.assetService.getAsset(spAssetModelId).pipe(
                    concatMap(current => {
                        this.currentAsset = current;
                        uniqueAssetIDsDict[spAssetModelId].forEach(path => {
                            if (path.length === 2) {
                                current.assetLinks = (
                                    current.assetLinks ?? []
                                ).filter(
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
                                        current,
                                        path,
                                        linkToRemove,
                                    );
                                });
                            }
                        });

                        console.log('Delete Update Asset Started');
                        const updateObservable =
                            this.assetService.updateAsset(current);
                        console.log('Delete Update Asset Ended');
                        return updateObservable ?? new Observable();
                    }),
                );
            });

            from(deleteObservables)
                .pipe(concatMap(obs => obs))
                .subscribe({
                    next: () => {
                        observer.next();
                    },
                    error: err => {
                        observer.error(err);
                    },
                    complete: () => {
                        observer.complete();
                    },
                });
        });
    }

    private deleteDictValue(
        dict: any,
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
        let parent: any = null;
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
