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
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AuthService } from '../../../services/auth.service';
import { SpDataExplorerRoutes } from '../../data-explorer.routes';
import { DataExplorerRoutingService } from '../../../data-explorer-shared/services/data-explorer-routing.service';
import { SpDataExplorerDataViewOverviewComponent } from './data-explorer-overview-table/data-explorer-overview-table.component';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-data-explorer-overview',
    templateUrl: './data-explorer-overview.component.html',
    styleUrls: ['./data-explorer-overview.component.scss'],
    standalone: false,
})
export class DataExplorerOverviewComponent implements OnInit, OnDestroy {
    @ViewChild(SpDataExplorerDataViewOverviewComponent)
    chartsOverview: SpDataExplorerDataViewOverviewComponent;

    auth$: Subscription;
    hasDataExplorerWritePrivileges = false;

    private breadcrumbService = inject(SpBreadcrumbService);
    private routingService = inject(DataExplorerRoutingService);
    private currentUserService = inject(CurrentUserService);
    private authService = inject(AuthService);

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpDataExplorerRoutes.BASE),
        );
        this.auth$ = this.currentUserService.user$.subscribe(user => {
            this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
            );
        });
    }

    createNewDataView(): void {
        this.routingService.navigateToDataView(true);
    }

    ngOnDestroy() {
        this.auth$?.unsubscribe();
    }
}
