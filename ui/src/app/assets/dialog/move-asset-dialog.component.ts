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
import { Component, inject } from '@angular/core';
import {
    MAT_DIALOG_DATA,
    MatDialogActions,
    MatDialogContent,
    MatDialogRef,
    MatDialogTitle,
} from '@angular/material/dialog';
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

export interface MoveAssetDialogData {
    assetToMove: SpAsset;
    availableAssets: AssetSummaryDto[];
}

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
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        TranslatePipe,
    ],
})
export class MoveAssetDialogComponent {
    private dialogRef = inject(MatDialogRef<MoveAssetDialogComponent>);
    private assetService = inject(AssetManagementService);
    readonly data = inject<MoveAssetDialogData>(MAT_DIALOG_DATA);

    readonly treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    readonly dataSource = new MatTreeNestedDataSource<SpAsset>();

    selectedTarget?: SpAsset;
    selectedTargetAsset?: SpAssetModel;

    hasChild = (_: number, node: SpAsset): boolean => !!node.assets?.length;

    openAsset(asset: AssetSummaryDto): void {
        this.assetService.getAsset(asset.elementId).subscribe(targetAsset => {
            this.showAssetTree(targetAsset);
        });
    }

    selectTarget(asset: SpAsset): void {
        this.selectedTarget = asset;
    }

    sitesAreConsistent(): boolean {
        if (!this.selectedTargetAsset) {
            return true;
        }

        const siteIds = this.getAllAssets(this.data.assetToMove)
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
        this.selectedTargetAsset = asset;
        this.selectedTarget = undefined;
        this.dataSource.data = [asset];
        this.treeControl.dataNodes = [asset];
        this.treeControl.expandAll();
    }
}
