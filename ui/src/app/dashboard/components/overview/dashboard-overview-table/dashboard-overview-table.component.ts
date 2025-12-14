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
    inject,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DateFormatService,
    DialogService,
    PanelType,
    SpAssetBrowserService,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { DataExplorerDashboardService } from '../../../../dashboard-shared/services/dashboard.service';
import { ChartSharedService } from '../../../../chart-shared/services/chart-shared.service';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CloneDashboardDialogComponent } from '../../../dialogs/clone-dashboard/clone-dashboard-dialog.component';
import { Subscription } from 'rxjs';
import { ChartRoutingService } from '../../../../chart-shared/services/chart-routing.service';
import { MatSort } from '@angular/material/sort';

@Component({
    selector: 'sp-dashboard-overview-table',
    templateUrl: './dashboard-overview-table.component.html',
    styleUrls: [
        '../../../../chart/components/chart-overview/chart-overview.component.scss',
    ],
    standalone: false,
})
export class DashboardOverviewTableComponent implements OnInit, OnDestroy {
    @Input()
    hasDashboardWritePrivileges: boolean;

    dataSource = new MatTableDataSource<Dashboard>();

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'name',
        'lastModified',
        'createdAt',
        'actions',
    ];
    dashboards: Dashboard[] = [];
    filteredDashboards: Dashboard[] = [];

    private dashboardService = inject(DashboardService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(ChartSharedService);
    private dialog = inject(MatDialog);
    protected translateService = inject(TranslateService);
    protected dateFormatService = inject(DateFormatService);
    private router = inject(Router);
    private assetFilterService = inject(SpAssetBrowserService);
    private routingService = inject(ChartRoutingService);
    private dialogService = inject(DialogService);

    assetFilter$: Subscription;
    currentFilterIds = new Set<string>();

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('dashboard');
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.currentFilterIds = filter?.activeElementIds;
                this.applyDashboardFilters(this.currentFilterIds);
            });

        this.dataSource.sortingDataAccessor = (dashboard, column) => {
            if (column === 'lastModified') {
                return dashboard.metadata.lastModifiedEpochMs;
            } else if (column === 'createdAt') {
                return dashboard.metadata.createdAtEpochMs;
            }
            return dashboard[column];
        };

        this.getDashboards();
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef = this.dataExplorerSharedService.openPermissionsDialog(
            dashboard.elementId,
            this.translateService.instant(
                `Manage permissions for dashboard ${dashboard.name}`,
            ),
            true,
            this.makeDashboardKioskUrl(dashboard.elementId),
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
            this.applyDashboardFilters(this.currentFilterIds);
        });
    }

    applyDashboardFilters(elementIds: Set<string>): void {
        if (this.assetFilterService.hasNoAssetFilterPermission()) {
            elementIds = new Set<string>();
        }
        if (elementIds == undefined) {
            this.filteredDashboards = [];
        } else if (elementIds.size == 0) {
            this.filteredDashboards = this.dashboards;
        } else {
            this.filteredDashboards = this.dashboards.filter(a =>
                elementIds.has(a.elementId),
            );
        }
        this.dataSource.sort = this.sort;
        this.dataSource.data = this.filteredDashboards;
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    openDashboardInKioskMode(dashboard: Dashboard) {
        this.router.navigate(['dashboard-kiosk', dashboard.elementId]);
    }

    makeDashboardKioskUrl(dashboardId: string): string {
        return `${window.location.protocol}//${window.location.host}/#/dashboard-kiosk/${dashboardId}`;
    }

    openCloneDialog(dashboard: Dashboard): void {
        const dialogRef = this.dialogService.open(
            CloneDashboardDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Clone dashboard'),
                width: '50vw',
                data: {
                    dashboard: dashboard,
                },
            },
        );
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.getDashboards();
            }
        });
    }

    onRowClicked(dashboard: Dashboard) {
        this.showDashboard(dashboard);
    }

    ngOnDestroy() {
        this.assetFilter$?.unsubscribe();
    }
}
