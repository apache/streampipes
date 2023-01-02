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
import { DashboardConfiguration } from './model/dashboard-configuration.model';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpAppRoutes } from '../app-overview/apps.routes';
import { SpAppAssetMonitoringRoutes } from './app-asset-monitoring.routes';

@Component({
    selector: 'sp-app-asset-monitoring',
    templateUrl: './app-asset-monitoring.component.html',
    styleUrls: ['./app-asset-monitoring.component.css'],
})
export class AppAssetMonitoringComponent implements OnInit {
    selectedIndex = 0;
    dashboardSelected = false;
    selectedDashboard: DashboardConfiguration;
    @Output() appOpened = new EventEmitter<boolean>();

    constructor(private breadcrumbService: SpBreadcrumbService) {}

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb([
            SpAppRoutes.BASE,
            this.breadcrumbService.removeLink(SpAppAssetMonitoringRoutes.BASE),
        ]);
        this.appOpened.emit(true);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    openDashboard(dashboardConfig: DashboardConfiguration) {
        this.selectedDashboard = dashboardConfig;
        this.dashboardSelected = true;
    }

    closeDashboard(dashboardClosed: boolean) {
        this.dashboardSelected = false;
        this.selectedIndex = 0;
    }

    editDashboard(dashboardConfig: DashboardConfiguration) {
        this.selectedDashboard = dashboardConfig;
        this.selectedIndex = 1;
    }
}
