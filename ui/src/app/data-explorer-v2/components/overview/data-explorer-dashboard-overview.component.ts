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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { DataExplorerEditDataViewDialogComponent } from '../../dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import { IDataViewDashboard } from '../../models/dataview-dashboard.model';
import { DataViewDashboardService } from '../../services/data-view-dashboard.service';

@Component({
    selector: 'sp-data-explorer-dashboard-overview',
    templateUrl: './data-explorer-dashboard-overview.component.html',
    styleUrls: ['./data-explorer-dashboard-overview.component.css']
})
export class DataExplorerDashboardOverviewComponent implements OnInit {

    @Input() dataViewDashboards: IDataViewDashboard[];
    @Output() reloadDashboardsEmitter = new EventEmitter<void>();
    @Output() selectDashboardEmitter = new EventEmitter<IDataViewDashboard>();

    dataSource = new MatTableDataSource<IDataViewDashboard>();
    displayedColumns: string[] = ['name', 'open', 'openWindow', 'edit', 'delete'];

    constructor(private dashboardService: DataViewDashboardService,
                public dialog: MatDialog) {

    }

    ngOnInit(): void {
        this.dataSource.data = this.dataViewDashboards;
    }

    openNewDataViewDialog() {
        const dataViewDashboard = {} as IDataViewDashboard;
        dataViewDashboard.widgets = [];

        this.openDataViewModificationDialog(true, dataViewDashboard);
    }

    openDataViewModificationDialog(createMode: boolean, dashboard: IDataViewDashboard) {
        const dialogRef = this.dialog.open(DataExplorerEditDataViewDialogComponent, {
            width: '70%',
            panelClass: 'custom-dialog-container'
        });
        dialogRef.componentInstance.createMode = createMode;
        dialogRef.componentInstance.dashboard = dashboard;

        dialogRef.afterClosed().subscribe(result => {
            this.reloadDashboardsEmitter.emit();
        });
    }

    openEditDataViewDialog(dashboard: IDataViewDashboard) {
        this.openDataViewModificationDialog(false, dashboard);
    }

    openDeleteDashboardDialog(dashboard: IDataViewDashboard) {
        // TODO add confirm dialog
        this.dashboardService.deleteDashboard(dashboard).subscribe(result => {
            this.reloadDashboardsEmitter.emit();
        });
    }

    showDashboard(dashboard: IDataViewDashboard) {
        this.selectDashboardEmitter.emit(dashboard);
    }

    openExternalDashboard(dashboard: IDataViewDashboard) {

    }

}
