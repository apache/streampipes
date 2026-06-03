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

import { Component, inject, Input, OnInit, ViewChild } from '@angular/core';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    ChartService,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DateFormatService,
    SpAssetBrowserService,
    SpTableAssetContextConfig,
    SpBasicHeaderTitleComponent,
    SpTableActionsDirective,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { ChartSharedService } from '../../../../chart-shared/services/chart-shared.service';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ChartRoutingService } from '../../../../chart-shared/services/chart-routing.service';
import { Subscription } from 'rxjs';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatMenuItem } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
    selector: 'sp-data-explorer-overview-table',
    templateUrl: './chart-overview-table.component.html',
    styleUrls: ['../chart-overview.component.scss'],
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
        MatTooltip,
        TranslatePipe,
    ],
})
export class ChartOverviewTableComponent implements OnInit {
    @Input()
    hasDataExplorerWritePrivileges: boolean;

    @ViewChild(MatSort)
    sort: MatSort;

    dataSource = new MatTableDataSource<DataExplorerWidgetModel>();
    displayedColumns: string[] = [
        'name',
        'assetContext',
        'lastModified',
        'createdAt',
        'actions',
    ];
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'chart',
        resourceIdKey: 'elementId',
    };
    charts: DataExplorerWidgetModel[] = [];
    filteredCharts: DataExplorerWidgetModel[] = [];

    private dataViewService = inject(ChartService);
    private dataExplorerDashboardService = inject(ChartSharedService);
    private dialog = inject(MatDialog);
    private translateService = inject(TranslateService);
    private dateFormatService = inject(DateFormatService);
    private routingService = inject(ChartRoutingService);
    private assetFilterService = inject(SpAssetBrowserService);

    assetFilter$: Subscription;
    currentFilterIds = new Set<string>();

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('chart');
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.currentFilterIds = filter?.activeElementIds;
                this.applyChartFilters(this.currentFilterIds);
            });

        this.dataSource.sortingDataAccessor = (chart, column) => {
            if (column === 'name') {
                return chart.baseAppearanceConfig.widgetTitle;
            } else if (column === 'lastModified') {
                return chart.metadata.lastModifiedEpochMs;
            } else if (column === 'createdAt') {
                return chart.metadata.createdAtEpochMs;
            }
            return chart[column];
        };
        this.getDataViews();
    }

    getDataViews(): void {
        this.dataViewService.getAllCharts().subscribe(widgets => {
            this.charts = widgets.sort((a, b) =>
                a.baseAppearanceConfig.widgetTitle.localeCompare(
                    b.baseAppearanceConfig.widgetTitle,
                ),
            );
            this.applyChartFilters(this.currentFilterIds);
        });
    }

    openDataView(dataView: DataExplorerWidgetModel, editMode: boolean): void {
        this.routingService.navigateToChart(
            editMode && this.hasDataExplorerWritePrivileges,
            dataView.elementId,
        );
    }

    showPermissionsDialog(chart: DataExplorerWidgetModel) {
        const dialogRef =
            this.dataExplorerDashboardService.openPermissionsDialog(
                chart.elementId,
                this.translateService.instant(
                    `Manage permissions for chart ${chart.baseAppearanceConfig.widgetTitle}`,
                ),
            );

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.getDataViews();
            }
        });
    }

    deleteDataView(dataView: DataExplorerWidgetModel) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '600px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete chart "{{chartTitle}}"?',
                    {
                        chartTitle:
                            dataView.baseAppearanceConfig.widgetTitle ?? '',
                    },
                ),
                subtitle: this.translateService.instant(
                    'The chart will be removed from all dashboards as well. This action cannot be undone!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete chart'),
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result === 'confirm') {
                this.dataViewService
                    .deleteChart(dataView.elementId)
                    .subscribe(() => {
                        this.getDataViews();
                    });
            }
        });
    }

    cloneDataView(dataView: DataExplorerWidgetModel) {
        this.dataViewService.cloneChart(dataView).subscribe(() => {
            this.getDataViews();
        });
    }

    applyChartFilters(elementIds: Set<string>): void {
        if (elementIds === undefined) {
            this.filteredCharts = [];
        } else if (elementIds.size === 0) {
            this.filteredCharts = this.charts;
        } else {
            this.filteredCharts = this.charts.filter(a =>
                elementIds.has(a.elementId),
            );
        }
        this.dataSource.sort = this.sort;
        this.dataSource.data = this.filteredCharts;
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    isLegacyMultiSourceChart(chart: DataExplorerWidgetModel): boolean {
        return (chart?.dataConfig?.sourceConfigs?.length ?? 0) > 1;
    }

    requiresAttention(chart: DataExplorerWidgetModel): boolean {
        return chart?.healthStatus === 'REQUIRES_ATTENTION';
    }
}
