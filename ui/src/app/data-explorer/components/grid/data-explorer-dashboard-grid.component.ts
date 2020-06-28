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
  Component, EventEmitter,
  Input,
  OnChanges,
  OnInit, Output,
  QueryList,
  SimpleChanges,
  ViewChildren
} from '@angular/core';
import { GridsterItemComponent, GridType } from 'angular-gridster2';
import { DataExplorerWidgetModel } from '../../../core-model/datalake/DataExplorerWidgetModel';
import { DateRange } from '../../../core-model/datalake/DateRange';
import { GridsterInfo } from '../../../dashboard/models/gridster-info.model';
import { IDataViewDashboard, IDataViewDashboardConfig, IDataViewDashboardItem } from '../../models/dataview-dashboard.model';
import { DataViewDataExplorerService } from '../../services/data-view-data-explorer.service';
import { RefreshDashboardService } from '../../services/refresh-dashboard.service';
import { ResizeService } from '../../services/resize.service';

@Component({
  selector: 'sp-data-explorer-dashboard-grid',
  templateUrl: './data-explorer-dashboard-grid.component.html',
  styleUrls: ['./data-explorer-dashboard-grid.component.css']
})
export class DataExplorerDashboardGridComponent implements OnInit, OnChanges {

  @Input()
  editMode: boolean;

  @Input()
  dashboard: IDataViewDashboard;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  viewDateRange: DateRange;

  @Output() deleteCallback: EventEmitter<IDataViewDashboardItem> = new EventEmitter<IDataViewDashboardItem>();
  @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();

  options: IDataViewDashboardConfig;
  loaded = false;

  @ViewChildren(GridsterItemComponent) gridsterItemComponents: QueryList<GridsterItemComponent>;

  constructor(private resizeService: ResizeService,
              private dashboardService: DataViewDataExplorerService,
              private refreshDashboardService: RefreshDashboardService) {

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
      resizable: { enabled: this.editMode },
      itemResizeCallback: ((item, itemComponent) => {
        this.resizeService.notify({gridsterItem: item, gridsterItemComponent: itemComponent} as GridsterInfo);
      }),
      itemInitCallback: ((item, itemComponent) => {
        this.resizeService.notify({gridsterItem: item, gridsterItemComponent: itemComponent} as GridsterInfo);
      })
    };
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['editMode'] && this.options) {
      this.options.draggable.enabled = this.editMode;
      this.options.resizable.enabled = this.editMode;
      this.options.displayGrid = this.editMode ? 'always' : 'none';
      this.options.api.optionsChanged();
    }
  }

  propagateItemRemoval(widget: IDataViewDashboardItem) {
    this.deleteCallback.emit(widget);
  }

  propagateItemUpdate(dashboardWidget: DataExplorerWidgetModel) {
    this.updateCallback.emit(dashboardWidget);
  }

}
