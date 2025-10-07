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

import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import {
    AssetManagementService,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    SpAsset,
    SpAssetTreeNode,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements OnInit {
    @Input() linkageData: LinkageData[] = [];
    @Input() stepper: MatStepper;

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @Output() selectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();

    treeControl: NestedTreeControl<SpAssetTreeNode>;
    dataSource: MatTreeNestedDataSource<SpAssetTreeNode>;

    treeDropdownOpen = false;

    assetsData: SpAssetTreeNode[] = [];
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;
    updateObservable: Observable<SpAssetModel>;
    selectedAssets: SpAssetTreeNode[] = [];

    constructor(private assetService: AssetManagementService) {
        this.treeControl = new NestedTreeControl<SpAssetTreeNode>(
            node => node.assets,
        );
        this.dataSource = new MatTreeNestedDataSource<SpAssetTreeNode>();
    }

    hasChild = (_: number, node: any) =>
        !!node.assets && node.assets.length > 0;

    toggleTreeDropdown() {
        this.treeDropdownOpen = !this.treeDropdownOpen;
    }

    onAssetSelect(node: SpAssetTreeNode): void {
        const index = this.selectedAssets.findIndex(
            asset => asset.assetId === node.assetId,
        );

        if (index > -1) {
            this.selectedAssets.splice(index, 1);
        } else {
            this.selectedAssets.push(node);
        }

        this.selectedAssetsChange.emit(this.selectedAssets);
    }

    isSelected(node: SpAssetTreeNode): boolean {
        return this.selectedAssets.some(
            asset => asset.assetId === node.assetId,
        );
    }

    ngOnInit(): void {
        this.loadAssets();
    }

    private loadAssets(): void {
        this.assetService.getAllAssets().subscribe({
            next: assets => {
                this.assetsData = this.mapAssets(assets);
                this.dataSource.data = this.assetsData;
            },
        });
    }
    private mapAssets(
        apiAssets: SpAsset[],
        parentId: string = '',
        index: any[] = [],
    ): SpAssetTreeNode[] {
        return apiAssets.map((asset, assetIndex) => {
            const currentPath = [...index, assetIndex];
            let flattenedPath = [];

            if (asset['_id']) {
                parentId = asset['_id'];
                flattenedPath = [parentId, ...currentPath];
            } else {
                flattenedPath = [...currentPath];
            }
            const flattenedPathCopy = [...flattenedPath];
            return {
                spAssetModelId: parentId,
                assetId: asset.assetId,
                assetName: asset.assetName,
                flattenPath: flattenedPath,
                assets: asset.assets
                    ? this.mapAssets(asset.assets, parentId, flattenedPathCopy)
                    : [],
            };
        });
    }
}
