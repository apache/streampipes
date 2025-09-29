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
    Input,
    Output,
    EventEmitter,
    OnInit,
} from '@angular/core';
import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import {
    AssetConstants,
    AssetManagementService,
    AssetLink,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    GenericStorageService,
    PipelineElementAssetService,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';
import { Observable } from 'rxjs';

export interface Asset {
    assetId: string;
    assetName: string;
    assets?: Asset[];
    id: string;
}

@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements OnInit {
    @Input() linkageData: LinkageData[] = [];
    @Input() stepper: MatStepper;

    @Input() assetSelected;

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @Input() selectedAssets: Asset[] = [];
    @Output() selectedAssetsChange = new EventEmitter<Asset[]>();

    treeControl: NestedTreeControl<Asset>;
    dataSource: MatTreeNestedDataSource<Asset>;

    treeDropdownOpen = false;

    //selectedAssets: Asset[] = [];

    assetsData: Asset[] = [];
    selectedAssetIds = { id: '', assetId: '' };
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;
    updateObservable: Observable<SpAssetModel>;

    constructor(
        private assetService: AssetManagementService,
        private storageService: GenericStorageService,
    ) {
        this.treeControl = new NestedTreeControl<Asset>(node => node.assets);
        this.dataSource = new MatTreeNestedDataSource<Asset>();
    }

    hasChild = (_: number, node: any) =>
        !!node.assets && node.assets.length > 0;

    toggleTreeDropdown() {
        this.treeDropdownOpen = !this.treeDropdownOpen;
    }

    onAssetSelect(node: Asset): void {
        const index = this.selectedAssets.findIndex(
            asset => asset.assetId === node.assetId,
        );

        if (index > -1) {
            // Deselect if already selected
            this.selectedAssets.splice(index, 1);
        } else {
            // Select if not already selected
            this.selectedAssets.push(node);
        }

        console.log('Emit Selected', this.selectedAssets);
        //this.assetSelected(this.selectedAssets); // Emit the selected or deselected asset
        this.selectedAssetsChange.emit(this.selectedAssets);
    }

    isSelected(node: Asset): boolean {
        return this.selectedAssets.some(
            asset => asset.assetId === node.assetId,
        );
    }

    ngOnInit(): void {
        this.loadAssets();
        console.log(this.dataSource.data);
        console.log(this.assetsData);
        this.loadAssetLinkTypes();
    }

    private loadAssets(): void {
        this.assetService.getAllAssets().subscribe({
            next: assets => {
                console.log(assets);
                this.assetsData = this.mapAssets(assets);
                this.dataSource.data = this.assetsData; // <-- ADD THIS LINE
                console.log(this.assetsData);
            },
        });
    }

    private loadAssetLinkTypes(): void {
        this.storageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(linkTypes => {
                this.assetLinkTypes = linkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );
                this.assetLinksLoaded = true;
            });
    }

    private mapAssets(apiAssets: any[], parentId: string = ''): Asset[] {
        return apiAssets.map(asset => ({
            id: parentId || asset._id,
            assetId: asset.assetId,
            assetName: asset.assetName,
            assets: asset.assets
                ? this.mapAssets(asset.assets, parentId || asset._id)
                : [],
        }));
    }
}
