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
    selector: 'sp-asset-link-configuration',
    templateUrl: './asset-link-configuration.component.html',
    styleUrls: ['./asset-link-configuration.component.scss'],
    standalone: false,
})
export class AssetLinkConfigurationComponent implements OnInit {
    @Input() linkageData: LinkageData[] = [];
    @Input() stepper: MatStepper;
    @Input() isEdit: boolean;
    @Input() itemId: unknown;

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @Output() selectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() deselectedAssetsChange = new EventEmitter<SpAssetTreeNode[]>();
    @Output() originalAssetsEmitter = new EventEmitter<SpAssetTreeNode[]>();

    treeControl: NestedTreeControl<SpAssetTreeNode>;
    dataSource: MatTreeNestedDataSource<SpAssetTreeNode>;

    treeDropdownOpen = false;

    assetsData: SpAssetTreeNode[] = [];
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;
    updateObservable: Observable<SpAssetModel>;
    selectedAssets: SpAssetTreeNode[] = [];
    deselectedAssets: SpAssetTreeNode[] = [];
    originalAssets: SpAssetTreeNode[] = [];

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

        const index_deselected = this.deselectedAssets.findIndex(
            asset => asset.assetId === node.assetId,
        );

        if (index > -1) {
            this.selectedAssets.splice(index, 1);
            if (this.isNodeInOriginalData(node)) {
                this.deselectedAssets.push(node);
            }
        } else {
            this.selectedAssets.push(node);
            if (index_deselected > -1) {
                this.deselectedAssets.splice(index_deselected, 1);
            }
        }

        const selectEmit = this.selectedAssets.filter(
            node => !this.isNodeInOriginalData(node),
        );

        this.selectedAssetsChange.emit(selectEmit);
        this.deselectedAssetsChange.emit(this.deselectedAssets);
    }

    private isNodeInOriginalData(node: SpAssetTreeNode): boolean {
        for (const asset of this.originalAssets) {
            if (
                asset.assetId === node.assetId &&
                asset.spAssetModelId === node.spAssetModelId
            ) {
                return true;
            }
        }
        return false;
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
                if (this.isEdit) {
                    this.setSelect();
                }
            },
        });
    }

    private setSelect() {
        if (!this.itemId) {
            return;
        }

        this.assetsData.forEach(node => {
            this.selectNodeIfMatch(node);
        });
    }

    private selectNodeIfMatch(
        node: SpAssetTreeNode,
        path: SpAssetTreeNode[] = [],
    ) {
        const currentPath = [...path, node];

        if (
            node.assetLinks &&
            node.assetLinks.some(link => link.resourceId === this.itemId)
        ) {
            if (!this.isSelected(node)) {
                this.selectedAssets.push(node);
                this.originalAssets.push(node);
                this.originalAssetsEmitter.emit(this.originalAssets);
                currentPath.forEach(n => this.treeControl.expand(n));
            }
        }

        if (node.assets) {
            node.assets.forEach(child =>
                this.selectNodeIfMatch(child, currentPath),
            );
        }
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
                assetLinks: asset.assetLinks,
                assets: asset.assets
                    ? this.mapAssets(asset.assets, parentId, flattenedPathCopy)
                    : [],
            };
        });
    }
}
