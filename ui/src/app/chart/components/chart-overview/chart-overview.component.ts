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
    SpAssetBrowserService,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpPageHeaderComponent,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { SpChartRoutes } from '../../chart.breadcrumb';
import { ChartRoutingService } from '../../../chart-shared/services/chart-routing.service';
import { ChartOverviewTableComponent } from './chart-overview-table/chart-overview-table.component';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import { Subscription } from 'rxjs';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { AsyncPipe } from '@angular/common';

@Component({
    selector: 'sp-chart-overview',
    templateUrl: './chart-overview.component.html',
    styleUrls: ['./chart-overview.component.scss'],
    imports: [
        SpBasicViewComponent,
        SpPageHeaderComponent,
        MatButton,
        ChartOverviewTableComponent,
        TranslatePipe,
        AsyncPipe,
    ],
})
export class ChartOverviewComponent implements OnInit, OnDestroy {
    @ViewChild(ChartOverviewTableComponent)
    chartsOverview: ChartOverviewTableComponent;

    auth$: Subscription;
    hasDataExplorerWritePrivileges = false;

    private breadcrumbService = inject(SpBreadcrumbService);
    private routingService = inject(ChartRoutingService);
    private currentUserService = inject(CurrentUserService);
    private authService = inject(AuthService);
    private assetFilterService = inject(SpAssetBrowserService);

    readonly pageHeaderAssetLinkType$ =
        this.assetFilterService.getAssetLinkType$('chart');

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpChartRoutes.BASE),
        );
        this.auth$ = this.currentUserService.user$.subscribe(_user => {
            this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
            );
        });
    }

    createNewChart(): void {
        this.routingService.navigateToCreateChart(true);
    }

    ngOnDestroy() {
        this.auth$?.unsubscribe();
    }
}
