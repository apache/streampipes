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
    AssetConstants,
    AssetLinkType,
    GenericStorageService,
    SpAsset,
} from '@streampipes/platform-services';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { AssetLinkTableComponent } from './asset-link-table/asset-link-table.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-view-asset-links',
    templateUrl: './view-asset-links.component.html',
    styleUrls: ['./view-asset-links.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        SplitSectionComponent,
        AssetLinkTableComponent,
        SpAlertBannerComponent,
        TranslatePipe,
    ],
})
export class ViewAssetLinksComponent implements OnInit {
    @Input()
    selectedAsset: SpAsset;

    assetLinkTypes: AssetLinkType[] = [];

    private genericStorageService = inject(GenericStorageService);

    ngOnInit() {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(res => {
                this.assetLinkTypes = res;
            });
    }
}
