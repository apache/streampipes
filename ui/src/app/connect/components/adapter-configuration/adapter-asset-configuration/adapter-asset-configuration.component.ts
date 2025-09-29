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
    AdapterDescription,
    AssetManagementService,
    AssetLink,
    LinkageData,
    SpAssetModel,
    AssetLinkType,
    GenericStorageService,
    PipelineElementAssetService,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';

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
    @Input() adapterDescription: AdapterDescription;
    @Input() linkageData: LinkageData[] = [];
    @Input() stepper: MatStepper;

    @Output() adapterStartedEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @Output() assetSelected: EventEmitter<Asset> = new EventEmitter<Asset>();

    treeControl: NestedTreeControl<Asset>;
    dataSource: MatTreeNestedDataSource<Asset>;

    treeDropdownOpen = false;

    selectedAssets: any = [];

    assetsData: Asset[] = [];
    selectedAssetIds = { id: '', assetId: '' };
    currentAsset: SpAssetModel;
    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;

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

        console.log('Selected', this.selectedAssets);
        this.assetSelected.emit(node); // Emit the selected or deselected asset
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

    private getAssetLinkTypeById(linkType: string): AssetLinkType | undefined {
        return this.assetLinkTypes.find(a => a.linkType === linkType);
    }

    private buildLinks(data: LinkageData[]): AssetLink[] {
        return data
            .filter(item => item.selected)
            .map(item => {
                const linkType = this.getAssetLinkTypeById(item.type);
                return {
                    linkLabel: item.name,
                    linkType: item.type,
                    editingDisabled: false,
                    queryHint: item.type,
                    navigationActive: linkType?.navigationActive ?? false,
                    resourceId: item.id,
                };
            });
    }

    private findAssetById(assetId: string): any {
        if (this.currentAsset?.assetId === assetId) return this.currentAsset;
        return this.findSubAssetById(this.currentAsset?.assets ?? [], assetId);
    }

    private findSubAssetById(assets: any[], assetId: string): any {
        for (const asset of assets) {
            if (asset.assetId === assetId) return asset;
            const found = this.findSubAssetById(asset.assets ?? [], assetId);
            if (found) return found;
        }
        return null;
    }

    onCheckboxChange(component: any): void {
        if (!component.selected) component.name = '';
    }

    save(): void {
        this.assetService.getAsset(this.selectedAssetIds.id).subscribe({
            next: current => {
                this.currentAsset = current;

                const links = this.buildLinks(this.linkageData);
                const targetAsset = this.findAssetById(
                    this.selectedAssetIds.assetId,
                );

                if (!targetAsset) return;

                targetAsset.assetLinks = [
                    ...(targetAsset.assetLinks ?? []),
                    ...links,
                ];

                const updateObservable = targetAsset._id
                    ? this.assetService.updateAsset(targetAsset)
                    : this.updateNestedAsset(targetAsset);

                console.log(updateObservable);

                updateObservable?.subscribe({
                    next: updated => {
                        this.adapterStartedEmitter.emit();
                    },
                });
            },
        });
    }

    cancel(): void {
        this.adapterStartedEmitter.emit();
    }

    private updateNestedAsset(assetToUpdate: any) {
        const index = this.currentAsset?.assets?.findIndex(
            (asset: any) => asset.assetId === assetToUpdate.assetId,
        );

        if (index === -1 || index === undefined) return null;

        this.currentAsset.assets[index] = assetToUpdate;
        console.log('currentAsset ', this.currentAsset);
        return this.assetService.updateAsset(this.currentAsset);
    }
}
