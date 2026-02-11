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
import {
    SpAssetBrowserService,
    SpBasicViewComponent,
} from '@streampipes/shared-ui';
import { Router } from '@angular/router';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';
import { SpAssetSelectionPanelComponent } from './asset-selection-panel/asset-selection-panel.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { AssetDetailsBasicsComponent } from './asset-details-panel/asset-details-basics/asset-details-basics.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-details',
    templateUrl: './asset-details.component.html',
    imports: [
        SpAssetSelectionPanelComponent,
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatButton,
        AssetDetailsBasicsComponent,
        TranslatePipe,
    ],
})
export class SpAssetDetailsComponent extends BaseAssetDetailsDirective {
    private router = inject(Router);
    private assetBrowserService = inject(SpAssetBrowserService);

    saveAsset() {
        this.cleanupEmpty();
        this.assetService.updateAsset(this.asset).subscribe(res => {
            this.assetBrowserService.loadAssetData();
            this.router.navigate(['assets']);
        });
    }

    cleanupEmpty(): void {
        if (this.asset.additionalData?.customFields) {
            this.asset.additionalData!.customFields =
                this.asset.additionalData.customFields.filter(
                    f => f.key?.trim() || f.value?.trim(),
                );
        }
    }

    onAssetAvailable() {}
}
