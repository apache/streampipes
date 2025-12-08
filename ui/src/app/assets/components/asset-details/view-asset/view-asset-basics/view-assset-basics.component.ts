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
    Isa95TypeService,
    SpAsset,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-view-asset-basics',
    templateUrl: './view-asset-basics.component.html',
    styleUrls: ['./view-asset-basics.component.scss'],
    standalone: false,
})
export class ViewAssetBasicsComponent implements OnChanges {
    @Input()
    selectedAsset: SpAsset;

    @Input()
    sites: AssetSiteDesc[] = [];

    selectedAssetType: string;

    constructor(private isa95TypeService: Isa95TypeService) {}

    ngOnChanges(changes: SimpleChanges) {
        this.selectedAssetType =
            this.isa95TypeService.toLabel(
                this.selectedAsset.assetType.isa95AssetType,
            ) || '';
    }

    getDescription(asset: SpAsset): string {
        return asset?.assetDescription || 'No description available.';
    }
}
