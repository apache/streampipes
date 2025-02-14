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
import { SpDataExplorerOverviewDirective } from '../data-explorer-overview.directive';
import { MatTableDataSource } from '@angular/material/table';
import {
    ChartService,
    DataExplorerWidgetModel,
} from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    CurrentUserService,
    DialogService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../../services/auth.service';
import { DataExplorerRoutingService } from '../../../../data-explorer-shared/services/data-explorer-routing.service';
import { DataExplorerSharedService } from '../../../../data-explorer-shared/services/data-explorer-shared.service';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-explorer-overview-table',
    templateUrl: './data-explorer-overview-table.component.html',
    styleUrls: ['../data-explorer-overview.component.scss'],
})
export class SpDataExplorerDataViewOverviewComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<DataExplorerWidgetModel>();
    displayedColumns: string[] = [];
    charts: DataExplorerWidgetModel[] = [];
    filteredCharts: DataExplorerWidgetModel[] = [];

    @Output()
    resourceCountEmitter: EventEmitter<number> = new EventEmitter();

    constructor(
        private dataViewService: ChartService,
        private dataExplorerDashboardService: DataExplorerSharedService,
        public dialogService: DialogService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        routingService: DataExplorerRoutingService,
        private dialog: MatDialog,
        private translateService: TranslateService,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.displayedColumns = ['name', 'actions'];
        this.getDataViews();
    }

    getDataViews(): void {
        this.dataViewService.getAllCharts().subscribe(widgets => {
            this.charts = widgets.sort((a, b) =>
                a.baseAppearanceConfig.widgetTitle.localeCompare(
                    b.baseAppearanceConfig.widgetTitle,
                ),
            );
            this.resourceCountEmitter.emit(this.charts.length);
            this.applyChartFilters();
        });
    }

    openDataView(dataView: DataExplorerWidgetModel, editMode: boolean): void {
        this.routingService.navigateToDataView(editMode, dataView.elementId);
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
                    'Are you sure you want to delete this chart?',
                ),
                subtitle: this.translateService.instant(
                    'The chart will be removed from all dashboards as well. This action cannot be undone!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                okTitle: this.translateService.instant('Delete chart'),
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.dataViewService
                    .deleteChart(dataView.elementId)
                    .subscribe(() => {
                        this.getDataViews();
                    });
            }
        });
    }

    applyChartFilters(elementIds: Set<string> = new Set<string>()): void {
        this.filteredCharts = this.charts.filter(a => {
            if (elementIds.size === 0) {
                return true;
            } else {
                return elementIds.has(a.elementId);
            }
        });
        this.dataSource.data = this.filteredCharts;
    }
}
