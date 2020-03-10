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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EventSchema } from '../../../../connect/schema-editor/model/EventSchema';
import { DataResult } from '../../../../core-model/datalake/DataResult';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';

@Component({
  selector: 'sp-data-explorer-table-widget',
  templateUrl: './table-widget.component.html',
  styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseDataExplorerWidget implements OnInit, OnDestroy {


  @ViewChild(MatSort, {static: true}) sort: MatSort;

  availableColumns: string[];
  selectedColumns: string[];

  dataSource = new MatTableDataSource();

  constructor(private dataLakeRestService: DatalakeRestService) {
    super();
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort;
    this.availableColumns = this.getPropertyKeys(this.dataExplorerWidget.dataLakeMeasure.eventSchema);

    // Reduce selected columns when more then 6
    this.selectedColumns = this.availableColumns.length > 6 ? this.availableColumns.slice(0, 5) : this.availableColumns;

    this.updateData();

  }



  updateData() {
    this.setShownComponents(false, false, true);

    this.dataLakeRestService.getDataAutoAggergation(
      this.dataExplorerWidget.dataLakeMeasure.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime())
      .subscribe(
      (res: DataResult) => {

        this.dataSource.data = this.transformData(res);
      }
    );
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

  setSelectedColumn(selectedColumns: string[]) {
    this.selectedColumns = selectedColumns;
  }

  ngOnDestroy(): void {
    this.dataSource.data = [];
  }



}
