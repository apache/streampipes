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
    ChartSummaryDto,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DateFormatService,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    PanelType,
    SpAssetBrowserService,
    SpTableAssetContextConfig,
    SpBasicHeaderTitleComponent,
    SpTableActionsDirective,
    SpTableComponent,
} from '@streampipes/shared-ui';
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
import { ChartRegistry } from '../../../../chart-shared/registry/chart-registry.service';

type ManageableChart = DataExplorerWidgetModel & {
    name: string;
    description: string;
};

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

    dataSource = new MatTableDataSource<ChartSummaryDto>();
    displayedColumns: string[] = [
        'name',
        'chartType',
        'assetContext',
        'lastModified',
        'createdAt',
        'actions',
    ];
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'chart',
        resourceIdKey: 'elementId',
    };
    charts: ChartSummaryDto[] = [];
    filteredCharts: ChartSummaryDto[] = [];

    private dataViewService = inject(ChartService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private dateFormatService = inject(DateFormatService);
    private routingService = inject(ChartRoutingService);
    private assetFilterService = inject(SpAssetBrowserService);
    private chartRegistryService = inject(ChartRegistry);

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
                return chart.name;
            } else if (column === 'lastModified') {
                return chart.lastModifiedEpochMs;
            } else if (column === 'createdAt') {
                return chart.createdAtEpochMs;
            } else if (column === 'chartType') {
                return chart.widgetType;
            }
            return chart[column];
        };
        this.getCharts();
    }

    getCharts(): void {
        this.dataViewService.getChartSummary().subscribe(chartSummary => {
            this.charts = chartSummary.resources.sort((a, b) =>
                a.name.localeCompare(b.name),
            );
            this.applyChartFilters(this.currentFilterIds);
        });
    }

    openChart(dataView: ChartSummaryDto, editMode: boolean): void {
        this.routingService.navigateToChart(
            editMode && this.hasDataExplorerWritePrivileges,
            dataView.elementId,
        );
    }

    showManageDialog(chartSummary: ChartSummaryDto) {
        this.dataViewService
            .getChart(chartSummary.elementId)
            .subscribe(chart => {
                const resource: ManageableChart = {
                    ...chart,
                    baseAppearanceConfig: { ...chart.baseAppearanceConfig },
                    name: chart.baseAppearanceConfig.widgetTitle,
                    description: '',
                };
                const resourceConfig: ObjectManageDialogResourceConfig<ManageableChart> =
                    {
                        resourceLabel: 'Chart',
                        nameLabel: 'Chart title',
                        descriptionLabel: 'Chart description',
                        nameProperty: 'name',
                        assetLinkType: 'chart',
                        assetLinkCheckboxLabel:
                            'Add the current chart to an existing asset',
                        saveResource: resource => {
                            resource.baseAppearanceConfig.widgetTitle =
                                resource.name;
                            const chartResource: Partial<ManageableChart> = {
                                ...resource,
                            };
                            delete chartResource.name;
                            delete chartResource.description;
                            return this.dataViewService.updateChart(
                                chartResource as DataExplorerWidgetModel,
                            );
                        },
                    };

                const dialogRef = this.dialogService.open(
                    ObjectManageDialogComponent,
                    {
                        panelType: PanelType.SLIDE_IN_PANEL,
                        title: this.translateService.instant('Manage'),
                        width: '50vw',
                        data: {
                            objectInstanceId: chart.elementId,
                            resource,
                            saveMode: 'immediate',
                            resourceConfig,
                            headerTitle:
                                this.translateService.instant('Manage Chart ') +
                                chart.baseAppearanceConfig.widgetTitle,
                        },
                    },
                );

                dialogRef.afterClosed().subscribe(refresh => {
                    if (refresh) {
                        this.getCharts();
                    }
                });
            });
    }

    deleteChart(chart: ChartSummaryDto) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '600px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete chart "{{chartTitle}}"?',
                    {
                        chartTitle: chart.name ?? '',
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
                    .deleteChart(chart.elementId)
                    .subscribe(() => {
                        this.getCharts();
                    });
            }
        });
    }

    cloneChart(chartSummary: ChartSummaryDto) {
        this.dataViewService
            .getChart(chartSummary.elementId)
            .subscribe(chart => {
                this.dataViewService.cloneChart(chart).subscribe(() => {
                    this.getCharts();
                });
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

    getChartTypeIcon(chart: ChartSummaryDto): string {
        return this.chartRegistryService.getChartTemplate(chart.widgetType)
            .icon;
    }

    getChartTypeName(chart: ChartSummaryDto): string {
        return this.chartRegistryService.getChartTemplate(chart.widgetType)
            .label;
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    isLegacyMultiSourceChart(chart: ChartSummaryDto): boolean {
        return chart.multiSourceChart;
    }

    requiresAttention(chart: DataExplorerWidgetModel): boolean {
        return chart?.healthStatus === 'REQUIRES_ATTENTION';
    }
}
