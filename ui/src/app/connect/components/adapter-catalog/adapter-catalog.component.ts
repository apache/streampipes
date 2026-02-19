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

import { Component, inject, OnInit } from '@angular/core';
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import {
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import { SpConnectRoutes } from '../../connect.breadcrumb';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpConnectFilterToolbarComponent } from '../filter-toolbar/filter-toolbar.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { AdapterCatalogItemComponent } from './adapter-description/adapter-catalog-item.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AdapterFilterPipe } from '../../filter/adapter-filter.pipe';

@Component({
    selector: 'sp-adapter-catalog',
    templateUrl: './adapter-catalog.component.html',
    styleUrls: ['./adapter-catalog.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        SpConnectFilterToolbarComponent,
        SpBasicHeaderTitleComponent,
        MatProgressSpinner,
        AdapterCatalogItemComponent,
        TranslatePipe,
        AdapterFilterPipe,
    ],
})
export class AdapterCatalogComponent implements OnInit {
    private dataMarketplaceService = inject(AdapterService);
    private breadcrumbService = inject(SpBreadcrumbService);

    adapterDescriptions: AdapterDescription[];

    adaptersLoading = true;
    adapterLoadingError = false;

    currentFilter: AdapterFilterSettingsModel;

    ngOnInit() {
        this.updateBreadcrumb();
        this.getAdapterDescriptions();
    }

    getAdapterDescriptions(): void {
        this.adaptersLoading = true;
        this.adapterDescriptions = [];

        this.dataMarketplaceService.getAdapterDescriptions().subscribe({
            next: allAdapters => {
                this.adapterDescriptions = allAdapters;
                this.adapterDescriptions.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.adaptersLoading = false;
            },
            error: _ => {
                this.adaptersLoading = false;
                this.adapterLoadingError = true;
            },
        });
    }

    applyFilter(filter: AdapterFilterSettingsModel) {
        this.currentFilter = { ...filter };
    }

    private updateBreadcrumb() {
        this.breadcrumbService.updateBreadcrumb([
            SpConnectRoutes.BASE,
            this.breadcrumbService.removeLink(SpConnectRoutes.CREATE),
        ]);
    }
}
