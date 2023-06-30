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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AdapterService } from '@streampipes/platform-services';
import { MatSelectChange } from '@angular/material/select';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';

@Component({
    selector: 'sp-connect-filter-toolbar',
    templateUrl: './filter-toolbar.component.html',
    styleUrls: ['./filter-toolbar.component.scss'],
})
export class SpConnectFilterToolbarComponent implements OnInit {
    @Output()
    filterChangedEmitter: EventEmitter<AdapterFilterSettingsModel> =
        new EventEmitter<AdapterFilterSettingsModel>();

    adapterCategories: any;

    currentFilter: AdapterFilterSettingsModel = {
        textFilter: '',
        selectedCategory: 'All',
        selectedType: 'All types',
    };

    constructor(private dataMarketplaceService: AdapterService) {}

    ngOnInit(): void {
        this.loadAvailableTypeCategories();
    }

    loadAvailableTypeCategories() {
        this.dataMarketplaceService.getAdapterCategories().subscribe(res => {
            this.adapterCategories = res;
            this.adapterCategories.unshift({
                label: 'All categories',
                description: '',
                code: 'All',
            });
            this.filterChangedEmitter.emit(this.currentFilter);
        });
    }

    filterAdapter(event: MatSelectChange) {
        this.filterChangedEmitter.emit(this.currentFilter);
    }

    updateFilterTerm(event: string) {
        this.currentFilter.textFilter = event;
        this.filterChangedEmitter.emit(this.currentFilter);
    }
}
