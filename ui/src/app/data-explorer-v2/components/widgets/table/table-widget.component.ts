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

import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DateRange } from '../../../../core-model/datalake/DateRange';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { IDataViewDashboardItem } from '../../../models/dataview-dashboard.model';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';

@Component({
  selector: 'sp-data-explorer-table-widget',
  templateUrl: './table-widget.component.html',
  styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseDataExplorerWidget implements OnInit, OnDestroy, OnChanges  {

  @Input()
  viewDateRange: DateRange;

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  @Output()
  removeWidgetCallback: EventEmitter<boolean> = new EventEmitter();

  availableColumns: string[] = ['time', 'count', 'randomText', 'randomNumber', 'timestamp'];
  selectedColumns: string[] = ['time'];

  dataSource = new MatTableDataSource();

  constructor(private dataLakeRestService: DatalakeRestService) {
    super();
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort;

    this.updateData();


  }

  updateData() {
    this.dataLakeRestService.getDataAutoAggergation(
      this.dataExplorerWidget.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime()).subscribe(
      (res: DataResult) => {

        this.dataSource.data = this.transformData(res);
      }
    );
  }

  transformData(data: DataResult) {
    const result = [];
    data.rows.forEach(row =>
      result.push(this.createTableObject(data.headers, row))
    );

    return result;
  }

  createTableObject(keys, values) {
    const object = {};
    keys.forEach((key, index) => {
      object[key] = values[index];
    });
    return object;
  }

  setSelectedColumn(selectedColumns: string[]) {
    this.selectedColumns = selectedColumns;
  }

  ngOnDestroy(): void {
    this.dataSource.data = [];
  }

  ngOnChanges(changes: SimpleChanges) {
    this.viewDateRange = changes.viewDateRange.currentValue;
    this.updateData();
  }

  removeWidget() {
    this.removeWidgetCallback.emit(true);
  }

}
