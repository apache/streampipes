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

import { Component, ViewChild } from '@angular/core';
import { Dashboard } from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { SpDataExplorerRoutes } from '../../data-explorer.routes';
import { DataExplorerDashboardService } from '../../services/data-explorer-dashboard.service';
import { SpDataExplorerDashboardOverviewComponent } from './data-explorer-dashboard-overview/data-explorer-dashboard-overview.component';
import { SpDataExplorerOverviewDirective } from './data-explorer-overview.directive';
import { DataExplorerRoutingService } from '../../services/data-explorer-routing.service';

@Component({
    selector: 'sp-data-explorer-overview',
    templateUrl: './data-explorer-overview.component.html',
    styleUrls: ['./data-explorer-overview.component.scss'],
})
export class DataExplorerOverviewComponent extends SpDataExplorerOverviewDirective {
    @ViewChild(SpDataExplorerDashboardOverviewComponent)
    dashboardOverview: SpDataExplorerDashboardOverviewComponent;

    resourceCount = 0;

    constructor(
        public dialogService: DialogService,
        private breadcrumbService: SpBreadcrumbService,
        private dataExplorerDashboardService: DataExplorerDashboardService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        routingService: DataExplorerRoutingService,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDataExplorerRoutes.BASE),
        );
    }

    openNewDashboardDialog() {
        const dataViewDashboard: Dashboard = {
            dashboardGeneralSettings: {},
            widgets: [],
            name: '',
            dashboardLiveSettings: {
                refreshModeActive: false,
                refreshIntervalInSeconds: 10,
                label: 'Off',
            },
        };

        this.openDashboardModificationDialog(true, dataViewDashboard);
    }

    createNewDataView(): void {
        this.routingService.navigateToDataView(true);
    }

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        const dialogRef =
            this.dataExplorerDashboardService.openDashboardModificationDialog(
                createMode,
                dashboard,
            );

        dialogRef.afterClosed().subscribe(() => {
            this.dashboardOverview.getDashboards();
        });
    }

    applyDashboardFilters(elementIds: Set<string> = new Set<string>()): void {
        this.dashboardOverview.applyDashboardFilters(elementIds);
    }
}
