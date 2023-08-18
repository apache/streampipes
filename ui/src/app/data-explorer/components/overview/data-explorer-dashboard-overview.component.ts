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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { DataExplorerEditDataViewDialogComponent } from '../../dialogs/edit-dashboard/data-explorer-edit-data-view-dialog.component';
import {
    Dashboard,
    DataViewDataExplorerService,
} from '@streampipes/platform-services';
import {
    CurrentUserService,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { UserRole } from '../../../_enums/user-role.enum';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { Router } from '@angular/router';
import { SpDataExplorerRoutes } from '../../data-explorer.routes';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-data-explorer-dashboard-overview',
    templateUrl: './data-explorer-dashboard-overview.component.html',
    styleUrls: ['./data-explorer-dashboard-overview.component.scss'],
})
export class DataExplorerDashboardOverviewComponent
    implements OnInit, OnDestroy
{
    dataSource = new MatTableDataSource<Dashboard>();
    displayedColumns: string[] = [];
    dashboards: Dashboard[] = [];

    isAdmin = false;

    hasDataExplorerWritePrivileges = false;
    hasDataExplorerDeletePrivileges = false;

    authSubscription: Subscription;

    constructor(
        private dataViewService: DataViewDataExplorerService,
        private dashboardService: DataViewDataExplorerService,
        public dialogService: DialogService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private router: Router,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDataExplorerRoutes.BASE),
        );
        this.authSubscription = this.currentUserService.user$.subscribe(
            user => {
                this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                    UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
                );
                this.hasDataExplorerDeletePrivileges = this.authService.hasRole(
                    UserPrivilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW,
                );
                this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
                this.displayedColumns = ['name', 'actions'];
            },
        );

        this.getDashboards();
    }

    ngOnDestroy() {
        if (this.authSubscription) {
            this.authSubscription.unsubscribe();
        }
    }

    getDashboards() {
        this.dataViewService.getDataViews().subscribe(data => {
            this.dashboards = data.sort((a, b) => a.name.localeCompare(b.name));
            this.dataSource.data = this.dashboards;
        });
    }

    openNewDataViewDialog() {
        const dataViewDashboard: Dashboard = {};
        dataViewDashboard.dashboardGeneralSettings = {};
        dataViewDashboard.widgets = [];
        dataViewDashboard.name = '';

        this.openDataViewModificationDialog(true, dataViewDashboard);
    }

    openDataViewModificationDialog(createMode: boolean, dashboard: Dashboard) {
        const dialogRef = this.dialogService.open(
            DataExplorerEditDataViewDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: createMode ? 'New Data View' : 'Edit Data View',
                width: '70vw',
                data: {
                    createMode: createMode,
                    dashboard: dashboard,
                },
            },
        );

        dialogRef.afterClosed().subscribe(result => {
            this.getDashboards();
        });
    }

    showPermissionsDialog(dashboard: Dashboard) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage permissions',
                width: '50vw',
                data: {
                    objectInstanceId: dashboard._id,
                    headerTitle:
                        'Manage permissions for dashboard ' + dashboard.name,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.getDashboards();
            }
        });
    }

    openEditDataViewDialog(dashboard: Dashboard) {
        this.openDataViewModificationDialog(false, dashboard);
    }

    openDeleteDashboardDialog(dashboard: Dashboard) {
        this.dashboardService.deleteDashboard(dashboard).subscribe(() => {
            this.getDashboards();
        });
    }

    showDashboard(dashboard: Dashboard) {
        this.router.navigate(['dataexplorer/', dashboard._id]);
    }

    editDashboard(dashboard: Dashboard) {
        this.router.navigate(['dataexplorer/', dashboard._id], {
            queryParams: { action: 'edit' },
        });
    }
}
