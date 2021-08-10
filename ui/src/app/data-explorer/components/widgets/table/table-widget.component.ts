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

import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {DataResult} from '../../../../core-model/datalake/DataResult';
import {DatalakeRestService} from '../../../../core-services/datalake/datalake-rest.service';
import {BaseDataExplorerWidget} from '../base/base-data-explorer-widget';
import {WidgetConfigurationService} from "../../../services/widget-configuration.service";
import {TableWidgetModel} from "./model/table-widget.model";

@Component({
  selector: 'sp-data-explorer-table-widget',
  templateUrl: './table-widget.component.html',
  styleUrls: ['./table-widget.component.scss']
})
export class TableWidgetComponent extends BaseDataExplorerWidget<TableWidgetModel> implements OnInit, OnDestroy {

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  //availableColumns: EventPropertyUnion[];
  //selectedColumns: EventPropertyUnion[];
  //columnNames: string[];

  dataSource = new MatTableDataSource();

  constructor(protected dataLakeRestService: DatalakeRestService,
              protected dialog: MatDialog,
              widgetConfigurationService: WidgetConfigurationService) {
    super(dataLakeRestService, dialog, widgetConfigurationService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.sort = this.sort;
    //this.columnNames = this.getRuntimeNames(this.selectedColumns);

    //this.updateData();
  }

  transformData(data: DataResult) {
    const result = [];
    if (data.total === 0) {
      this.setShownComponents(true, false, false);
    } else {
      data.rows.forEach(row =>
        result.push(this.createTableObject(data.headers, row))
      );
      this.setShownComponents(false, true, false);
    }

    return result;
  }

  createTableObject(keys, values) {
    const object = {};
    keys.forEach((key, index) => {
      object[key] = values[index];
    });
    return object;
  }



  ngOnDestroy(): void {
    this.dataSource.data = [];
  }


  sortData(event) {
    if (event.direction === 'asc') {
      this.dataSource.data = this.dataSource.data.sort(
        (a, b) => (a[event.active] > b[event.active]) ? 1 : ((b[event.active] > a[event.active]) ? -1 : 0));
    }

    if (event.direction === 'desc') {
      this.dataSource.data = this.dataSource.data.sort(
        (a, b) => (a[event.active] > b[event.active]) ? -1 : ((b[event.active] > a[event.active]) ? 1 : 0));
    }

    if (event.direction === '') {
      this.dataSource.data = this.dataSource.data.sort(
        (a, b) => (a['timestamp'] > b['timestamp']) ? 1 : ((b['timestamp'] > a['timestamp']) ? -1 : 0));
    }
  }

  public refreshData() {
    this.setShownComponents(false, false, true);

    this.dataLakeRestService.getDataAutoAggregation(
        this.dataLakeMeasure.measureName, this.timeSettings.startTime, this.timeSettings.endTime)
        .subscribe(
            (res: DataResult) => {
              this.dataSource.data = this.transformData(res);
            }
        );
  }

  public refreshView() {
    this.dataSource.filter = this.dataExplorerWidget.dataConfig.searchValue;
  }

}
