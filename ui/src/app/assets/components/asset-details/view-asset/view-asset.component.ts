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
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpAssetTopBannerComponent } from './asset-top-banner/asset-top-banner.component';
import {
    SpBasicViewComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { SpAssetSelectionMenuComponent } from '../edit-asset/asset-selection-menu/asset-selection-menu.component';
import { ViewAssetBasicsComponent } from './view-asset-basics/view-assset-basics.component';
import { AssetDetailsCustomFieldsComponent } from '../edit-asset/asset-details-panel/asset-details-basics/asset-details-custom-fields/asset-details-custom-fields.component';
import { ViewAssetLinksComponent } from './view-asset-links/view-asset-links.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-view-asset',
    templateUrl: './view-asset.component.html',
    imports: [
        LayoutDirective,
        SpAssetTopBannerComponent,
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        MatButton,
        MatMenuTrigger,
        MatTooltip,
        MatIcon,
        MatMenu,
        SpAssetSelectionMenuComponent,
        ViewAssetBasicsComponent,
        SplitSectionComponent,
        AssetDetailsCustomFieldsComponent,
        ViewAssetLinksComponent,
        TranslatePipe,
    ],
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
