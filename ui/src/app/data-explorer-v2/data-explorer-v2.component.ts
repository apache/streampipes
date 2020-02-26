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
import { IDataViewDashboard } from './models/dataview-dashboard.model';
import { DataViewDataExplorerService } from './services/data-view-data-explorer.service';
import { RefreshDashboardService } from './services/refresh-dashboard.service';

@Component({
    selector: 'sp-data-explorer-v2',
    templateUrl: './data-explorer-v2.component.html',
    styleUrls: ['./data-explorer-v2.component.css']
})
export class DataExplorerV2Component implements OnInit {

    selectedDataViewDashboard: IDataViewDashboard;
    selectedIndex = 0;
    dashboardsLoaded = false;
    dashboardTabSelected = false;

    editMode = false;

    dataViewDashboards: IDataViewDashboard[];

    constructor(private dataViewService: DataViewDataExplorerService,
                private refreshDashboardService: RefreshDashboardService) {}

    public ngOnInit() {
        this.getDashboards();
        this.refreshDashboardService.refreshSubject.subscribe(currentDashboardId => {
            this.getDashboards(currentDashboardId);
        });

    }

    openDashboard(dashboard: IDataViewDashboard) {
        const index = this.dataViewDashboards.indexOf(dashboard);
        this.selectDashboard((index + 1));
    }

    selectDashboard(index: number) {
        this.selectedIndex = index;
        if (index === 0) {
            this.dashboardTabSelected = false;
        } else {
            this.dashboardTabSelected = true;
            this.selectedDataViewDashboard = this.dataViewDashboards[(index - 1)];
        }
    }

    protected getDashboards(currentDashboardId?: string) {
        this.dashboardsLoaded = false;
        this.dataViewService.getDataViews().subscribe(data => {
            this.dataViewDashboards = data;
            if (currentDashboardId) {
                const currentDashboard = this.dataViewDashboards.find(d => d._id === currentDashboardId);
                this.selectDashboard(this.dataViewDashboards.indexOf(currentDashboard) + 1);
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
