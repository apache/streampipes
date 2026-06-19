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

import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { SpAsset, SpAssetModel } from '@streampipes/platform-services';
import { NestedTreeControl } from '@angular/cdk/tree';
import {
    CdkDrag,
    CdkDragDrop,
    CdkDragHandle,
    CdkDropList,
} from '@angular/cdk/drag-drop';
import {
    MatNestedTreeNode,
    MatTree,
    MatTreeNestedDataSource,
    MatTreeNode,
    MatTreeNodeDef,
    MatTreeNodeOutlet,
    MatTreeNodeToggle,
} from '@angular/material/tree';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpBasicViewComponent } from '@streampipes/shared-ui';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { MatTooltip } from '@angular/material/tooltip';

const HOVER_EXPAND_DELAY_MS = 500;

@Component({
    selector: 'sp-asset-selection-panel',
    templateUrl: './asset-selection-panel.component.html',
    styleUrls: ['./asset-selection-panel.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        SpBasicViewComponent,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatTree,
        MatTreeNodeDef,
        MatTreeNode,
        MatTreeNodeToggle,
        MatIcon,
        MatIconButton,
        MatNestedTreeNode,
        MatTreeNodeOutlet,
        TranslatePipe,
        CdkDropList,
        CdkDrag,
        CdkDragHandle,
        MatTooltip,
    ],
})
export class SpAssetSelectionPanelComponent implements OnInit, OnDestroy {
    @Input()
    assetModel: SpAssetModel;

    @Input()
    selectedAsset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    selectedAssetEmitter: EventEmitter<{ asset: SpAsset; rootNode: boolean }> =
        new EventEmitter<{ asset: SpAsset; rootNode: boolean }>();

    treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    dataSource = new MatTreeNestedDataSource<SpAsset>();

    @ViewChild('tree') tree;

    hasChild = (_: number, node: SpAsset) =>
        this.editMode || (!!node.assets && node.assets.length > 0);

    dropTargetIds: string[] = [];
    activeDropTargetAssetId: string | undefined;
    private hoverExpandTimer: ReturnType<typeof setTimeout> | undefined;

    ngOnInit(): void {
        this.treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
        this.dataSource = new MatTreeNestedDataSource<SpAsset>();
        this.resetTree();
    }

    ngOnDestroy(): void {
        this.cancelHoverExpand();
    }

    selectNode(asset: SpAsset, rootNode: boolean) {
        this.selectedAssetEmitter.emit({ asset, rootNode });
        this.expandToAsset(asset.assetId);
    }

    addAsset(node: SpAsset) {
        this.getChildAssets(node).push(this.makeNewAsset());
        this.dataSource.data = [this.assetModel];
        this.treeControl.dataNodes = [this.assetModel];
        this.rerenderTree();
    }

    rerenderTree(): void {
        this.dataSource.data = [];
        this.dataSource.data = [this.assetModel];
        this.refreshDropListIds();
    }

    deleteAsset(node: SpAsset) {
        this.removeAssetWithId(this.assetModel.assets, node.assetId);
        this.rerenderTree();
    }

    dropAssetIntoParent(
        event: CdkDragDrop<SpAsset[]>,
        targetParent: SpAsset,
    ): void {
        this.cancelHoverExpand();
        this.activeDropTargetAssetId = undefined;
        const draggedAsset = event.item.data as SpAsset;
        let assetWasMoved = false;
        if (this.canDropAsset(draggedAsset, targetParent)) {
            const sourceAssets = this.findParentAssets(draggedAsset.assetId);
            const targetAssets = this.getChildAssets(targetParent);
            const sourceIndex = sourceAssets?.findIndex(
                asset => asset.assetId === draggedAsset.assetId,
            );

            if (sourceAssets && sourceIndex !== undefined && sourceIndex >= 0) {
                sourceAssets.splice(sourceIndex, 1);
                targetAssets.push(draggedAsset);
                assetWasMoved = true;
            }
        }

        this.rerenderTree();
        if (assetWasMoved) {
            this.expandToAsset(draggedAsset.assetId);
            if (this.selectedAsset?.assetId === draggedAsset.assetId) {
                this.selectNode(draggedAsset, this.isRootNode(draggedAsset));
            }
        }
    }

    canEnterDropList = (drag: CdkDrag, drop: CdkDropList): boolean => {
        const targetParent = this.findAssetByDropListId(drop.id);
        return this.canDropAsset(drag.data as SpAsset, targetParent);
    };

    markDropTarget(node: SpAsset): void {
        this.activeDropTargetAssetId = node.assetId;
        this.scheduleHoverExpand(node);
    }

