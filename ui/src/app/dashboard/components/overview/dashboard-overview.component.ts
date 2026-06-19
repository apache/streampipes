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

import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    CurrentUserService,
    SpBasicViewComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import { SpDashboardRoutes } from '../../dashboard.breadcrumb';
import { DashboardOverviewTableComponent } from './dashboard-overview-table/dashboard-overview-table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { MatButton } from '@angular/material/button';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { ChartRoutingService } from '../../../chart-shared/services/chart-routing.service';

@Component({
    selector: 'sp-dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatButton,
        DashboardOverviewTableComponent,
        TranslatePipe,
    ],
})
export class DashboardOverviewComponent implements OnInit, OnDestroy {
    displayedColumns: string[] = ['name', 'actions'];
    hasDashboardWritePrivileges = false;

    @ViewChild(DashboardOverviewTableComponent)
    dashboardOverview: DashboardOverviewTableComponent;

    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private routingService = inject(ChartRoutingService);

    private user$: Subscription;

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDashboardRoutes.BASE),
        );
        this.user$ = this.currentUserService.user$.subscribe(_user => {
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
        });
    }

    openNewDashboardDialog() {
        this.routingService.navigateToDashboard(true, 'create');
    }

    ngOnDestroy() {
        this.user$?.unsubscribe();
    }
}
