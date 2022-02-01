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

import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, zip } from 'rxjs';
import { RefreshDashboardService } from '../../services/refresh-dashboard.service';
import { DataExplorerDashboardGridComponent } from '../grid/data-explorer-dashboard-grid.component';
import { MatDrawer } from '@angular/material/sidenav';
import { Tuple2 } from '../../../core-model/base/Tuple2';
import {
  Dashboard,
  TimeSettings,
  DataExplorerWidgetModel,
  DataLakeMeasure,
  ClientDashboardItem,
  DataViewDataExplorerService
} from '@streampipes/platform-services';
import { DataExplorerDesignerPanelComponent } from '../designer-panel/data-explorer-designer-panel.component';
import { TimeSelectionService } from '../../services/time-selection.service';

@Component({
  selector: 'sp-data-explorer-dashboard-panel',
  templateUrl: './data-explorer-dashboard-panel.component.html',
  styleUrls: ['./data-explorer-dashboard-panel.component.css']
})
export class DataExplorerDashboardPanelComponent implements OnInit {

  @Input() dashboard: Dashboard;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input() timeSettings: TimeSettings;
  @Input() editMode: boolean;
  @Input() timeRangeVisible: boolean;

  @Output() editModeChange: EventEmitter<boolean> = new EventEmitter();

  @Output() resetDashboardChanges: EventEmitter<boolean> = new EventEmitter();

  @ViewChild('dashboardGrid') dashboardGrid: DataExplorerDashboardGridComponent;
  @ViewChild('designerDrawer') designerDrawer: MatDrawer;
  @ViewChild('designerPanel') designerPanel: DataExplorerDesignerPanelComponent;

  public items: Dashboard[];

  widgetIdsToRemove: string[] = [];
  widgetsToUpdate: Map<string, DataExplorerWidgetModel> = new Map<string, DataExplorerWidgetModel>();

  currentlyConfiguredWidget: DataExplorerWidgetModel;
  newWidgetMode = false;
  currentlyConfiguredWidgetId: string;
  dataLakeMeasure: DataLakeMeasure;

  showDesignerPanel = false;

  constructor(private dataViewDataExplorerService: DataViewDataExplorerService,
              public dialog: MatDialog,
              private refreshDashboardService: RefreshDashboardService,
              private timeSelectionService: TimeSelectionService) {
  }

  public ngOnInit() {
  }

  triggerResize() {
    window.dispatchEvent(new Event('resize'));
  }

  addWidget(widgetConfig: Tuple2<DataLakeMeasure, DataExplorerWidgetModel>): void {
    this.dataLakeMeasure = widgetConfig.a;
    this.dataViewDataExplorerService.saveWidget(widgetConfig.b).subscribe(response => {
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
    this.dataViewDataExplorerService.updateDashboard(this.dashboard).subscribe(result => {
      this.dashboard._rev = result._rev;
      if (this.widgetIdsToRemove.length > 0) {
        const observables = this.deleteWidgets();
        zip(...observables).subscribe(() => {
          this.widgetIdsToRemove.forEach(id => {
            this.dashboardGrid.configuredWidgets.delete(id);
          });

          this.afterDashboardChange();
        });
      } else {
        this.afterDashboardChange();
      }

    });
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
    const index = this.dashboard.widgets.findIndex(item => item.id === widget._id);
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
    return this.widgetIdsToRemove.map(widgetId => {
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
    this.resetDashboardChanges.emit(true);
  }

  createWidget() {
    this.dataLakeMeasure = new DataLakeMeasure();
    this.currentlyConfiguredWidget = new DataExplorerWidgetModel();
    this.currentlyConfiguredWidget['@class'] = 'org.apache.streampipes.model.datalake.DataExplorerWidgetModel';
    this.currentlyConfiguredWidget.baseAppearanceConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.widgetTitle = 'New Widget';
    this.currentlyConfiguredWidget.dataConfig = {};
    this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor = '#FFFFFF';
    this.newWidgetMode = true;
    this.showDesignerPanel = true;
    this.newWidgetMode = true;
  }

  closeDesignerPanel() {
    this.showDesignerPanel = false;
    this.currentlyConfiguredWidget = undefined;
    this.dataLakeMeasure = undefined;
    this.currentlyConfiguredWidgetId = undefined;
  }
}
