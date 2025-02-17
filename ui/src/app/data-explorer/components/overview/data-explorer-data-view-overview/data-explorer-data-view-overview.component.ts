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
import { SpDataExplorerOverviewDirective } from '../data-explorer-overview.directive';
import { MatTableDataSource } from '@angular/material/table';
import {
    Dashboard,
    DataExplorerWidgetModel,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogService,
    ConfirmDialogComponent,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../../services/auth.service';
import { DataExplorerRoutingService } from '../../../services/data-explorer-routing.service';
import { DataExplorerDashboardService } from '../../../services/data-explorer-dashboard.service';
import { MatDialog } from '@angular/material/dialog';

@Component({
    selector: 'sp-data-explorer-data-view-overview',
    templateUrl: './data-explorer-data-view-overview.component.html',
    styleUrls: ['../data-explorer-overview.component.scss'],
})
export class SpDataExplorerDataViewOverviewComponent extends SpDataExplorerOverviewDirective {
    dataSource = new MatTableDataSource<DataExplorerWidgetModel>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];

    constructor(
        private dataViewService: DataViewDataExplorerService,
        private dataExplorerDashboardService: DataExplorerDashboardService,
        public dialogService: DialogService,
        authService: AuthService,
        currentUserService: CurrentUserService,
        routingService: DataExplorerRoutingService,
        private dialog: MatDialog,
    ) {
        super(dialogService, authService, currentUserService, routingService);
    }

    afterInit(): void {
        this.displayedColumns = ['name', 'actions'];
        this.getDataViews();
    }

    getDataViews(): void {
        this.dataViewService.getAllWidgets().subscribe(widgets => {
            widgets = widgets.sort((a, b) =>
                a.baseAppearanceConfig.widgetTitle.localeCompare(
                    b.baseAppearanceConfig.widgetTitle,
                ),
            );
            this.dataSource.data = widgets;
        });
    }

    openDataView(dataView: DataExplorerWidgetModel, editMode: boolean): void {
        this.routingService.navigateToDataView(editMode, dataView.elementId);
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef =
            this.dataExplorerDashboardService.openPermissionsDialog(
                dashboard.elementId,
                `Manage permissions for dashboard ${dashboard.name}`,
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
                title: 'Are you sure you want to delete this data view?',
                subtitle:
                    'The data view will be removed from all dashboards as well. This action cannot be undone!',
                cancelTitle: 'Cancel',
                okTitle: 'Delete Data View',
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.dataViewService
                    .deleteWidget(dataView.elementId)
                    .subscribe(() => {
                        this.getDataViews();
                    });
            }
        });
    }
}
