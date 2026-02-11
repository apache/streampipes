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

import { Component, Input, OnInit } from '@angular/core';
import {
    LocationConfig,
    LocationConfigService,
    SpAsset,
} from '@streampipes/platform-services';
import { FlexDirective } from '@ngbracket/ngx-layout/flex';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { SingleMarkerMapComponent } from '../../../../../../../../core-ui/single-marker-map/single-marker-map.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-location',
    templateUrl: './asset-location.component.html',
    imports: [
        FlexDirective,
        MatCheckbox,
        FormsModule,
        SingleMarkerMapComponent,
        TranslatePipe,
    ],
})
export class AssetLocationComponent implements OnInit {
    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    locationConfig: LocationConfig;

    constructor(private locationConfigService: LocationConfigService) {}

    ngOnInit() {
        this.asset.assetSite.location ??= {
            coordinates: {
                latitude: 0,
                longitude: 0,
            },
            zoom: 1,
        };
        this.locationConfigService
            .getLocationConfig()
            .subscribe(res => (this.locationConfig = res));
    }
}
