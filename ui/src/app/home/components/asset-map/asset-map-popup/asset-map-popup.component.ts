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
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    AssetLinkType,
    AssetSiteDesc,
    Isa95TypeService,
    SpAssetModel,
} from '@streampipes/platform-services';
import { Router } from '@angular/router';

export type PopupAction = 'details' | 'pipelines' | 'dashboards';

@Component({
    selector: 'sp-asset-map-popup',
    templateUrl: './asset-map-popup.component.html',
    styleUrls: ['./asset-map-popup.component.scss'],
    standalone: false,
})
export class AssetMapPopupComponent implements OnInit {
    @Input()
    asset: SpAssetModel;

    @Input()
    site: AssetSiteDesc;

    @Input()
    assetLinkTypes: Record<string, AssetLinkType> = {};

    @Output() actionClicked = new EventEmitter<PopupAction>();

    isa95Type: string;

    private isa95TypeService = inject(Isa95TypeService);
    private router = inject(Router);

    ngOnInit() {
        this.isa95Type = this.isa95TypeService.toLabel(
            this.asset.assetType.isa95AssetType,
        );
    }

    navigateToAsset(): void {
        this.router.navigate([
            'assets',
            'details',
            this.asset.elementId,
            'view',
        ]);
    }
}
