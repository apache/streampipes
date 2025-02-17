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

import { Component, Input, OnChanges, OnInit } from '@angular/core';
import {
    Isa95TypeService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';
import { AssetBrowserData } from '../../../asset-browser.model';
import { SpAssetBrowserService } from '../../../asset-browser.service';

@Component({
    selector: 'sp-asset-browser-node-info',
    templateUrl: 'asset-browser-node-info.component.html',
    styleUrls: ['./asset-browser-node-info.component.scss'],
})
export class AssetBrowserNodeInfoComponent implements OnInit {
    @Input()
    asset: SpAsset;

    @Input()
    assetBrowserData: AssetBrowserData;

    labels: SpLabel[] = [];
    assetType: string;
    assetSite: string;

    constructor(private isa95TypeService: Isa95TypeService) {}

    ngOnInit() {
        this.labels =
            this.assetBrowserData.labels.filter(
                l => this.asset.labelIds?.find(a => a === l._id) !== undefined,
            ) || [];
        if (this.asset.assetType?.isa95AssetType !== undefined) {
            this.assetType = this.isa95TypeService.toLabel(
                this.asset.assetType.isa95AssetType,
            );
        }
        if (this.asset.assetSite?.siteId) {
            this.assetSite = this.assetBrowserData.sites.find(
                site => site._id === this.asset.assetSite.siteId,
            )?.label;
        }
    }
}
