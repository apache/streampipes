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
import { AssetBrowserData, AssetFilter } from '../../asset-browser.model';
import { Subscription } from 'rxjs';
import { SpAssetBrowserService } from '../../asset-browser.service';

@Component({
    selector: 'sp-asset-browser-filter',
    templateUrl: 'asset-browser-filter.component.html',
    styleUrl: 'asset-browser-filter.component.scss',
})
export class AssetBrowserFilterComponent implements OnInit, OnDestroy {
    @Input()
    assetBrowserData: AssetBrowserData;
    activeFilters: AssetFilter;

    filterSub: Subscription;

    @Output()
    closeMenu: EventEmitter<void> = new EventEmitter();

    constructor(private assetBrowserService: SpAssetBrowserService) {}

    ngOnInit() {
        this.filterSub = this.assetBrowserService.filter$.subscribe(
            activeFilters => {
                this.activeFilters = activeFilters;
            },
        );
    }

    applyFilters(): void {
        this.assetBrowserService.applyFilters(this.activeFilters);
        this.closeMenu.emit();
    }

    resetFilters(): void {
        this.assetBrowserService.resetFilters();
        this.closeMenu.emit();
    }

    ngOnDestroy() {
        this.filterSub?.unsubscribe();
    }
}
