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

import { Component, OnInit } from '@angular/core';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { Router } from '@angular/router';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import { SpConnectRoutes } from '../../connect.routes';

@Component({
    selector: 'sp-data-marketplace',
    templateUrl: './data-marketplace.component.html',
    styleUrls: ['./data-marketplace.component.scss'],
})
export class DataMarketplaceComponent implements OnInit {
    adapterDescriptions: AdapterDescription[];

    adaptersLoading = true;
    adapterLoadingError = false;

    currentFilter: AdapterFilterSettingsModel;

    constructor(
        private dataMarketplaceService: AdapterService,
        private shepherdService: ShepherdService,
        private router: Router,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb([
            SpConnectRoutes.BASE,
            this.breadcrumbService.removeLink(SpConnectRoutes.CREATE),
        ]);
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

    startAdapterTutorial3() {
        this.shepherdService.startAdapterTour3();
    }

    selectAdapter(appId: string) {
        this.router.navigate(['connect', 'create', appId]);
    }

    applyFilter(filter: AdapterFilterSettingsModel) {
        this.currentFilter = { ...filter };
    }
}