    clearDropTarget(node: SpAsset): void {
        if (this.activeDropTargetAssetId === node.assetId) {
            this.activeDropTargetAssetId = undefined;
            this.cancelHoverExpand();
        }
    }

    canDropAsset(
        draggedAsset: SpAsset | undefined,
        targetParent: SpAsset | undefined,
    ): boolean {
        if (!draggedAsset || !targetParent) {
            return false;
        }
        const sourceAssets = this.findParentAssets(draggedAsset.assetId);
        return (
            !this.isRootNode(draggedAsset) &&
            draggedAsset.assetId !== targetParent.assetId &&
            sourceAssets !== (targetParent.assets || []) &&
            !this.isDescendantOf(draggedAsset, targetParent.assetId)
        );
    }

    getDropTargetId(node: SpAsset): string {
        return `asset-drop-target-${node.assetId}`;
    }

    getChildAssets(node: SpAsset): SpAsset[] {
        if (!node.assets) {
            node.assets = [];
        }
        return node.assets;
    }

    isRootNode(node: SpAsset): boolean {
        return node.assetId === this.assetModel.assetId;
    }

    removeAssetWithId(assets: SpAsset[], id: string) {
        for (let i = 0; i < assets.length; i++) {
            if (assets[i].assetId === id) {
                assets.splice(i, 1);
                return;
            }
            if (assets[i].assets) {
                this.removeAssetWithId(assets[i].assets, id);
            }
        }
    }

    makeNewAsset(): SpAsset {
        return {
            assetId: this.makeAssetId(),
            assetName: 'New Asset',
            assetDescription: '',
            assetLinks: [],
            assetType: undefined,
            assets: [],
            assetSite: undefined,
            labelIds: [],
            additionalData: {},
        };
    }

    makeAssetId(): string {
        return 'a' + Math.random().toString(36).substring(2, 9);
    }

    private resetTree() {
        this.dataSource.data = [this.assetModel];
        this.treeControl.dataNodes = [this.assetModel];
        this.refreshDropListIds();
        this.treeControl.expandAll();
    }

    private refreshDropListIds(): void {
        this.dropTargetIds = this.getAllAssets(this.assetModel).map(node =>
            this.getDropTargetId(node),
        );
    }

    private getAllAssets(node: SpAsset): SpAsset[] {
        return [
            node,
            ...(node.assets?.flatMap(child => this.getAllAssets(child)) || []),
        ];
    }

    private isDescendantOf(asset: SpAsset, targetAssetId: string): boolean {
        return (
            asset.assets?.some(
                child =>
                    child.assetId === targetAssetId ||
                    this.isDescendantOf(child, targetAssetId),
            ) || false
        );
    }

    private findParentAssets(assetId: string): SpAsset[] | undefined {
        return this.findParentAssetsRecursive(this.assetModel, assetId);
    }

    private findParentAssetsRecursive(
        parent: SpAsset,
        assetId: string,
    ): SpAsset[] | undefined {
        const children = parent.assets || [];
        if (children.some(child => child.assetId === assetId)) {
            return children;
        }
        for (const child of children) {
            const parentAssets = this.findParentAssetsRecursive(child, assetId);
            if (parentAssets) {
                return parentAssets;
            }
        }
        return undefined;
    }

    private findAssetByDropListId(dropListId: string): SpAsset | undefined {
        const dropTarget = this.getAllAssets(this.assetModel).find(
            node => this.getDropTargetId(node) === dropListId,
        );
        return dropTarget;
    }

    private scheduleHoverExpand(node: SpAsset): void {
        this.cancelHoverExpand();
        this.hoverExpandTimer = setTimeout(() => {
            if (this.activeDropTargetAssetId === node.assetId) {
                this.treeControl.expand(node);
            }
            this.hoverExpandTimer = undefined;
        }, HOVER_EXPAND_DELAY_MS);
    }

    private cancelHoverExpand(): void {
        if (this.hoverExpandTimer) {
            clearTimeout(this.hoverExpandTimer);
            this.hoverExpandTimer = undefined;
        }
    }

    private expandToAsset(assetId: string) {
        const path = this.findPath(this.assetModel, assetId);
        if (path) {
            path.forEach(node => this.treeControl.expand(node));
        }
    }

    private findPath(
        node: SpAsset,
        targetId: string,
        path: SpAsset[] = [],
    ): SpAsset[] | undefined {
        const currentPath = [...path, node];
        if (node.assetId === targetId) {
            return currentPath;
        }
        for (const child of node.assets || []) {
            const res = this.findPath(child, targetId, currentPath);
            if (res) {
                return res;
            }
        }
        return undefined;
    }
}
