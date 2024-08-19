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

import { Component, Input, OnInit } from '@angular/core';
import {
    Isa95TypeDesc,
    Isa95TypeService,
} from '@streampipes/platform-services';
import { AssetFilter } from '../../../asset-browser.model';

@Component({
    selector: 'sp-asset-browser-filter-type',
    templateUrl: 'asset-browser-filter-type.component.html',
    styleUrls: ['../asset-browser-filter.component.scss'],
})
export class AssetBrowserFilterTypeComponent implements OnInit {
    allAssetTypes: Isa95TypeDesc[] = [];

    @Input()
    activeFilters: AssetFilter;

    constructor(private typeService: Isa95TypeService) {}

    ngOnInit() {
        this.allAssetTypes = this.typeService
            .getTypeDescriptions()
            .sort((a, b) => a.label.localeCompare(b.label));
    }

    compare(o1: Isa95TypeDesc, o2: Isa95TypeDesc): boolean {
        return o1.type === o2.type;
    }

    selectAll(): void {
        this.activeFilters.selectedTypes = [...this.allAssetTypes];
    }

    selectNone(): void {
        this.activeFilters.selectedTypes = [];
    }
}
