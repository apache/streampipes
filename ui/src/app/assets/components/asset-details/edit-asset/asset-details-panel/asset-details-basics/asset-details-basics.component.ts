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
    Component,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
} from '@angular/core';
import {
    AssetSiteDesc,
    Isa95TypeDesc,
    Isa95TypeService,
    SpAsset,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-details-basics',
    templateUrl: './asset-details-basics.component.html',
    styleUrls: ['./asset-details-basics.component.scss'],
})
export class AssetDetailsBasicsComponent implements OnInit, OnChanges {
    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Input()
    rootNode: boolean;

    @Input()
    sites: AssetSiteDesc[];

    isa95Types: Isa95TypeDesc[] = [];

    constructor(private isa95TypeService: Isa95TypeService) {}

    ngOnInit() {
        this.isa95Types = this.isa95TypeService.getTypeDescriptions();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset']) {
            this.asset.assetType ??= {
                assetIcon: undefined,
                assetIconColor: undefined,
                assetTypeCategory: undefined,
                assetTypeLabel: undefined,
                isa95AssetType: 'OTHER',
            };
        }
    }
}
