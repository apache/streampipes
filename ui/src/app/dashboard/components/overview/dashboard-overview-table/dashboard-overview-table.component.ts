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

import { Component, EventEmitter, inject, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DateFormatService,
} from '@streampipes/shared-ui';
import { SpDataExplorerOverviewDirective } from '../../../../data-explorer/components/overview/data-explorer-overview.directive';
import { MatDialog } from '@angular/material/dialog';
import { DataExplorerDashboardService } from '../../../../dashboard-shared/services/dashboard.service';
import { DataExplorerSharedService } from '../../../../data-explorer-shared/services/data-explorer-shared.service';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-dashboard-overview-table',
    templateUrl: './dashboard-overview-table.component.html',
    styleUrls: [
        '../../../../data-explorer/components/overview/data-explorer-overview.component.scss',
    ],
    standalone: false,
})
export class DashboardOverviewTableComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];
    filteredDashboards: Dashboard[] = [];

    @Output()
    resourceCountEmitter: EventEmitter<number> = new EventEmitter();

    private dashboardService = inject(DashboardService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(DataExplorerSharedService);
    private dialog = inject(MatDialog);
    protected translateService = inject(TranslateService);
    protected dateFormatService = inject(DateFormatService);
    private router = inject(Router);

    afterInit(): void {
        this.displayedColumns = [
            'name',
            'lastModified',
            'createdAt',
            'actions',
        ];
        this.getDashboards();
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef = this.dataExplorerSharedService.openPermissionsDialog(
            dashboard.elementId,
            this.translateService.instant(
                `Manage permissions for dashboard ${dashboard.name}`,
            ),
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
                title: this.translateService.instant(
                    'Are you sure you want to delete this dashboard?',
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be undone!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                okTitle: this.translateService.instant('Delete dashboard'),
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
        this.dashboardService.getDashboards().subscribe(data => {
            this.dashboards = data.sort((a, b) => a.name.localeCompare(b.name));
            this.resourceCountEmitter.emit(this.dashboards.length);
            this.applyDashboardFilters();
        });
    }

    applyDashboardFilters(elementIds: Set<string> = new Set<string>()): void {
        if (elementIds.size == 0) {
            this.filteredDashboards = this.dashboards;
        } else {
            this.filteredDashboards = this.dashboards.filter(a =>
                elementIds.has(a.elementId),
            );
        }
        this.dataSource.data = this.filteredDashboards;
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    openDashboardInKioskMode(dashboard: Dashboard) {
        this.router.navigate(['dashboard-kiosk', dashboard.elementId]);
    }
}
