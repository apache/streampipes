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
    Asset,
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

    @Input() selectedAssets: Asset[] = [];
    @Output() selectedAssetsChange = new EventEmitter<Asset[]>();

    treeControl: NestedTreeControl<Asset>;
    dataSource: MatTreeNestedDataSource<Asset>;

    treeDropdownOpen = false;

    assetsData: Asset[] = [];
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;
    updateObservable: Observable<SpAssetModel>;

    constructor(private assetService: AssetManagementService) {
        this.treeControl = new NestedTreeControl<Asset>(node => node.assets);
        this.dataSource = new MatTreeNestedDataSource<Asset>();
    }

    hasChild = (_: number, node: any) =>
        !!node.assets && node.assets.length > 0;

    toggleTreeDropdown() {
        this.treeDropdownOpen = !this.treeDropdownOpen;
    }

    onAssetSelect(node: Asset): void {
        console.log(node);
        const index = this.selectedAssets.findIndex(
            asset => asset.assetId === node.assetId,
        );

        if (index > -1) {
            console.log('index lower 0');
            //this.selectedAssets.splice(index, 1);
        } else {
            this.selectedAssets.push(node);
        }
        console.log('Selected Assets', this.selectedAssets);
        this.selectedAssetsChange.emit(this.selectedAssets);
    }

    isSelected(node: Asset): boolean {
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
                console.log('Asset Data ', this.assetsData);
                this.dataSource.data = this.assetsData;
            },
        });
    }
    private mapAssets(
        apiAssets: any[],
        parentId: string = '',
        index: any[] = [],
    ): Asset[] {
        return apiAssets.map((asset, assetIndex) => {
            const currentPath = [...index, assetIndex];
            let flattenedPath = [];

            if (asset._id) {
                parentId = asset._id;
                flattenedPath = [parentId, ...currentPath];
            } else {
                flattenedPath = [...currentPath];
            }

            return {
                id: parentId || asset._id,
                assetId: asset.assetId,
                assetName: asset.assetName,
                flattenPath: flattenedPath,
                assets: asset.assets
                    ? this.mapAssets(
                          asset.assets,
                          parentId || asset._id,
                          flattenedPath,
                      )
                    : [],
            };
        });
    }
}
