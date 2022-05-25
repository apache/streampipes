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

import {
  Component,
  EventEmitter,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { Observable, zip } from "rxjs";
import { DataExplorerDashboardGridComponent } from "../grid/data-explorer-dashboard-grid.component";
import { MatDrawer } from "@angular/material/sidenav";
import { Tuple2 } from "../../../core-model/base/Tuple2";
import {
  Dashboard,
  TimeSettings,
  DataExplorerWidgetModel,
  DataLakeMeasure,
  ClientDashboardItem,
  DataViewDataExplorerService,
} from "@streampipes/platform-services";
import { DataExplorerDesignerPanelComponent } from "../designer-panel/data-explorer-designer-panel.component";
import { TimeSelectionService } from "../../services/time-selection.service";
import { AuthService } from "../../../services/auth.service";
import { UserPrivilege } from "../../../_enums/user-privilege.enum";
import { ActivatedRoute, Router } from "@angular/router";
import { combineLatest } from "rxjs";

@Component({
  selector: "sp-data-explorer-dashboard-panel",
  templateUrl: "./data-explorer-dashboard-panel.component.html",
  styleUrls: ["./data-explorer-dashboard-panel.component.css"],
})
export class DataExplorerDashboardPanelComponent implements OnInit {
  dashboardLoaded = false;
  dashboard: Dashboard;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  timeSettings: TimeSettings;

  editMode: boolean = false;
  timeRangeVisible: boolean = true;

  @Output()
  editModeChange: EventEmitter<boolean> = new EventEmitter();

  @ViewChild("dashboardGrid")
  dashboardGrid: DataExplorerDashboardGridComponent;
  @ViewChild("designerDrawer")
  designerDrawer: MatDrawer;
  @ViewChild("designerPanel")
  designerPanel: DataExplorerDesignerPanelComponent;

  hasDataExplorerWritePrivileges = false;
  hasDataExplorerDeletePrivileges = false;

  public items: Dashboard[];

  widgetIdsToRemove: string[] = [];
  widgetsToUpdate: Map<string, DataExplorerWidgetModel> = new Map<
    string,
    DataExplorerWidgetModel
  >();

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
  ) {
    combineLatest([this.route.params, this.route.url]).subscribe(
      ([params, url]) => {
        if (url.length > 1 && url[1].path == 'edit') {
          this.editMode = true;
        }
        this.getDashboard(params.id);
      }
    );
  }

  public ngOnInit() {
    this.authService.user$.subscribe((user) => {
      this.hasDataExplorerWritePrivileges = this.authService.hasRole(
        UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW
      );
      this.hasDataExplorerDeletePrivileges = this.authService.hasRole(
        UserPrivilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW
      );
    });
  }

  triggerResize() {
    window.dispatchEvent(new Event("resize"));
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
    this.dashboardGrid.loadWidgetConfig(widget._id, true);
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
              this.dashboardGrid.configuredWidgets.delete(id);
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
    this.dashboardGrid.updateAllWidgets();
    this.editModeChange.emit(false);
    this.closeDesignerPanel();
  }

  startEditMode(widgetModel: DataExplorerWidgetModel) {
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
  }

  createWidget() {
    this.dataLakeMeasure = new DataLakeMeasure();
    this.currentlyConfiguredWidget = new DataExplorerWidgetModel();
    this.currentlyConfiguredWidget["@class"] =
      "org.apache.streampipes.model.datalake.DataExplorerWidgetModel";
    this.currentlyConfiguredWidget.baseAppearanceConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.widgetTitle =
      "New Widget";
    this.currentlyConfiguredWidget.dataConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor =
      "#FFFFFF";
    this.currentlyConfiguredWidget.baseAppearanceConfig.textColor = "#3e3e3e";
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

  getDashboard(dashboardId: string) {
    this.dataViewService.getDataViews().subscribe((data) => {
      this.dashboard = data.filter(
        (dashboard) => dashboard._id === dashboardId
      )[0];
      this.timeSettings = this.dashboard.dashboardTimeSettings;
      this.dashboardLoaded = true;
    });
  }

  goBackToOverview() {
    this.router.navigate(["dataexplorer/"]);
  }
}
