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

import { AfterViewInit, Component, Input } from '@angular/core';

import {
    AdapterDescription,
    AssetManagementService,
} from '@streampipes/platform-services';
import { MatStepper } from '@angular/material/stepper';

interface LinkageData {
    elementId: string;
    pipelineId: string;
}

export interface Asset {
    assetId: string;
    assetName: string;
    assets?: Asset[]; // Sub-assets
}
@Component({
    selector: 'sp-adapter-asset-configuration',
    templateUrl: './adapter-asset-configuration.component.html',
    styleUrls: ['./adapter-asset-configuration.component.scss'],
    standalone: false,
})
export class AdapterAssetConfigurationComponent implements AfterViewInit {
    /**
     * Adapter description the selected format is added to
     */
    assetsData: Asset[] = [];

    @Input() adapterDescription: AdapterDescription;

    @Input() linkageData: LinkageData;

    @Input() stepper: MatStepper;

    constructor(private assetManagementService: AssetManagementService) {}

    ngAfterViewInit(): void {
        console.log('SAVE');
        this.getAssets();

        // Process the API data into a tree format
        //this.assetsData = this.transformAssetsData(apiResponse);
        console.log(this.assetsData);
    }

    getAssets(): void {
        this.assetManagementService.getAllAssets().subscribe({
            next: data => {
                this.assetsData = this.transformAssetsData(data);
            },
        });
    }

    assignToAssets(linkageData: LinkageData) {}

    transformAssetsData(apiResponse: any[]): Asset[] {
        return apiResponse.map(asset => ({
            assetId: asset.assetId,
            assetName: asset.assetName,
            assets: asset.assets ? this.transformAssetsData(asset.assets) : [],
        }));
    }
}
