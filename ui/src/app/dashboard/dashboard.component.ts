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
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { Tuple2 } from '../core-model/base/Tuple2';
import { EditModeService } from './services/edit-mode.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserPrivilege } from '../_enums/user-privilege.enum';

@Component({
    selector: 'dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

    selectedDashboard: Dashboard;
    selectedIndex = 0;
    dashboardsLoaded = false;
    dashboardTabSelected = false;

    editMode = false;

    dashboards: Dashboard[];
    hasDashboardWritePrivileges = false;

    routeParams: any;

    constructor(private dashboardService: DashboardService,
                private refreshDashboardService: RefreshDashboardService,
                private editModeService: EditModeService,
                private route: ActivatedRoute,
                private router: Router,
                private authService: AuthService) {}

    ngOnInit() {
        this.hasDashboardWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_DASHBOARD);
        this.route.params.subscribe(params => {
            this.getDashboards(params.id);
        });
        this.refreshDashboardService.refreshSubject.subscribe(currentDashboardId => {
            this.getDashboards(currentDashboardId);
        });
        this.editModeService.editModeSubject.subscribe(editMode => {
            this.editMode = editMode;
        });

    }

    openDashboard(data: Tuple2<Dashboard, boolean>) {
        const index = this.dashboards.indexOf(data.a);
        this.editMode = data.b;
        this.selectDashboard((index + 1));
    }

    selectDashboard(index: number) {
        this.selectedIndex = index;
        if (index === 0) {
            this.dashboardTabSelected = false;
            this.router.navigate(['dashboard']);
        } else {
            this.dashboardTabSelected = true;
            this.selectedDashboard = this.dashboards[(index - 1)];
            this.router.navigate(['dashboard', this.selectedDashboard._id]);
        }
    }

    protected getDashboards(currentDashboardId?: string) {
        this.dashboardsLoaded = false;
        this.dashboardService.getDashboards().subscribe(data => {
            this.dashboards = data.sort((a, b) => a.name.localeCompare(b.name));
            if (currentDashboardId) {
                const currentDashboard = this.dashboards.find(d => d._id === currentDashboardId);
                this.selectDashboard(this.dashboards.indexOf(currentDashboard) + 1);
            } else {
                this.selectedIndex = 0;
            }
            this.dashboardsLoaded = true;
        });
    }

    toggleEditMode() {
        this.editMode = ! (this.editMode);
    }
}
