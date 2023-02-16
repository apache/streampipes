/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Component, OnInit } from '@angular/core';
import { Dashboard, DashboardService } from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';

@Component({
    templateUrl: './standalone-dashboard.component.html',
    styleUrls: ['./standalone-dashboard.component.css'],
})
export class StandaloneDashboardComponent implements OnInit {
    dashboard: Dashboard;
    dashboardReady = false;

    constructor(
        private activatedRoute: ActivatedRoute,
        private dashboardService: DashboardService,
    ) {}

    ngOnInit(): void {
        this.activatedRoute.params.subscribe(params => {
            if (params['dashboardId']) {
                const dashboardId = params['dashboardId'];
                this.dashboardService
                    .getDashboard(dashboardId)
                    .subscribe(dashboard => {
                        this.dashboard = dashboard;
                        this.dashboardReady = true;
                    });
            }
        });
    }
}
