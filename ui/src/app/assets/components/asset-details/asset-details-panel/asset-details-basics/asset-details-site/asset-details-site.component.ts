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

import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AssetSiteDesc, SpAsset } from '@streampipes/platform-services';
import { MatSelectChange } from '@angular/material/select';

@Component({
    selector: 'sp-asset-details-site',
    templateUrl: './asset-details-site.component.html',
})
export class AssetDetailsSiteComponent implements OnChanges {
    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Input()
    sites: AssetSiteDesc[];

    currentSite: AssetSiteDesc;

    constructor() {}

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset']) {
            this.asset.assetSite ??= {
                area: undefined,
                siteId: undefined,
                hasExactLocation: false,
            };
            if (this.sites.length > 0) {
                if (this.asset.assetSite.siteId !== undefined) {
                    this.selectCurrentSite(this.asset.assetSite.siteId);
                }
            }
        }
    }

    handleLocationChange(event: MatSelectChange) {
        this.selectCurrentSite(event.value);
    }

    selectCurrentSite(siteId: string): void {
        this.currentSite = this.sites.find(loc => loc._id === siteId);
    }
}
