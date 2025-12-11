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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    Isa95TypeService,
    LocationConfig,
    SpAssetModel,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-top-banner',
    templateUrl: './asset-top-banner.component.html',
    styleUrls: ['./asset-top-banner.component.scss'],
    standalone: false,
})
export class SpAssetTopBannerComponent implements OnInit {
    @Input()
    assetModel: SpAssetModel;

    @Input()
    locationConfig: LocationConfig;

    assetType = undefined;

    private isa95TypeService = inject(Isa95TypeService);

    ngOnInit() {
        this.assetType =
            this.isa95TypeService.toLabel(
                this.assetModel.assetType.isa95AssetType,
            ) || '';
    }
}
