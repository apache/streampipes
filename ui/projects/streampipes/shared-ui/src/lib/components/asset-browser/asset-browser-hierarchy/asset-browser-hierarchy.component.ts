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
    inject,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { AssetBrowserData } from '../asset-browser.model';
import { NestedTreeControl } from '@angular/cdk/tree';
import { SpAsset } from '@streampipes/platform-services';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-browser-hierarchy',
    templateUrl: 'asset-browser-hierarchy.component.html',
    styleUrls: ['./asset-browser-hierarchy.component.scss'],
    standalone: false,
})
export class AssetBrowserHierarchyComponent implements OnChanges {
    translateService = inject(TranslateService);

    @Input()
    assetBrowserData: AssetBrowserData;

    @Input()
    allResourcesAlias = this.translateService.instant('Resources');

    @Input()
    assetSelectionMode = false;

    @Input()
    filteredAssetLinkType: string;

    @Input()
    hideAssetChildren = false;

    @Input()
    resourceCount = 0;

    @Output()
    selectedAssetEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

    treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
    dataSource = new MatTreeNestedDataSource<SpAsset>();

    selectedAsset: SpAsset = null;

    @ViewChild('tree') tree;

    hasChild = (_: number, node: SpAsset) =>
        // if asset selection mode is active, only show the direct root children
        !!node.assets &&
        node.assets.length > 0 &&
        (!this.assetSelectionMode || node.assetId === '_root');

    ngOnChanges(changes: SimpleChanges) {
        if (changes.assetBrowserData) {
            this.reloadTree();
        }
    }

    reloadTree(): void {
        this.treeControl = new NestedTreeControl<SpAsset>(node => node.assets);
        this.dataSource = new MatTreeNestedDataSource<SpAsset>();
        const nodes = this.makeRootNode();
        this.selectedAsset = nodes;
        this.dataSource.data = [nodes];
        this.treeControl.dataNodes = [nodes];
        this.treeControl.expandAll();
    }

    selectNode(asset: SpAsset) {
        this.selectedAssetEmitter.emit(asset);
        this.selectedAsset = asset;
    }

    makeRootNode(): SpAsset {
        return {
            assetId: '_root',
            assetName: this.translateService.instant(
                'All {{allResourcesAlias}}',
                { allResourcesAlias: this.allResourcesAlias },
            ),
            assetDescription: '',
            assetLinks: [],
            assets: this.makeAssets(),
            assetType: undefined,
            assetSite: undefined,
            additionalData: {},
            labelIds: [],
        };
    }

    private cloneWithoutChildren(assets: SpAsset[]): SpAsset[] {
        return assets.map(a => ({
            ...a,
            assets: [],
        }));
    }

    makeAssets(): SpAsset[] {
        return this.hideAssetChildren
            ? this.cloneWithoutChildren(this.assetBrowserData.assets)
            : this.assetBrowserData.assets;
    }
}
