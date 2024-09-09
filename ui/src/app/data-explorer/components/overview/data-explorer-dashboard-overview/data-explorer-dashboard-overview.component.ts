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

import { Component, EventEmitter, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import {
    Dashboard,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    ConfirmDialogComponent,
    DialogService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../../services/auth.service';
import { SpDataExplorerOverviewDirective } from '../data-explorer-overview.directive';
import { DataExplorerDashboardService } from '../../../services/data-explorer-dashboard.service';
import { DataExplorerRoutingService } from '../../../services/data-explorer-routing.service';
import { MatDialog } from '@angular/material/dialog';

@Component({
    selector: 'sp-data-explorer-dashboard-overview',
    templateUrl: './data-explorer-dashboard-overview.component.html',
    styleUrls: ['../data-explorer-overview.component.scss'],
})
export class SpDataExplorerDashboardOverviewComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];
    filteredDashboards: Dashboard[] = [];

    @Output()
    resourceCountEmitter: EventEmitter<number> = new EventEmitter();

    constructor(
        private dataViewService: DataViewDataExplorerService,
        private dashboardService: DataViewDataExplorerService,
        private dataExplorerDashboardService: DataExplorerDashboardService,
        public dialogService: DialogService,
        routingService: DataExplorerRoutingService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        private dialog: MatDialog,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.displayedColumns = ['name', 'actions'];
        this.getDashboards();
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef =
            this.dataExplorerDashboardService.openPermissionsDialog(
                dashboard.elementId,
                `Manage permissions for dashboard ${dashboard.name}`,
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
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '600px',
            data: {
                title: 'Are you sure you want to delete this dashboard?',
                subtitle: 'This action cannot be undone!',
                cancelTitle: 'Cancel',
                okTitle: 'Delete Dashboard',
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.dashboardService
                    .deleteDashboard(dashboard)
                    .subscribe(() => {
                        this.getDashboards();
                    });
            }
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
            this.resourceCountEmitter.emit(this.dashboards.length);
            this.applyDashboardFilters();
        });
    }

    applyDashboardFilters(elementIds: Set<string> = new Set<string>()): void {
        this.filteredDashboards = this.dashboards.filter(a => {
            if (elementIds.size === 0) {
                return true;
            } else {
                return elementIds.has(a.elementId);
            }
        });
        this.dataSource.data = this.filteredDashboards;
    }
}
