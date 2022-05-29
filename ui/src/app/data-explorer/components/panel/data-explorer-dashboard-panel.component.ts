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

import { Component, EventEmitter, OnInit, Output, ViewChild, } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, Observable, zip } from 'rxjs';
import { DataExplorerDashboardGridComponent } from '../widget-view/grid-view/data-explorer-dashboard-grid.component';
import { MatDrawer } from '@angular/material/sidenav';
import { Tuple2 } from '../../../core-model/base/Tuple2';
import {
  ClientDashboardItem,
  Dashboard,
  DataExplorerWidgetModel,
  DataLakeMeasure,
  DataViewDataExplorerService,
  TimeSettings,
} from '@streampipes/platform-services';
import { DataExplorerDesignerPanelComponent } from '../designer-panel/data-explorer-designer-panel.component';
import { TimeSelectionService } from '../../services/time-selection.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { ActivatedRoute, Router } from '@angular/router';
import { DataExplorerDashboardSlideViewComponent } from '../widget-view/slide-view/data-explorer-dashboard-slide-view.component';

@Component({
  selector: 'sp-data-explorer-dashboard-panel',
  templateUrl: './data-explorer-dashboard-panel.component.html',
  styleUrls: ['./data-explorer-dashboard-panel.component.css'],
})
export class DataExplorerDashboardPanelComponent implements OnInit {
  dashboardLoaded = false;
  dashboard: Dashboard;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  timeSettings: TimeSettings;
  viewMode = 'grid';

  editMode = false;
  timeRangeVisible = true;

  @Output()
  editModeChange: EventEmitter<boolean> = new EventEmitter();

  @ViewChild('dashboardGrid')
  dashboardGrid: DataExplorerDashboardGridComponent;

  @ViewChild('dashboardSlide')
  dashboardSlide: DataExplorerDashboardSlideViewComponent;

  @ViewChild('designerDrawer')
  designerDrawer: MatDrawer;

  @ViewChild('designerPanel')
  designerPanel: DataExplorerDesignerPanelComponent;

  hasDataExplorerWritePrivileges = false;
  hasDataExplorerDeletePrivileges = false;

  public items: Dashboard[];

  widgetIdsToRemove: string[] = [];
  widgetsToUpdate: Map<string, DataExplorerWidgetModel> = new Map<string, DataExplorerWidgetModel>();

  currentlyConfiguredWidget: DataExplorerWidgetModel;
  newWidgetMode = false;
  currentlyConfiguredWidgetId: string;
  dataLakeMeasure: DataLakeMeasure;

  showDesignerPanel = false;

  constructor(
    private dataViewDataExplorerService: DataViewDataExplorerService,
    public dialog: MatDialog,
    private timeSelectionService: TimeSelectionService,
    private authService: AuthService,
    private dashboardService: DataViewDataExplorerService,
    private route: ActivatedRoute,
    private dataViewService: DataViewDataExplorerService,
    private router: Router
  ) { }

  public ngOnInit() {
    combineLatest([this.route.params, this.route.queryParams, this.authService.user$]).subscribe(
      ([params, qp, user]) => {
        this.hasDataExplorerWritePrivileges = this.authService.hasRole(
          UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW
        );
        this.hasDataExplorerDeletePrivileges = this.authService.hasRole(
          UserPrivilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW
        );
        if (qp.action === 'edit' && this.hasDataExplorerWritePrivileges) {
          this.editMode = true;
        }
        const startTime = params.startTime;
        const endTime = params.endTime;
        this.getDashboard(params.id, startTime, endTime);
      }
    );
  }

  triggerResize() {
    window.dispatchEvent(new Event('resize'));
  }

  addWidget(
    widgetConfig: Tuple2<DataLakeMeasure, DataExplorerWidgetModel>
  ): void {
    this.dataLakeMeasure = widgetConfig.a;
    this.dataViewDataExplorerService
      .saveWidget(widgetConfig.b)
      .subscribe((response) => {
        this.addWidgetToDashboard(response);
      });
  }

  addWidgetToDashboard(widget: DataExplorerWidgetModel) {
    // tslint:disable-next-line:no-object-literal-type-assertion
    const dashboardItem = {} as ClientDashboardItem;
    dashboardItem.id = widget._id;
    dashboardItem.cols = 3;
    dashboardItem.rows = 4;
    dashboardItem.x = 0;
    dashboardItem.y = 0;
    this.dashboard.widgets.push(dashboardItem);
    if (this.viewMode === 'grid') {
      this.dashboardGrid.loadWidgetConfig(widget._id, true);
    } else {
      this.dashboardSlide.loadWidgetConfig(widget._id, true);
    }
  }

