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

import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import {
    Dashboard,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import { ObjectPermissionDialogComponent } from '../../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import {
    CurrentUserService,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../../services/auth.service';
import { SpDataExplorerOverviewDirective } from '../data-explorer-overview.directive';
import { DataExplorerDashboardService } from '../../../services/data-explorer-dashboard.service';
import { DataExplorerRoutingService } from '../../../services/data-explorer-routing.service';

@Component({
    selector: 'sp-data-explorer-dashboard-overview',
    templateUrl: './data-explorer-dashboard-overview.component.html',
    styleUrls: ['../data-explorer-overview.component.scss'],
})
export class SpDataExplorerDashboardOverviewComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];

    constructor(
        private dataViewService: DataViewDataExplorerService,
        private dashboardService: DataViewDataExplorerService,
        private dataExplorerDashboardService: DataExplorerDashboardService,
        public dialogService: DialogService,
        routingService: DataExplorerRoutingService,
        authService: AuthService,
        currentUserService: CurrentUserService,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.displayedColumns = ['name', 'actions'];
        this.getDashboards();
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage permissions',
                width: '50vw',
                data: {
                    objectInstanceId: dashboard.elementId,
                    headerTitle:
                        'Manage permissions for dashboard ' + dashboard.name,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.getDashboards();
            }
        });
    }

    openEditDashboardDialog(dashboard: Dashboard) {
        const dialogRef =
            this.dataExplorerDashboardService.openDashboardModificationDialog(
                false,
                dashboard,
            );

        dialogRef.afterClosed().subscribe(() => {
            this.getDashboards();
        });
    }

    openDeleteDashboardDialog(dashboard: Dashboard) {
        this.dashboardService.deleteDashboard(dashboard).subscribe(() => {
            this.getDashboards();
        });
    }

    showDashboard(dashboard: Dashboard) {
        this.routingService.navigateToDashboard(false, dashboard.elementId);
    }

    editDashboard(dashboard: Dashboard) {
        this.routingService.navigateToDashboard(true, dashboard.elementId);
    }

    getDashboards() {
        this.dataViewService.getDataViews().subscribe(data => {
            this.dashboards = data.sort((a, b) => a.name.localeCompare(b.name));
            this.dataSource.data = this.dashboards;
        });
    }
}
