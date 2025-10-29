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
    ViewChild,
} from '@angular/core';
import { AssetBrowserData } from '../asset-browser.model';
import { MatMenuTrigger } from '@angular/material/menu';
import { Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { CurrentUserService } from '../../../services/current-user.service';
import { SpAssetBrowserService } from '../asset-browser.service';
import { SpAsset } from '@streampipes/platform-services';

@Component({
    selector: 'sp-asset-browser-toolbar',
    templateUrl: 'asset-browser-toolbar.component.html',
    standalone: false,
})
export class AssetBrowserToolbarComponent implements OnInit {
    private currentUserService = inject(CurrentUserService);
    private assetBrowserService = inject(SpAssetBrowserService);
    private translateService = inject(TranslateService);

    @Input()
    allResourcesAlias = this.translateService.instant('Resources');

    @Input()
    browserWidth = 20;

    @Input()
    filteredAssetLinkType: string;

    @Input()
    resourceCount = 0;

    @Input()
    assetSelectionMode = false;

    @Output()
    filterIdsEmitter: EventEmitter<Set<string>> = new EventEmitter<
        Set<string>
    >();

    @Output()
    selectedAssetIdEmitter: EventEmitter<string> = new EventEmitter<string>();

    assetBrowserData: AssetBrowserData;
    showAssetBrowser = false;

    assetBrowserDataSub: Subscription;

    @ViewChild('menuTrigger') menu: MatMenuTrigger;

    ngOnInit() {
        this.showAssetBrowser = this.currentUserService.hasAnyRole([
            'PRIVILEGE_READ_ASSETS',
            'PRIVILEGE_WRITE_ASSETS',
        ]);
        if (this.showAssetBrowser) {
            this.assetBrowserDataSub =
                this.assetBrowserService.assetData$.subscribe(assetData => {
                    this.assetBrowserData = assetData;
                    console.log(assetData);
                });
        }
    }

    applyAssetFilter(asset: SpAsset) {
        const elementIds = new Set<string>();
        if (asset.assetId !== '_root') {
            this.assetBrowserService.collectElementIds(
                asset,
                this.filteredAssetLinkType,
                elementIds,
            );
            this.filterIdsEmitter.emit(elementIds);
        }
        this.filterIdsEmitter.emit(elementIds);
        this.selectedAssetIdEmitter.emit(asset.assetId);
    }

    closeMenu(): void {
        this.menu.closeMenu();
    }
}
