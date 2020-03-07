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

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {Dashboard} from "../../models/dashboard.model";
import {MatTableDataSource} from "@angular/material/table";
import {MatDialog} from "@angular/material/dialog";
import {DashboardService} from "../../services/dashboard.service";
import {EditDashboardDialogComponent} from "../../dialogs/edit-dashboard/edit-dashboard-dialog.component";
import {Tuple2} from "../../../core-model/base/Tuple2";

@Component({
    selector: 'dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.css']
})
export class DashboardOverviewComponent implements OnInit {

    @Input() dashboards: Array<Dashboard>;
    @Output() reloadDashboardsEmitter = new EventEmitter<void>();
    @Output() selectDashboardEmitter = new EventEmitter<Tuple2<Dashboard, boolean>>();

    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = ['name', 'open', 'openWindow', 'settings', 'edit', 'delete'];

    constructor(private dashboardService: DashboardService,
                public dialog: MatDialog) {

    }

    ngOnInit(): void {
        this.dataSource.data = this.dashboards;
    }

    openNewDashboardDialog() {
        let dashboard = {} as Dashboard;
        dashboard.widgets = [];

        this.openDashboardModificationDialog(true, dashboard);
    }

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        const dialogRef = this.dialog.open(EditDashboardDialogComponent, {
            width: '70%',
            panelClass: 'custom-dialog-container'
        });
        dialogRef.componentInstance.createMode = createMode;
        dialogRef.componentInstance.dashboard = dashboard;

        dialogRef.afterClosed().subscribe(result => {
            this.reloadDashboardsEmitter.emit();
        });
    }

    openEditDashboardDialog(dashboard: Dashboard) {
        this.openDashboardModificationDialog(false, dashboard);
    }

    openDeleteDashboardDialog(dashboard: Dashboard) {
        // TODO add confirm dialog
        this.dashboardService.deleteDashboard(dashboard).subscribe(result => {
            this.reloadDashboardsEmitter.emit();
        });
    }

    showDashboard(dashboard: Dashboard, openInEditMode: boolean) {
        let data: Tuple2<Dashboard, boolean> = {} as Tuple2<Dashboard, boolean>;
        data.a = dashboard;
        data.b = openInEditMode;
        this.selectDashboardEmitter.emit(data);
    }

    openExternalDashboard(dashboard: Dashboard) {

    }

}