  persistDashboardChanges() {
    this.dataViewDataExplorerService
      .updateDashboard(this.dashboard)
      .subscribe((result) => {
        this.dashboard._rev = result._rev;
        if (this.widgetIdsToRemove.length > 0) {
          const observables = this.deleteWidgets();
          zip(...observables).subscribe(() => {
            this.widgetIdsToRemove.forEach((id) => {
              if (this.viewMode === 'grid') {
                this.dashboardGrid.configuredWidgets.delete(id);
              } else {
                this.dashboardSlide.configuredWidgets.delete(id);
              }
            });

            this.afterDashboardChange();
          });
        } else {
          this.afterDashboardChange();
        }
      });

    this.editMode = false;
  }

  afterDashboardChange() {
    if (this.viewMode === 'grid') {
      this.dashboardGrid.updateAllWidgets();
    } else {
      this.dashboardSlide.updateAllWidgets();
    }
    this.editModeChange.emit(false);
    this.closeDesignerPanel();
  }

  startEditMode(widgetModel: DataExplorerWidgetModel) {
    this.editMode = true;
    this.editModeChange.emit(true);
    this.updateCurrentlyConfiguredWidget(widgetModel);
  }

  prepareWidgetUpdates(): Observable<any>[] {
    const promises: Observable<any>[] = [];
    this.widgetsToUpdate.forEach((widget, key) => {
      promises.push(this.dataViewDataExplorerService.updateWidget(widget));
    });

    return promises;
  }

  removeAndQueueItemForDeletion(widget: DataExplorerWidgetModel) {
    const index = this.dashboard.widgets.findIndex(
      (item) => item.id === widget._id
    );
    this.dashboard.widgets.splice(index, 1);
    this.widgetIdsToRemove.push(widget._id);
    if (this.currentlyConfiguredWidget._id === widget._id) {
      this.currentlyConfiguredWidget = undefined;
    }
  }

  updateAndQueueItemForDeletion(widget: DataExplorerWidgetModel) {
    this.widgetsToUpdate.set(widget._id, widget);
  }

  deleteWidgets(): Observable<any>[] {
    return this.widgetIdsToRemove.map((widgetId) => {
      return this.dataViewDataExplorerService.deleteWidget(widgetId);
    });
  }

  toggleGrid(gridVisible: boolean) {
    this.dashboardGrid.toggleGrid();
  }

  updateDateRange(timeSettings: TimeSettings) {
    this.timeSettings = timeSettings;
    this.dashboard.dashboardTimeSettings = timeSettings;
    this.timeSelectionService.notify(timeSettings);
  }

  updateCurrentlyConfiguredWidget(currentWidget: DataExplorerWidgetModel) {
    if (currentWidget) {
      this.widgetsToUpdate.set(currentWidget._id, currentWidget);
      this.currentlyConfiguredWidget = currentWidget;
      this.currentlyConfiguredWidgetId = currentWidget._id;
      this.designerPanel.modifyWidgetMode(currentWidget, false);
      this.showDesignerPanel = true;
    } else {
      this.showDesignerPanel = false;
    }
  }

  discardChanges() {
    this.editMode = false;
  }

  triggerEditMode() {
    this.editMode = true;
    this.editModeChange.emit(true);
  }

  createWidget() {
    this.dataLakeMeasure = new DataLakeMeasure();
    this.currentlyConfiguredWidget = new DataExplorerWidgetModel();
    this.currentlyConfiguredWidget['@class'] =
      'org.apache.streampipes.model.datalake.DataExplorerWidgetModel';
    this.currentlyConfiguredWidget.baseAppearanceConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.widgetTitle =
      'New Widget';
    this.currentlyConfiguredWidget.dataConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor =
      '#FFFFFF';
    this.currentlyConfiguredWidget.baseAppearanceConfig.textColor = '#3e3e3e';
    this.newWidgetMode = true;
    this.showDesignerPanel = true;
    this.newWidgetMode = true;
    this.designerPanel.resetIndex();
  }

  closeDesignerPanel() {
    this.showDesignerPanel = false;
    this.currentlyConfiguredWidget = undefined;
    this.dataLakeMeasure = undefined;
    this.currentlyConfiguredWidgetId = undefined;
  }

  deleteDashboard(dashboard: Dashboard) {
    this.dashboardService.deleteDashboard(dashboard).subscribe((result) => {
      this.goBackToOverview();
    });
  }

  getDashboard(dashboardId: string,
               startTime: number,
               endTime: number) {
    this.dataViewService.getDataViews().subscribe((data) => {
      this.dashboard = data.filter(
        (dashboard) => dashboard._id === dashboardId
      )[0];
      this.timeSettings = (startTime && endTime) ? this.overrideTime(+startTime, +endTime) : this.dashboard.dashboardTimeSettings;
      this.dashboardLoaded = true;
    });
  }

  overrideTime(startTime: number,
               endTime: number): TimeSettings {
    return {startTime, endTime, dynamicSelection: -1};
  }

  goBackToOverview() {
    this.router.navigate(['dataexplorer/']);
  }
}
