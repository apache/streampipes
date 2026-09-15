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

import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, inject, Input } from '@angular/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatNestedTreeNode,
    MatTree,
    MatTreeNestedDataSource,
    MatTreeNode,
    MatTreeNodeDef,
    MatTreeNodeOutlet,
    MatTreeNodeToggle,
} from '@angular/material/tree';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
    AssetManagementService,
    AssetSummaryDto,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogRef } from '@streampipes/shared-ui';

export interface MoveAssetDialogResult {
    targetAsset: SpAssetModel;
    targetParentAssetId: string;
    removeMovedAssetSite: boolean;
}

@Component({
    selector: 'sp-move-asset-dialog',
    templateUrl: './move-asset-dialog.component.html',
    styleUrls: ['./move-asset-dialog.component.scss'],
    imports: [
        MatTree,
        MatTreeNodeDef,
        MatTreeNode,
        MatTreeNodeToggle,
        MatNestedTreeNode,
        MatTreeNodeOutlet,
        MatButton,
        MatIconButton,
        MatIcon,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        TranslatePipe,
    ],
})
export class MoveAssetDialogComponent {
    private dialogRef = inject<DialogRef<MoveAssetDialogComponent>>(DialogRef);
    private assetService = inject(AssetManagementService);

    readonly treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    readonly dataSource = new MatTreeNestedDataSource<SpAsset>();

    @Input()
    assetToMove: SpAsset;

    @Input()
    set availableAssets(assets: AssetSummaryDto[]) {
        this.dataSource.data = assets.map(asset =>
            this.makeRootAssetPlaceholder(asset),
        );
        this.treeControl.dataNodes = this.dataSource.data;
    }

    selectedTarget?: SpAsset;
    selectedTargetAsset?: SpAssetModel;
    private loadedRootAssetIds = new Set<string>();

    hasChild = (_: number, node: SpAsset): boolean =>
        this.isRootAsset(node) || !!node.assets?.length;

    selectTarget(asset: SpAsset): void {
        const rootAsset = this.findRootAsset(asset);
        if (!rootAsset || !this.loadedRootAssetIds.has(rootAsset.elementId)) {
            return;
        }

        this.selectedTarget = asset;
        this.selectedTargetAsset = rootAsset;
    }

    toggleAssetTree(asset: SpAsset): void {
        if (!this.isRootAsset(asset)) {
            this.treeControl.toggle(asset);
            return;
        }

        if (this.treeControl.isExpanded(asset)) {
            this.treeControl.collapse(asset);
            return;
        }

        const rootAsset = asset as SpAssetModel;
        if (this.loadedRootAssetIds.has(rootAsset.elementId)) {
            this.treeControl.expand(asset);
            return;
        }

        this.assetService
            .getAsset(rootAsset.elementId)
            .subscribe(targetAsset => {
                this.showAssetTree(targetAsset);
            });
    }

    sitesAreConsistent(): boolean {
        if (!this.selectedTargetAsset) {
            return true;
        }

        const siteIds = this.getAllAssets(this.assetToMove)
            .concat(this.getAllAssets(this.selectedTargetAsset))
            .map(asset => asset.assetSite?.siteId ?? '')
            .filter((siteId, index, ids) => ids.indexOf(siteId) === index);

        return siteIds.length <= 1;
    }

    save(): void {
        if (!this.selectedTargetAsset || !this.selectedTarget) {
            return;
        }

        this.dialogRef.close({
            targetAsset: this.selectedTargetAsset,
            targetParentAssetId: this.selectedTarget.assetId,
            removeMovedAssetSite: !this.sitesAreConsistent(),
        } satisfies MoveAssetDialogResult);
    }

    cancel(): void {
        this.dialogRef.close();
    }

    private getAllAssets(asset: SpAsset): SpAsset[] {
        return [
            asset,
            ...(asset.assets?.flatMap(child => this.getAllAssets(child)) ?? []),
        ];
    }

    private showAssetTree(asset: SpAssetModel): void {
        const rootAssetIndex = this.dataSource.data.findIndex(
            rootAsset =>
                (rootAsset as SpAssetModel).elementId === asset.elementId,
        );
        this.dataSource.data.splice(rootAssetIndex, 1, asset);
        this.dataSource.data = [...this.dataSource.data];
        this.loadedRootAssetIds.add(asset.elementId);
        this.selectedTargetAsset = asset;
        this.selectedTarget = asset;
        this.treeControl.dataNodes = this.dataSource.data;
        this.treeControl.expand(asset);
    }

    private makeRootAssetPlaceholder(asset: AssetSummaryDto): SpAssetModel {
        return {
            elementId: asset.elementId,
            assetId: asset.elementId,
            assetName: asset.assetName,
            assetDescription: asset.assetDescription,
            assets: [],
        } as SpAssetModel;
    }

    private isRootAsset(asset: SpAsset): boolean {
        return this.dataSource.data.includes(asset);
    }

    private findRootAsset(asset: SpAsset): SpAssetModel | undefined {
        return this.dataSource.data.find(rootAsset =>
            this.containsAsset(rootAsset, asset.assetId),
        ) as SpAssetModel | undefined;
    }

    private containsAsset(asset: SpAsset, assetId: string): boolean {
        return (
            asset.assetId === assetId ||
            (asset.assets?.some(child => this.containsAsset(child, assetId)) ??
                false)
        );
    }
}
