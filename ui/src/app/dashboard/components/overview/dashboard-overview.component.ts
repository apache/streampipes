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
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    PanelType,
    SpBasicViewComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import { SpDashboardRoutes } from '../../dashboard.breadcrumb';
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import { DashboardOverviewTableComponent } from './dashboard-overview-table/dashboard-overview-table.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Subscription, tap } from 'rxjs';
import { MatButton } from '@angular/material/button';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';

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

    private dialogService = inject(DialogService);
    private dashboardService = inject(DashboardService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private translateService = inject(TranslateService);

    private user$: Subscription;

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDashboardRoutes.BASE),
        );
        this.user$ = this.currentUserService.user$.subscribe(user => {
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
        });
    }

    openNewDashboardDialog2() {
        const dashboard = this.makeDashboard();
        const resourceConfig: ObjectManageDialogResourceConfig<Dashboard> = {
            resourceLabel: 'Dashboard',
            nameLabel: 'Dashboard title',
            descriptionLabel: 'Dashboard description',
            idProperty: 'elementId',
            nameProperty: 'name',
            assetLinkType: 'dashboard',
            assetLinkCheckboxLabel:
                'Add the current dashboard to an existing asset',
            saveResource: resource =>
                this.dashboardService.saveDashboard(resource).pipe(
                    tap(savedDashboard => {
                        Object.assign(resource, savedDashboard);
                    }),
                ),
        };

        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('New dashboard'),
            width: '50vw',
            data: {
                createMode: true,
                resource: dashboard,
                saveMode: 'immediate',
                resourceConfig,
                headerTitle: this.translateService.instant('New dashboard'),
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.dashboardOverview.getDashboards();
            }
        });
    }

    openNewDashboardDialog() {
        this.openNewDashboardDialog2();
    }

    private makeDashboard(): Dashboard {
        const dashboard: Dashboard = {
            dashboardGeneralSettings: {
                chartOverrides: {
                    hideToolbox: false,
                },
                defaultViewMode: 'grid',
                globalTimeEnabled: true,
                gridRowHeightPx: 90,
            },
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
            gridColumns: 12,
        };

        return dashboard;
    }

    ngOnDestroy() {
        this.user$?.unsubscribe();
    }
}
