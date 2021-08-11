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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatTableDataSource} from '@angular/material/table';
import {DataExplorerEditDataViewDialogComponent} from '../../dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import {DataViewDataExplorerService} from '../../services/data-view-data-explorer.service';
import {Dashboard} from "../../../dashboard/models/dashboard.model";
import { Tuple2 } from '../../../core-model/base/Tuple2';

@Component({
    selector: 'sp-data-explorer-dashboard-overview',
    templateUrl: './data-explorer-dashboard-overview.component.html',
    styleUrls: ['./data-explorer-dashboard-overview.component.scss']
})
export class DataExplorerDashboardOverviewComponent implements OnInit {

    @Input() dataViewDashboards: Dashboard[];
    @Output() reloadDashboardsEmitter = new EventEmitter<void>();
    @Output() selectDashboardEmitter = new EventEmitter<Tuple2<Dashboard, boolean>>();

    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = ['name', 'open', 'edit', 'delete'];

    editLabels: boolean;

    constructor(private dashboardService: DataViewDataExplorerService,
                public dialog: MatDialog) {

    }

    ngOnInit(): void {
        this.dataSource.data = this.dataViewDashboards;
        this.editLabels = false;
    }

    openNewDataViewDialog() {
        const dataViewDashboard = {} as Dashboard;
        dataViewDashboard.widgets = [];

        this.openDataViewModificationDialog(true, dataViewDashboard);
    }

    openEditLabelView() {
       this.editLabels = true;
    }

    openDataViewModificationDialog(createMode: boolean, dashboard: Dashboard) {
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

    openEditDataViewDialog(dashboard: Dashboard) {
        this.openDataViewModificationDialog(false, dashboard);
    }

    openDeleteDashboardDialog(dashboard: Dashboard) {
        // TODO add confirm dialog
        this.dashboardService.deleteDashboard(dashboard).subscribe(result => {
            this.reloadDashboardsEmitter.emit();
        });
    }

    showDashboard(dashboard: Dashboard, editMode: boolean) {
        const dashboardSettings: Tuple2<Dashboard, boolean> = {a: dashboard, b: editMode};
        this.selectDashboardEmitter.emit(dashboardSettings);
    }
}
