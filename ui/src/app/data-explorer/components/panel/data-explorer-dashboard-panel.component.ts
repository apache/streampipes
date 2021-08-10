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

import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Observable, Subscription} from 'rxjs';
import {DataExplorerAddVisualizationDialogComponent} from '../../dialogs/add-widget/data-explorer-add-visualization-dialog.component';
import {DataViewDataExplorerService} from '../../services/data-view-data-explorer.service';
import {RefreshDashboardService} from '../../services/refresh-dashboard.service';
import {DataExplorerWidgetModel, DataLakeMeasure} from "../../../core-model/gen/streampipes-model";
import {DataExplorerDashboardGridComponent} from "../grid/data-explorer-dashboard-grid.component";
import {MatDrawer} from "@angular/material/sidenav";
import {Tuple2} from "../../../core-model/base/Tuple2";
import {Dashboard, DashboardItem, TimeSettings} from "../../../dashboard/models/dashboard.model";

@Component({
  selector: 'sp-data-explorer-dashboard-panel',
  templateUrl: './data-explorer-dashboard-panel.component.html',
  styleUrls: ['./data-explorer-dashboard-panel.component.css']
})
export class DataExplorerDashboardPanelComponent implements OnInit {

  @Input()
  dashboard: Dashboard;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  timeSettings: TimeSettings;

  @Input('editMode')
  editMode: boolean;

  @Output('editModeChange')
  editModeChange: EventEmitter<boolean> = new EventEmitter();

  @ViewChild('dashboardGrid') dashboardGrid: DataExplorerDashboardGridComponent;
  @ViewChild('designerDrawer') designerDrawer: MatDrawer;

  public items: Dashboard[];

  protected subscription: Subscription;

  widgetIdsToRemove: string[] = [];
  widgetsToUpdate: Map<string, DataExplorerWidgetModel> = new Map<string, DataExplorerWidgetModel>();

  currentlyConfiguredWidget: DataExplorerWidgetModel;
  dataLakeMeasure: DataLakeMeasure;

  constructor(private dataViewDataExplorerService: DataViewDataExplorerService,
              public dialog: MatDialog,
              private refreshDashboardService: RefreshDashboardService) {
  }

  public ngOnInit() {

  }

  addWidget(): void {
    const dialogRef = this.dialog.open(DataExplorerAddVisualizationDialogComponent, {
      width: '70%',
      height: '500px',
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe(widget => {
      if (widget) {
        this.addWidgetToDashboard(widget);
      }
    });
  }

  addWidgetToDashboard(widget: DataExplorerWidgetModel) {
    const dashboardItem = {} as DashboardItem;
    dashboardItem.widgetId = widget._id;
    dashboardItem.id = widget._id;
    dashboardItem.widgetType = widget.widgetType;
    dashboardItem.cols = 3;
    dashboardItem.rows = 4;
    dashboardItem.x = 0;
    dashboardItem.y = 0;
    this.dashboard.widgets.push(dashboardItem);
    this.dashboardGrid.loadWidgetConfig(widget._id);
  }

  updateDashboard(closeEditMode?: boolean) {
    this.dataViewDataExplorerService.updateDashboard(this.dashboard).subscribe(result => {
      this.refreshDashboardService.notify(this.dashboard._id);
      // TODO delete widgets
      this.dashboardGrid.updateAllWidgets();
        // if (this.widgetsToUpdate.size > 0) {
        //     forkJoin(this.prepareWidgetUpdates()).subscribe(result => {
        //           this.closeEditModeAndReloadDashboard(closeEditMode);
        //     });
        // } else {
        //     this.deleteWidgets();
        //     this.closeEditModeAndReloadDashboard(false);
        // }
    });
  }

  closeEditModeAndReloadDashboard(closeEditMode: boolean) {
    if (closeEditMode) {
      this.editModeChange.emit(!(this.editMode));
    }
    this.refreshDashboardService.notify(this.dashboard._id);
  }

  prepareWidgetUpdates(): Array<Observable<any>> {
    const promises: Array<Observable<any>> = [];
    this.widgetsToUpdate.forEach((widget, key) => {
      promises.push(this.dataViewDataExplorerService.updateWidget(widget));
    });

    return promises;
  }

  discardChanges() {
    this.editModeChange.emit(!(this.editMode));
    this.refreshDashboardService.notify(this.dashboard._id);
  }

  removeAndQueueItemForDeletion(widget: DashboardItem) {
    this.dashboard.widgets.splice(this.dashboard.widgets.indexOf(widget), 1);
    this.widgetIdsToRemove.push(widget.id);
  }

  updateAndQueueItemForDeletion(dataExlporerWidget: DataExplorerWidgetModel) {
    this.widgetsToUpdate.set(dataExlporerWidget._id, dataExlporerWidget);
  }

  deleteWidgets() {
    this.widgetIdsToRemove.forEach(widgetId => {
      this.dataViewDataExplorerService.deleteWidget(widgetId).subscribe();
    });
  }

  toggleGrid(gridVisible: boolean) {
    this.dashboardGrid.toggleGrid();
  }

  closeDrawer() {
    this.designerDrawer.close();
    this.dashboardGrid.options.api.optionsChanged();
  }

  updateCurrentlyConfiguredWidget(currentWidget: Tuple2<DataExplorerWidgetModel, DataLakeMeasure>) {
    this.widgetsToUpdate.set(currentWidget.a._id, currentWidget.a);
    this.currentlyConfiguredWidget = currentWidget.a;
    this.dataLakeMeasure = currentWidget.b;
    this.designerDrawer.open();
  }
}
