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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import {BaseChartComponent} from '../chart/baseChart.component';
import {DataResult} from '../../core-model/datalake/DataResult';
import {GroupedDataResult} from '../../core-model/datalake/GroupedDataResult';

@Component({
    selector: 'sp-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.css']
})
export class TableComponent extends BaseChartComponent {

    displayedColumns: String[] = [];
    dataSource = new MatTableDataSource();

    selectedGroup: string = undefined;

    constructor() {
        super();
    }

    transformData(data: DataResult, xKey: String): DataResult {
        let tmp = [];
        data.rows.forEach(row =>
            tmp.push(this.createTableObject(data.headers, row))
        );
        data.rows = tmp;

        return data;
    }

    transformGroupedData(data: GroupedDataResult, xKey: string): GroupedDataResult {
        for (var key in data.dataResults) {
            let dataResult = data.dataResults[key];
            dataResult.rows = this.transformData(dataResult, xKey).rows;
        }
        return data;
    }

    displayData(transformedData: DataResult, yKeys: string[]) {
        this.displayedColumns = Object.assign([], yKeys);
        this.displayedColumns.unshift(this.xKey);

        this.dataSource.data = transformedData.rows;
    }

    displayGroupedData(transformedData: GroupedDataResult, yKeys: string[]) {
        this.displayedColumns = Object.assign([], yKeys);
        this.displayedColumns.unshift(this.xKey);

        if (this.selectedGroup === undefined) {
            this.selectedGroup = this.getGroupKeys()[0];
        }
        this.dataSource.data = transformedData.dataResults[this.selectedGroup].rows;
    }

    stopDisplayData() {
        this.dataSource.data = []
    }

    createTableObject(keys, values) {
        let object = {};
        keys.forEach((key, index) => {
            object[key] = values[index];
        });
        return object;
    }

    getGroupKeys() {
       return Object.keys((this.transformedData as GroupedDataResult).dataResults);
    }

    selectGroup(value) {
        this.selectedGroup = value;
        if (this.selectedGroup !== undefined) {
            this.dataSource.data = (this.transformedData as GroupedDataResult)
                .dataResults[this.selectedGroup].rows;
        }
    }
}