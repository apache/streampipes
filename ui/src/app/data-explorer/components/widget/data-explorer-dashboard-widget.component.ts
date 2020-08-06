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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { GridsterItem, GridsterItemComponent } from 'angular-gridster2';
import { DateRange } from '../../../core-model/datalake/DateRange';
import { DataExplorerAddVisualizationDialogComponent } from '../../dialogs/add-widget/data-explorer-add-visualization-dialog.component';
import { IDataViewDashboardItem } from '../../models/dataview-dashboard.model';
import { DataViewDataExplorerService } from '../../services/data-view-data-explorer.service';
import {DataExplorerWidgetModel} from "../../../core-model/gen/streampipes-model";

@Component({
  selector: 'sp-data-explorer-dashboard-widget',
  templateUrl: './data-explorer-dashboard-widget.component.html',
  styleUrls: ['./data-explorer-dashboard-widget.component.css']
})
export class DataExplorerDashboardWidgetComponent implements OnInit {

  @Input()
  widget: IDataViewDashboardItem;

  @Input()
  editMode: boolean;

  @Input()
  item: GridsterItem;

  @Input()
  gridsterItemComponent: GridsterItemComponent;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  viewDateRange: DateRange;

  @Output() deleteCallback: EventEmitter<IDataViewDashboardItem> = new EventEmitter<IDataViewDashboardItem>();
  @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();

  widgetLoaded = false;
  configuredWidget: DataExplorerWidgetModel;

  constructor(private dataViewDataExplorerService: DataViewDataExplorerService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.dataViewDataExplorerService.getWidget(this.widget.id).subscribe(response => {
      this.configuredWidget = response;
      this.widgetLoaded = true;
    });
  }

  removeWidget() {
    this.deleteCallback.emit(this.widget);
  }

  editWidget(): void {
    const dialogRef = this.dialog.open(DataExplorerAddVisualizationDialogComponent, {
      width: '70%',
      height: '500px',
      panelClass: 'custom-dialog-container',
      data: {
        'widget': this.configuredWidget
      }
    });

    dialogRef.afterClosed().subscribe(widget => {
      if (widget) {
        this.configuredWidget = widget;
        this.updateCallback.emit(this.configuredWidget);
      }
    });
  }
}
