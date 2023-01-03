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
    AfterViewInit,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { SpAsset, SpAssetModel } from '@streampipes/platform-services';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';

@Component({
    selector: 'sp-asset-selection-panel-component',
    templateUrl: './asset-selection-panel.component.html',
    styleUrls: ['./asset-selection-panel.component.scss'],
})
export class SpAssetSelectionPanelComponent implements OnInit {
    @Input()
    assetModel: SpAssetModel;

    @Input()
    selectedAsset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    selectedAssetEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

    treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    dataSource = new MatTreeNestedDataSource<SpAsset>();

    @ViewChild('tree') tree;

    hasChild = (_: number, node: SpAsset) =>
        !!node.assets && node.assets.length > 0;

    ngOnInit(): void {
        this.treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
        this.dataSource = new MatTreeNestedDataSource<SpAsset>();
        this.dataSource.data = [this.assetModel];
        this.treeControl.dataNodes = [this.assetModel];
        this.treeControl.expandAll();
    }

    selectNode(asset: SpAsset) {
        this.selectedAssetEmitter.emit(asset);
    }

    addAsset(node: SpAsset) {
        if (!node.assets) {
            node.assets = [];
        }
        node.assets.push(this.makeNewAsset());
        this.dataSource.data = [this.assetModel];
        this.treeControl.dataNodes = [this.assetModel];
        this.rerenderTree();
    }

    deleteAsset(node: SpAsset) {
        this.removeAssetWithId(this.assetModel.assets, node.assetId);
        this.rerenderTree();
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

    rerenderTree(): void {
        this.dataSource.data = null;
        this.dataSource.data = [this.assetModel];
        this.treeControl.expandAll();
    }

    makeNewAsset(): SpAsset {
        return {
            assetId: this.makeAssetId(),
            assetName: 'New Asset',
            assetDescription: '',
            assetLinks: [],
            assetType: undefined,
            assets: [],
        };
    }

    makeAssetId(): string {
        return 'a' + Math.random().toString(36).substring(2, 9);
    }
}
