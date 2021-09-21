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

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, QueryList, SimpleChanges, ViewChildren } from '@angular/core';
import { GridsterItemComponent, GridType } from 'angular-gridster2';
import { GridsterInfo } from '../../../dashboard/models/gridster-info.model';
import { IDataViewDashboardConfig } from '../../models/dataview-dashboard.model';
import { ResizeService } from '../../services/resize.service';
import { DataExplorerWidgetModel, DataLakeMeasure } from '../../../core-model/gen/streampipes-model';
import { Dashboard, TimeSettings } from '../../../dashboard/models/dashboard.model';
import { DataViewDataExplorerService } from '../../../platform-services/apis/data-view-data-explorer.service';

@Component({
  selector: 'sp-data-explorer-dashboard-grid',
  templateUrl: './data-explorer-dashboard-grid.component.html',
  styleUrls: ['./data-explorer-dashboard-grid.component.scss']
})
export class DataExplorerDashboardGridComponent implements OnInit, OnChanges {

  @Input()
  editMode: boolean;

  _dashboard: Dashboard;

  configuredWidgets: Map<string, DataExplorerWidgetModel> = new Map<string, DataExplorerWidgetModel>();
  dataLakeMeasures: Map<string, DataLakeMeasure> = new Map<string, DataLakeMeasure>();

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  timeSettings: TimeSettings;

  @Output() deleteCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() configureWidgetCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();

  options: IDataViewDashboardConfig;
  loaded = false;

  currentlyConfiguredWidgetId: string;

  @ViewChildren(GridsterItemComponent) gridsterItemComponents: QueryList<GridsterItemComponent>;

  constructor(private resizeService: ResizeService,
              private dataViewDataExplorerService: DataViewDataExplorerService) {

  }

  ngOnInit(): void {
    this.options = {
      disablePushOnDrag: true,
      draggable: { enabled: this.editMode },
      gridType: GridType.VerticalFixed,
      minCols: 8,
      maxCols: 8,
      minRows: 4,
      fixedRowHeight: 100,
      fixedColWidth: 100,
      margin: 5,
      displayGrid: this.editMode ? 'always' : 'none',
      resizable: { enabled: this.editMode },
      itemResizeCallback: ((item, itemComponent) => {
        this.resizeService.notify({
          gridsterItem: item,
          gridsterItemComponent: itemComponent
        } as GridsterInfo);
      }),
      itemInitCallback: ((item, itemComponent) => {
        this.resizeService.notify({
          gridsterItem: item,
          gridsterItemComponent: itemComponent
        } as GridsterInfo);
        window.dispatchEvent(new Event('resize'));
      })
    };
  }

  @Input() set dashboard(dashboard: Dashboard) {
    this._dashboard = dashboard;
    this.loadWidgetConfigs();
  }

  get dashboard() {
    return this._dashboard;
  }

  loadWidgetConfigs() {
    this.dashboard.widgets.forEach(widget => {
      this.loadWidgetConfig(widget.id);
    });

  }

  loadWidgetConfig(widgetId: string, setCurrentlyConfigured?: boolean) {
    this.dataViewDataExplorerService.getWidget(widgetId).subscribe(response => {
      this.configuredWidgets.set(widgetId, response);
      //this.dataViewDataExplorerService.getPersistedDataStream(response.pipelineId, response.measureName).subscribe(ps => {
      this.dataLakeMeasures.set(widgetId, response.dataConfig.sourceConfigs[0].measure);
      if (setCurrentlyConfigured) {
        this.propagateWidgetSelection(this.configuredWidgets.get(widgetId));
      }
      //});
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editMode'] && this.options) {
      this.options.draggable.enabled = this.editMode;
      this.options.resizable.enabled = this.editMode;
      this.options.displayGrid = this.editMode ? 'always' : 'none';
      this.options.api.optionsChanged();
    }
  }

  propagateItemRemoval(widget: DataExplorerWidgetModel) {
    this.deleteCallback.emit(widget);
  }

  propagateItemUpdate(dashboardWidget: DataExplorerWidgetModel) {
    this.updateCallback.emit(dashboardWidget);
  }

  propagateWidgetSelection(configuredWidget: DataExplorerWidgetModel) {
    this.configureWidgetCallback.emit(configuredWidget);
    this.currentlyConfiguredWidgetId = configuredWidget._id;
    this.options.api.optionsChanged();
  }

  toggleGrid() {
    this.options.displayGrid = this.options.displayGrid === 'none' ? 'always' : 'none';
    this.options.api.optionsChanged();
  }

  updateAllWidgets() {
    this.configuredWidgets.forEach((value, key) => {
      this.dataViewDataExplorerService.updateWidget(value).subscribe(response => {
        value._rev = response._rev;
      });
    });
  }

  startEditMode(value: DataExplorerWidgetModel) {
    this.startEditModeEmitter.emit(value);
    this.currentlyConfiguredWidgetId = value._id;
  }

}
