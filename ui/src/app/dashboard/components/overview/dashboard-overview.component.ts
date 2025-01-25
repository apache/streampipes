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

import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { UserRole } from '../../../_enums/user-role.enum';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { SpDashboardRoutes } from '../../dashboard.routes';

@Component({
    selector: 'sp-dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.scss'],
})
export class DashboardOverviewComponent implements OnInit {
    displayedColumns: string[] = [];

    isAdmin = false;
    hasDashboardWritePrivileges = false;

    constructor(
        public dialog: MatDialog,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

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

    applyDashboardFilters(elements: Set<string>): void {}
}
