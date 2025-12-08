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

import { Component, inject } from '@angular/core';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';
import {
    LocationConfig,
    LocationConfigService,
    SpAsset,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-view-asset',
    templateUrl: './view-asset.component.html',
    standalone: false,
})
export class SpViewAssetComponent extends BaseAssetDetailsDirective {
    locationConfig: LocationConfig;

    private locationConfigService = inject(LocationConfigService);

    onAssetAvailable() {
        this.locationConfigService
            .getLocationConfig()
            .subscribe(config => (this.locationConfig = config));
    }

    getSubassetCount(asset: SpAsset): number {
        if (!asset?.assets || asset.assets.length === 0) {
            return 0;
        }
        return asset.assets.reduce(
            (sum, child) => sum + 1 + this.getSubassetCount(child),
            0,
        );
    }
}
