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

import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { UserRole } from '../../../_enums/user-role.enum';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { SpDashboardRoutes } from '../../dashboard.routes';
import { Dashboard } from '@streampipes/platform-services';
import { DataExplorerDashboardService } from '../../../dashboard-shared/services/dashboard.service';
import { DashboardOverviewTableComponent } from './dashboard-overview-table/dashboard-overview-table.component';
import { TranslateService } from '@ngx-translate/core';
import { DataExplorerSharedService } from '../../../data-explorer-shared/services/data-explorer-shared.service';

@Component({
    selector: 'sp-dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.scss'],
    standalone: false,
})
export class DashboardOverviewComponent implements OnInit {
    displayedColumns: string[] = [];

    isAdmin = false;
    hasDashboardWritePrivileges = false;
    resourceCount = 0;

    @ViewChild(DashboardOverviewTableComponent)
    dashboardOverview: DashboardOverviewTableComponent;

    public dialog = inject(MatDialog);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(DataExplorerSharedService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private translateService = inject(TranslateService);

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDashboardRoutes.BASE),
        );
        this.currentUserService.user$.subscribe(user => {
            this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
            this.displayedColumns = ['name', 'actions'];
        });
    }

    openNewDashboardDialog() {
        const dataViewDashboard: Dashboard = {
            dashboardGeneralSettings: {},
            widgets: [],
            name: '',
            dashboardLiveSettings: {
                refreshModeActive: false,
                refreshIntervalInSeconds: 10,
                label: this.translateService.instant('Off'),
            },
            metadata: {
                createdAtEpochMs: Date.now(),
                lastModifiedEpochMs: Date.now(),
            },
        };

        this.openDashboardModificationDialog(true, dataViewDashboard);
    }

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        const dialogRef =
            this.dataExplorerDashboardService.openDashboardModificationDialog(
                createMode,
                dashboard,
            );

        dialogRef.afterClosed().subscribe(() => {
            this.dashboardOverview.getDashboards();
        });
    }

    applyDashboardFilters(elementIds: Set<string> = new Set<string>()): void {
        this.dashboardOverview.applyDashboardFilters(elementIds);
    }
}
