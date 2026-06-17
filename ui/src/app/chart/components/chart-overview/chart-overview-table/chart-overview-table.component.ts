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
import { MatProgressSpinner } from '@angular/material/progress-spinner';

type ManageableChart = DataExplorerWidgetModel & {
    name: string;
    description: string;
};

type ChartOverviewRow = ChartSummaryDto & {
    chartTypeIcon: string;
    chartTypeName: string;
    createdAtLabel: string;
    lastModifiedLabel: string;
    showLegacyWarning: boolean;
    showRequiresAttentionWarning: boolean;
    showDataCyId: string;
    editDataCyId: string;
    manageDataCyId: string;
    deleteDataCyId: string;
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
        MatProgressSpinner,
        TranslatePipe,
    ],
})
export class ChartOverviewTableComponent implements OnInit, OnDestroy {
    @Input()
    hasDataExplorerWritePrivileges: boolean;

    @ViewChild(MatSort)
    set sort(sort: MatSort | undefined) {
        this._sort = sort;
        if (sort) {
            this.dataSource.sort = sort;
        }
    }

    dataSource = new MatTableDataSource<ChartOverviewRow>();
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
    readonly nameSearchConfig = {
        enabled: true,
        placeholder: 'Search charts',
    };
    isLoading = false;
    charts: ChartOverviewRow[] = [];
    filteredCharts: ChartOverviewRow[] = [];

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
    private _sort?: MatSort;
    private chartTypeMetadata = new Map<
        string,
        { icon: string; label: string }
    >();

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
        this.isLoading = true;
        this.dataViewService.getChartSummary().subscribe({
            next: chartSummary => {
                this.charts = chartSummary.resources
                    .map(chart => this.toChartOverviewRow(chart))
                    .sort((a, b) => a.name.localeCompare(b.name));
                this.applyChartFilters(this.currentFilterIds);
            },
            complete: () => {
                this.isLoading = false;
            },
            error: () => {
                this.isLoading = false;
            },
        });
    }

    ngOnDestroy(): void {
        this.assetFilter$?.unsubscribe();
    }

    openChart(dataView: ChartSummaryDto, editMode: boolean): void {
        this.routingService.navigateToChart(
            editMode && this.hasDataExplorerWritePrivileges,
            dataView.elementId,
        );
    }

    showManageDialog(chartSummary: ChartSummaryDto) {
        this.withChart(chartSummary, chart => {
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
        this.withChart(chartSummary, chart => {
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
        if (this._sort) {
            this.dataSource.sort = this._sort;
        }
        this.dataSource.data = this.filteredCharts;
    }

    private withChart(
        chartSummary: ChartSummaryDto,
        callback: (chart: DataExplorerWidgetModel) => void,
    ): void {
        this.dataViewService
            .getChart(chartSummary.elementId)
            .subscribe(chart => {
                callback(chart);
            });
    }

    private getChartTypeMetadata(widgetType: string): {
        icon: string;
        label: string;
    } {
        const cached = this.chartTypeMetadata.get(widgetType);
        if (cached) {
            return cached;
        }

        const template = this.chartRegistryService.getChartTemplate(widgetType);
        const metadata = {
            icon: template?.icon ?? 'insert_chart',
            label: template?.label ?? widgetType,
        };
        this.chartTypeMetadata.set(widgetType, metadata);
        return metadata;
    }

    private toChartOverviewRow(chart: ChartSummaryDto): ChartOverviewRow {
        const typeMetadata = this.getChartTypeMetadata(chart.widgetType);
        const sanitizedName = chart.name.replaceAll(' ', '');

        return {
            ...chart,
            chartTypeIcon: typeMetadata.icon,
            chartTypeName: typeMetadata.label,
            createdAtLabel:
                chart.createdAtEpochMs !== null
                    ? this.dateFormatService.formatDate(chart.createdAtEpochMs)
                    : '–',
            lastModifiedLabel:
                chart.lastModifiedEpochMs !== null
                    ? this.dateFormatService.formatDate(
                          chart.lastModifiedEpochMs,
                      )
                    : '–',
            showLegacyWarning: !!chart.multiSourceChart,
            showRequiresAttentionWarning:
                chart.healthStatus === 'REQUIRES_ATTENTION',
            showDataCyId: `show-data-view-${sanitizedName}`,
            editDataCyId: `edit-data-view-${sanitizedName}`,
            manageDataCyId: `open-manage-permissions-${sanitizedName}`,
            deleteDataCyId: `delete-data-view-${chart.name}`,
        };
    }
}
