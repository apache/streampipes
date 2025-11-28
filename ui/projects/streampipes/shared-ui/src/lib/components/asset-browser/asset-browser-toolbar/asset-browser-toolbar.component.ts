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

import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AssetBrowserData } from '../asset-browser.model';
import { MatMenuTrigger } from '@angular/material/menu';
import { Subscription } from 'rxjs';
import { CurrentUserService } from '../../../services/current-user.service';
import { SpAssetBrowserService } from '../asset-browser.service';

@Component({
    selector: 'sp-asset-browser-toolbar',
    templateUrl: 'asset-browser-toolbar.component.html',
    styleUrls: ['asset-browser-toolbar.component.scss'],
    standalone: false,
})
export class AssetBrowserToolbarComponent implements OnInit, OnDestroy {
    private currentUserService = inject(CurrentUserService);
    private assetBrowserService = inject(SpAssetBrowserService);

    assetBrowserData: AssetBrowserData;
    showAssetBrowser = false;

    assetBrowserData$: Subscription;
    filterActive = false;
    filterDisabled = false;
    selectedAssetCount = 0;
    allAssetCount = 0;

    @ViewChild('menuTrigger') menu: MatMenuTrigger;

    ngOnInit() {
        this.showAssetBrowser = this.currentUserService.hasAnyRole([
            'PRIVILEGE_READ_ASSETS',
            'PRIVILEGE_WRITE_ASSETS',
        ]);
        if (this.showAssetBrowser) {
            this.assetBrowserData$ =
                this.assetBrowserService.assetData$.subscribe(assetData => {
                    this.assetBrowserData = assetData;
                });
            this.assetBrowserService.currentAssetFilter$.subscribe(
                assetFilter => {
                    this.filterActive = assetFilter.filterActive;
                    this.filterDisabled = assetFilter.filterDisabled;
                    this.allAssetCount = assetFilter.allAssetCount;
                    this.selectedAssetCount = assetFilter.selectedAssetCount;
                },
            );
        }
    }

    ngOnDestroy() {
        this.assetBrowserService.resetFilters();
        this.assetBrowserData$?.unsubscribe();
    }

    closeMenu(): void {
        this.menu.closeMenu();
    }
}
