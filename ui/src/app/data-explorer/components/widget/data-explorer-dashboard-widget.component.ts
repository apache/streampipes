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
import { GridsterItemComponent } from 'angular-gridster2';
import { DateRange } from '../../../core-model/datalake/DateRange';
import { DataExplorerWidgetModel, DataLakeMeasure } from '../../../core-model/gen/streampipes-model';
import { DataDownloadDialog } from '../datadownloadDialog/dataDownload.dialog';
import { DashboardItem, TimeSettings } from '../../../dashboard/models/dashboard.model';
import { DataViewDataExplorerService } from '../../../platform-services/apis/data-view-data-explorer.service';
import { interval, Observable } from 'rxjs';
import { takeWhile } from 'rxjs/operators';

@Component({
  selector: 'sp-data-explorer-dashboard-widget',
  templateUrl: './data-explorer-dashboard-widget.component.html',
  styleUrls: ['./data-explorer-dashboard-widget.component.scss']
})
export class DataExplorerDashboardWidgetComponent implements OnInit {

  @Input()
  dashboardItem: DashboardItem;

  @Input()
  configuredWidget: DataExplorerWidgetModel;

  @Input()
  dataLakeMeasure: DataLakeMeasure;

  @Input()
  editMode: boolean;

  @Input()
  gridsterItemComponent: GridsterItemComponent;

  @Input()
  currentlyConfiguredWidgetId: string;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  timeSettings: TimeSettings;

  @Output() deleteCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() configureWidgetCallback: EventEmitter<DataExplorerWidgetModel>
    = new EventEmitter<DataExplorerWidgetModel>();
  @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();

  title = '';
  widgetLoaded = false;

  msCounter = interval(10);
  timerActive = false;
  loadingTime = 0;

  constructor(private dataViewDataExplorerService: DataViewDataExplorerService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.widgetLoaded = true;
    this.title = this.dataLakeMeasure.measureName;
  }

  removeWidget() {
    this.deleteCallback.emit(this.configuredWidget);
  }

  downloadDataAsFile() {
    this.dialog.open(DataDownloadDialog, {
      width: '600px',
      data: {
        index: this.dataLakeMeasure.measureName,
        date: DateRange.fromTimeSettings(this.timeSettings)
      },
      panelClass: 'custom-dialog-container'
    });
  }

  startEditMode() {
    this.startEditModeEmitter.emit(this.configuredWidget);
  }

  triggerWidgetEditMode() {
    this.configureWidgetCallback.emit(this.configuredWidget);
  }

  startLoadingTimer() {
    this.timerActive = true;
    interval( 10 )
        .pipe(takeWhile(() => this.timerActive))
        .subscribe(value => {
      this.loadingTime = (value * 10 / 1000);
    });
  }

  stopLoadingTimer() {
    this.timerActive = false;
  }

  handleTimer(start: boolean) {
    start ? this.startLoadingTimer() : this.stopLoadingTimer();
  }
}
