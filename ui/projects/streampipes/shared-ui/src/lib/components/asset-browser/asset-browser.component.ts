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
    Input,
    OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import { SpAssetBrowserService } from './asset-browser.service';
import { AssetBrowserData } from './asset-browser.model';
import { Subscription } from 'rxjs';
import { SpAsset } from '@streampipes/platform-services';
import { Router } from '@angular/router';
import { CurrentUserService } from '../../services/current-user.service';

@Component({
    selector: 'sp-asset-browser',
    templateUrl: 'asset-browser.component.html',
    styleUrls: ['./asset-browser.component.scss'],
})
export class AssetBrowserComponent implements OnInit, OnDestroy {
    @Input()
    showResources = false;

    @Input()
    allResourcesAlias = 'Resources';

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

    assetBrowserDataSub: Subscription;
    expandedSub: Subscription;

    expanded = true;
    showAssetBrowser = false;

    constructor(
        private assetBrowserService: SpAssetBrowserService,
        private router: Router,
        private currentUserService: CurrentUserService,
    ) {}

    ngOnInit(): void {
        this.showAssetBrowser = this.currentUserService.hasAnyRole([
            'PRIVILEGE_READ_ASSETS',
            'PRIVILEGE_WRITE_ASSETS',
        ]);
        if (this.showAssetBrowser) {
            this.assetBrowserDataSub =
                this.assetBrowserService.assetData$.subscribe(assetData => {
                    this.assetBrowserData = assetData;
                });
            this.expandedSub = this.assetBrowserService.expanded$.subscribe(
                expanded => (this.expanded = expanded),
            );
        }
    }

    toggleExpanded(event: boolean): void {
        this.assetBrowserService.expanded$.next(event);
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

    navigateToAssetManagement(): void {
        this.router.navigate(['assets', 'overview']);
    }

    ngOnDestroy(): void {
        this.assetBrowserDataSub?.unsubscribe();
        this.expandedSub?.unsubscribe();
    }
}
