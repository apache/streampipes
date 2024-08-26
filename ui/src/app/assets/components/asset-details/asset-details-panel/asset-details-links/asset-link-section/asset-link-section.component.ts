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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
    AssetLink,
    AssetLinkType,
    SpAsset,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-link-section',
    templateUrl: './asset-link-section.component.html',
    styleUrls: ['./asset-link-section.component.scss'],
})
export class AssetLinkSectionComponent {
    @Input()
    assetLinkType: AssetLinkType;

    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    openEditAssetLinkEmitter: EventEmitter<AssetLink> =
        new EventEmitter<AssetLink>();

    deleteAssetLink(assetLink: AssetLink): void {
        const index = this.asset.assetLinks.indexOf(assetLink);
        this.asset.assetLinks.splice(index, 1);
        this.asset.assetLinks = [...this.asset.assetLinks];
    }
}
