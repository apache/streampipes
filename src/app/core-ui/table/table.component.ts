/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {BaseChartComponent} from '../chart/baseChart.component';

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

    transformData(data: any[], xKey: String): any[] {
        return data;
    }

    transformGroupedDate(data: Map<string, any[]>, xKey: String): Map<string, any[]> {
        return data;
    }

    displayData(transformedData: any[], yKeys: String[]) {
        this.displayedColumns = Object.assign([], yKeys);
        this.displayedColumns.unshift(this.xKey);

        this.dataSource.data = transformedData;
    }

    displayGroupedData(transformedData: Map<string, any[]>, yKeys: String[]) {
        this.displayedColumns = Object.assign([], yKeys);
        this.displayedColumns.unshift(this.xKey);

        if (this.selectedGroup === undefined) {
            this.selectedGroup = this.getGroupKeys()[0];
        }
        this.dataSource.data = transformedData[this.selectedGroup];

    }

    stopDisplayData() {
        this.dataSource.data = []
    }

    getGroupKeys() {
       return Object.keys(this.transformedData);
    }


    selectGroup(value) {
        this.selectedGroup = value;
        if (this.selectedGroup !== undefined) {
            this.dataSource.data = this.transformedData[this.selectedGroup];
        }
    }
}