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
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    Dashboard,
    DashboardService,
    DashboardSummaryDto,
} from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DateFormatService,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    PanelType,
    SpAssetBrowserService,
    SpBasicHeaderTitleComponent,
    SpTableActionsDirective,
    SpTableAssetContextConfig,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { DataExplorerDashboardService } from '../../../../dashboard-shared/services/dashboard.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CloneDashboardDialogComponent } from '../../../dialogs/clone-dashboard/clone-dashboard-dialog.component';
import { Subscription } from 'rxjs';
import { ChartRoutingService } from '../../../../chart-shared/services/chart-routing.service';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatMenuItem } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-dashboard-overview-table',
    templateUrl: './dashboard-overview-table.component.html',
    styleUrls: [
        '../../../../chart/components/chart-overview/chart-overview.component.scss',
    ],
    imports: [
        FlexDirective,
        LayoutDirective,
        SpBasicHeaderTitleComponent,
        LayoutAlignDirective,
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        LayoutGapDirective,
        SpTableActionsDirective,
        MatMenuItem,
        MatIcon,
        TranslatePipe,
    ],
})
export class DashboardOverviewTableComponent implements OnInit, OnDestroy {
    @Input()
    hasDashboardWritePrivileges: boolean;

    dataSource = new MatTableDataSource<DashboardSummaryDto>();

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'name',
        'assetContext',
        'lastModified',
        'createdAt',
        'actions',
    ];
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'dashboard',
        resourceIdKey: 'elementId',
    };
    dashboards: DashboardSummaryDto[] = [];
    filteredDashboards: DashboardSummaryDto[] = [];

    private dashboardService = inject(DashboardService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
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
            if (column === 'name') {
                return dashboard.name;
            } else if (column === 'lastModified') {
                return dashboard.lastModifiedEpochMs;
            } else if (column === 'createdAt') {
                return dashboard.createdAtEpochMs;
            }
            return dashboard[column];
        };

        this.getDashboards();
    }

    showManageDialog(dashboard: DashboardSummaryDto): void {
        this.withDashboard(dashboard, resource => {
            const resourceConfig: ObjectManageDialogResourceConfig<Dashboard> =
                {
                    resourceLabel: 'Dashboard',
                    nameLabel: 'Name',
                    descriptionLabel: 'Description',
                    nameProperty: 'name',
                    assetLinkType: 'dashboard',
                    assetLinkCheckboxLabel:
                        'Add the current dashboard to an existing asset',
                    saveResource: resource =>
                        this.dashboardService.updateDashboard(resource),
                };
            const dialogRef = this.dialogService.open(
                ObjectManageDialogComponent,
                {
                    panelType: PanelType.SLIDE_IN_PANEL,
                    title: this.translateService.instant('Manage'),
                    width: '50vw',
                    data: {
                        objectInstanceId: resource.elementId,
                        resource: { ...resource },
                        saveMode: 'immediate',
                        resourceConfig,
                        anonymousReadSupported: true,
                        publicLink: this.makeDashboardKioskUrl(
                            resource.elementId,
                        ),
                        headerTitle:
                            this.translateService.instant('Manage Dashboard ') +
                            resource.name,
                    },
                },
            );
            dialogRef.afterClosed().subscribe(refresh => {
                if (refresh) {
                    this.getDashboards();
                }
            });
        });
    }

    openDeleteDashboardDialog(dashboard: DashboardSummaryDto): void {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '600px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete dashboard "{{dashboardTitle}}"?',
                    {
                        dashboardTitle: dashboard.name ?? '',
                    },
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be undone!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete dashboard'),
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result === 'confirm') {
                this.dashboardService
                    .deleteDashboardById(dashboard.elementId)
                    .subscribe(() => {
                        this.getDashboards();
                    });
            }
        });
    }

    showDashboard(dashboard: DashboardSummaryDto): void {
        this.routingService.navigateToDashboard(false, dashboard.elementId);
    }

    editDashboard(dashboard: DashboardSummaryDto): void {
        this.routingService.navigateToDashboard(true, dashboard.elementId);
    }

    getDashboards(): void {
        this.dashboardService.getDashboardSummary().subscribe(data => {
            this.dashboards = data.resources.sort((a, b) =>
                a.name.localeCompare(b.name),
            );
            this.applyDashboardFilters(this.currentFilterIds);
        });
    }

    applyDashboardFilters(elementIds: Set<string>): void {
        if (elementIds === undefined) {
            this.filteredDashboards = [];
        } else if (elementIds.size === 0) {
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

    openDashboardInKioskMode(dashboard: DashboardSummaryDto): void {
        this.router.navigate(['dashboard-kiosk', dashboard.elementId]);
    }

    makeDashboardKioskUrl(dashboardId: string): string {
        return `${window.location.protocol}//${window.location.host}/#/dashboard-kiosk/${dashboardId}`;
    }

    openCloneDialog(dashboardSummary: DashboardSummaryDto): void {
        this.withDashboard(dashboardSummary, dashboard => {
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
        });
    }

    onRowClicked(dashboard: DashboardSummaryDto): void {
        this.showDashboard(dashboard);
    }

    ngOnDestroy(): void {
        this.assetFilter$?.unsubscribe();
    }

    private withDashboard(
        dashboardSummary: DashboardSummaryDto,
        callback: (dashboard: Dashboard) => void,
    ): void {
        this.dashboardService
            .getDashboard(dashboardSummary.elementId)
            .subscribe(dashboard => {
                callback(dashboard);
            });
    }
}
