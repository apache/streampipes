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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SpAsset, SpAssetModel } from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-selection-menu',
    templateUrl: './asset-selection-menu.component.html',
    styleUrls: ['./asset-selection-menu.component.scss'],
    standalone: false,
})
export class SpAssetSelectionMenuComponent implements OnInit {
    @Input()
    assetModel: SpAssetModel;

    @Input()
    selectedAsset: SpAsset;

    @Output()
    closeMenu = new EventEmitter<void>();

    @Output()
    assetSelected = new EventEmitter<{ asset: SpAsset; rootNode: boolean }>();

    flatAssets: { asset: SpAsset; depth: number; label: string }[] = [];

    ngOnInit() {
        this.rebuildFlatAssets();
    }

    private rebuildFlatAssets() {
        if (!this.assetModel) {
            this.flatAssets = [];
            return;
        }
        this.flatAssets = this.flattenAssets(this.assetModel);
    }

    private flattenAssets(
        asset: SpAsset,
        depth = 0,
    ): { asset: SpAsset; depth: number; label: string }[] {
        const current = [
            {
                asset,
                depth,
                label: asset.assetName,
            },
        ];

        const children =
            asset.assets?.flatMap(child =>
                this.flattenAssets(child, depth + 1),
            ) || [];

        return [...current, ...children];
    }

    selectAsset(asset: SpAsset) {
        this.assetSelected.emit({
            asset,
            rootNode: asset.assetId === this.assetModel.assetId,
        });
    }
}
