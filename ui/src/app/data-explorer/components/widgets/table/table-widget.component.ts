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
import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { TableWidgetModel } from './model/table-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';
import { MatPaginator } from '@angular/material/paginator';

@Component({
    selector: 'sp-data-explorer-table-widget',
    templateUrl: './table-widget.component.html',
    styleUrls: ['./table-widget.component.scss'],
})
export class TableWidgetComponent
    extends BaseDataExplorerWidgetDirective<TableWidgetModel>
    implements OnInit, OnDestroy
{
    @ViewChild(MatSort, { static: true }) sort: MatSort;
    @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

    dataSource = new MatTableDataSource();
    columnNames: string[];

    ngOnInit(): void {
        super.ngOnInit();
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.columnNames = ['time'].concat(
            this.dataExplorerWidget.visualizationConfig.selectedColumns.map(
                c => c.fullDbName,
            ),
        );
    }

    transformData(spQueryResult: SpQueryResult) {
        return spQueryResult.allDataSeries.flatMap(series =>
            series.rows.map(row =>
                this.createTableObject(spQueryResult.headers, row),
            ),
        );
    }

    createTableObject(keys, values) {
        return keys.reduce((object, key, index) => {
            object[key] = values[index];
            return object;
        }, {});
    }

    ngOnDestroy(): void {
        this.dataSource.data = [];
    }

    sortData(event) {
        if (event.direction === 'asc') {
            this.dataSource.data = this.dataSource.data.sort((a, b) =>
                a[event.active] > b[event.active]
                    ? 1
                    : b[event.active] > a[event.active]
                      ? -1
                      : 0,
            );
        }

        if (event.direction === 'desc') {
            this.dataSource.data = this.dataSource.data.sort((a, b) =>
                a[event.active] > b[event.active]
                    ? -1
                    : b[event.active] > a[event.active]
                      ? 1
                      : 0,
            );
        }

        if (event.direction === '') {
            this.dataSource.data = this.dataSource.data.sort((a, b) =>
                a['timestamp'] > b['timestamp']
                    ? 1
                    : b['timestamp'] > a['timestamp']
                      ? -1
                      : 0,
            );
        }
    }

    public refreshView() {
        this.refreshColumns();
    }

    onResize(width: number, height: number) {}

    beforeDataFetched() {
        this.setShownComponents(false, false, true, false);
        this.dataSource.data = [];
    }

    onDataReceived(spQueryResults: SpQueryResult[]) {
        this.columnNames = ['time'].concat(
            this.dataExplorerWidget.visualizationConfig.selectedColumns.map(
                c => c.fullDbName,
            ),
        );
        const transformedData = spQueryResults
            .map(spQueryResult => this.transformData(spQueryResult))
            .flat();
        this.dataSource.data = [...transformedData];
        this.setShownComponents(false, true, false, false);
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {
        this.dataExplorerWidget.visualizationConfig.selectedColumns =
            this.fieldUpdateService.updateFieldSelection(
                this.dataExplorerWidget.visualizationConfig.selectedColumns,
                {
                    addedFields,
                    removedFields,
                    fieldProvider: this.fieldProvider,
                },
                () => true,
            );
        this.refreshColumns();
    }

    refreshColumns(): void {
        this.dataSource.filter =
            this.dataExplorerWidget.visualizationConfig.searchValue;
        this.columnNames = ['time'].concat(
            this.dataExplorerWidget.visualizationConfig.selectedColumns.map(
                c => c.fullDbName,
            ),
        );
    }
}
