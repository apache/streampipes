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

import { Component, OnInit, ViewChild } from '@angular/core';
import { DataViewDataExplorerService, Dashboard } from '@streampipes/platform-services';
import { RefreshDashboardService } from './services/refresh-dashboard.service';
import { DataExplorerDashboardPanelComponent } from './components/panel/data-explorer-dashboard-panel.component';
import { Tuple2 } from '../core-model/base/Tuple2';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserPrivilege } from '../_enums/user-privilege.enum';

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

  dataViewDashboards: Dashboard[];

  routeParams: any;

  hasDataExplorerWritePrivileges = false;
  hasDataExplorerDeletePrivileges = false;

  @ViewChild('dashboardPanel') dashboardPanel: DataExplorerDashboardPanelComponent;

  constructor(private dataViewService: DataViewDataExplorerService,
              private refreshDashboardService: RefreshDashboardService,
              private route: ActivatedRoute,
              private authService: AuthService) {
  }


  public ngOnInit() {
    this.authService.user$.subscribe(user => {
      this.hasDataExplorerWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW);
      this.hasDataExplorerDeletePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW);
    });
    this.route.queryParams.subscribe(params => {
      this.routeParams = { startTime: params['startTime'], endTime: params['endTime'], dashboardId: params['dashboardId'] };
      this.getDashboards();
    });
    this.refreshDashboardService.refreshSubject.subscribe(currentDashboardId => {
      this.getDashboards(currentDashboardId);
    });

  }

  openDashboard(dashboard: Tuple2<Dashboard, boolean>) {
    const index = this.dataViewDashboards.indexOf(dashboard.a);
    this.selectDashboard((index + 1), dashboard.b);
  }

  selectDashboardInTab(index: number) {
    if (index !== this.selectedIndex) {
      this.selectDashboard(index);
    }
  }

  selectDashboard(index: number, editMode = false) {
    this.selectedIndex = index;
    if (index === 0) {
      this.dashboardTabSelected = false;
    } else {
      this.dashboardTabSelected = true;
      this.applyTimeSettings(this.dataViewDashboards[(index - 1)]);
      this.selectedDataViewDashboard = this.dataViewDashboards[(index - 1)];
    }
  }

  applyTimeSettings(dashboard: Dashboard) {
    if (this.routeParams.startTime && this.routeParams.endTime) {
      dashboard.dashboardTimeSettings = {
        startTime: this.routeParams.startTime * 1,
        endTime: this.routeParams.endTime * 1,
        dynamicSelection: -1
      };
    } else if (!dashboard.dashboardTimeSettings) {
      const currentTime = new Date().getTime();
      dashboard.dashboardTimeSettings = {
        startTime: currentTime - 100000 * 60000,
        endTime: currentTime,
        dynamicSelection: -1
      };
    }
  }

  findAndSelectDashboard(dashboardId: string) {
    const currentDashboard = this.dataViewDashboards.find(d => d._id === dashboardId);
    this.selectDashboard(this.dataViewDashboards.indexOf(currentDashboard) + 1);
  }

  getDashboards(currentDashboardId?: string) {
    this.dashboardsLoaded = false;
    this.dataViewService.getDataViews().subscribe(data => {
      this.dataViewDashboards = data.sort((a, b) => a.name.localeCompare(b.name));
      if (currentDashboardId) {
        this.findAndSelectDashboard(currentDashboardId);
      } else if (this.routeParams.dashboardId) {
        this.findAndSelectDashboard(this.routeParams.dashboardId);
      } else {
        this.selectedIndex = 0;
      }
      this.dashboardsLoaded = true;
    });
  }


  // resetDashboardChanges() {
  //   this.getDashboards(this.selectedDataViewDashboard._id);
  // }
}
