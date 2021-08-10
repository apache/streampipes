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

import {Component, OnInit, ViewChild} from '@angular/core';
import {DataViewDataExplorerService} from './services/data-view-data-explorer.service';
import {RefreshDashboardService} from './services/refresh-dashboard.service';
import {DataExplorerDashboardPanelComponent} from "./components/panel/data-explorer-dashboard-panel.component";
import {Dashboard, TimeSettings} from "../dashboard/models/dashboard.model";

@Component({
  selector: 'sp-data-explorer',
  templateUrl: './data-explorer.component.html',
  styleUrls: ['./data-explorer.component.css']
})
export class DataExplorerComponent implements OnInit {

  selectedDataViewDashboard: Dashboard;
  selectedIndex = 0;
  dashboardsLoaded = false;
  dashboardTabSelected = false;

  editMode = true;
  gridVisible: boolean = true;

  dataViewDashboards: Dashboard[];

  @ViewChild('dashboardPanel') dashboardPanel: DataExplorerDashboardPanelComponent;

  constructor(private dataViewService: DataViewDataExplorerService,
              private refreshDashboardService: RefreshDashboardService) {
  }


  public ngOnInit() {
    this.getDashboards();
    this.refreshDashboardService.refreshSubject.subscribe(currentDashboardId => {
      this.getDashboards(currentDashboardId);
    });

  }

  openDashboard(dashboard: Dashboard) {
    const index = this.dataViewDashboards.indexOf(dashboard);
    this.selectDashboard((index + 1));
  }

  selectDashboard(index: number) {
    this.selectedIndex = index;
    if (index === 0) {
      this.dashboardTabSelected = false;
    } else {
      this.dashboardTabSelected = true;
      this.applyTimeSettings(this.dataViewDashboards[(index - 1)]);
      this.selectedDataViewDashboard = this.dataViewDashboards[(index - 1)];
      console.log(this.selectedDataViewDashboard);
    }
  }

  applyTimeSettings(dashboard: Dashboard) {
    if (!dashboard.dashboardTimeSettings) {
      const currentTime = new Date().getTime();
      dashboard.dashboardTimeSettings = {
        startTime: currentTime - 100000 * 60000,
        endTime: currentTime,
        dynamicSelection: -1
      };
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
    this.editMode = !(this.editMode);
  }

  updateDateRange(timeSettings: TimeSettings) {
    this.selectedDataViewDashboard.dashboardTimeSettings = timeSettings;
  }

  saveDashboard() {
    this.dashboardPanel.updateDashboard(false);
  }

  addVisualization() {
    this.dashboardPanel.addWidget();
  }

  toggleGrid() {
    this.dashboardPanel.toggleGrid(this.gridVisible);
  }
}
