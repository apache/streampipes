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

import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { RestService } from '../../services/rest.service';
import { DashboardConfiguration } from '../../model/dashboard-configuration.model';

@Component({
    selector: 'sp-asset-dashboard-overview',
    templateUrl: './dashboard-overview.component.html',
    styleUrls: ['./dashboard-overview.component.css'],
})
export class AssetDashboardOverviewComponent implements OnInit {
    @Output() selectedDashboard = new EventEmitter<DashboardConfiguration>();
    @Output() createDashboard = new EventEmitter<void>();

    dashboardConfigs: DashboardConfiguration[] = [];

    constructor(private restService: RestService) {}

    ngOnInit() {
        this.getDashboards();
    }

    getImageUrl(dashboardConfig: DashboardConfiguration) {
        return this.restService.getImageUrl(
            dashboardConfig.imageInfo.imageName,
        );
    }

    getDashboards() {
        this.restService.getDashboards().subscribe(response => {
            console.log(response);
            this.dashboardConfigs = response;
        });
    }

    openDashboard(dashboardConfig: DashboardConfiguration) {
        this.selectedDashboard.emit(dashboardConfig);
    }

    deleteDashboard(dashboardId: string) {
        this.restService.deleteDashboard(dashboardId).subscribe(response => {
            this.getDashboards();
        });
    }
}
