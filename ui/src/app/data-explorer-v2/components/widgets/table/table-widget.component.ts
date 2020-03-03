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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { DateRange } from '../../../../core-model/datalake/DateRange';
import { DatalakeRestService } from '../../../../core-services/datalake/datalake-rest.service';
import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { MatTableDataSource } from "@angular/material/table";
import { DataResult } from "../../../../core-model/datalake/DataResult";

@Component({
  selector: 'sp-data-explorer-table-widget',
  templateUrl: './table-widget.component.html',
  styleUrls: ['./table-widget.component.css']
})
export class TableWidgetComponent extends BaseDataExplorerWidget implements OnInit, OnDestroy {

  @Input()
  viewDateRange: DateRange;

  displayedColumns: string[] = ['time', 'count', 'randomNumber', 'timestamp'];

  dataSource = new MatTableDataSource();
  // item: any;
  //
  // selectedProperty: string;

  constructor(private dataLakeRestService: DatalakeRestService) {
    super();
  }

  ngOnInit(): void {
    const groupbyUnit = 's';
    const groupbyValue = 1;

    this.dataLakeRestService.getData(
      this.dataExplorerWidget.measureName, this.viewDateRange.startDate.getTime(), this.viewDateRange.endDate.getTime(),
      groupbyUnit, groupbyValue).subscribe(
      (res: DataResult) => {

          const transformed = this.transformData(res, 'time');

          this.dataSource.data = this.displayData(transformed, 'time', this.displayedColumns);
      }
    );
  }


  transformData(data: DataResult, xKey: string): DataResult {
    const tmp = [];
    data.rows.forEach(row =>
      tmp.push(this.createTableObject(data.headers, row))
    );
    data.rows = tmp;

    return data;
  }


    displayData(transformedData: DataResult, xKey: string, yKeys: string[]) {
        this.displayedColumns = Object.assign([], yKeys);
        // this.displayedColumns.unshift(xKey);

        // this.dataSource.data = transformedData.rows;
        return transformedData.rows;
    }


  createTableObject(keys, values) {
    const object = {};
    keys.forEach((key, index) => {
      object[key] = values[index];
    });
    return object;
  }


  ngOnDestroy(): void {
  }

  isNumber(item: any): boolean {
    return false;
  }
}
