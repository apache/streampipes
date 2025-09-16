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
    AfterViewInit,
    Component,
    NgModule,
    Input,
    ViewChild,
    ContentChildren,
    ContentChild,
    QueryList,
} from '@angular/core';
import {
    MatColumnDef,
    MatHeaderRowDef,
    MatNoDataRow,
    MatRowDef,
    MatTable,
    MatTableDataSource,
} from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { AdapterService } from '@streampipes/platform-services';
import { Observable } from 'rxjs';
import { MatSort, MatSortHeader } from '@angular/material/sort';

@Component({
    selector: 'sp-table-pagination',
    templateUrl: './sp-table-pagination.component.html',
    styleUrls: ['./sp-table-pagination.component.scss'],
    standalone: false,
})
export class SpTablePaginationComponent<T> implements AfterViewInit {
    @ContentChildren(MatHeaderRowDef) headerRowDefs: QueryList<MatHeaderRowDef>;
    @ContentChildren(MatRowDef) rowDefs: QueryList<MatRowDef<T>>;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild(MatNoDataRow) noDataRow: MatNoDataRow;

    @ViewChild(MatTable, { static: true }) table: MatTable<T>;
    @ContentChild(MatSort) sort: MatSort;

    @ContentChild(MatSortHeader) sort2: MatSortHeader;

    @Input() columns: string[] = [];

    @ViewChild('paginator') paginator: MatPaginator;

    @Input() fetchDataFn: (
        startKey?: any,
        pageSize?: number,
    ) => Observable<T[]>;

    dataSource = new MatTableDataSource<T>([]);
    pageSize = 20;
    totalItems = 1000000; // Optional: Use if backend returns total count
    last_key = undefined;
    currentPage = 0;
    propertyName = 'createdAt';

    // Keep track of keys for pagination
    startKeyMap: Map<number, number | null> = new Map();

    constructor(private adapterService: AdapterService) {
        console.log('[SpTablePagination] Constructor');
    }

    ngOnInit() {
        console.log('[SpTablePagination] ngOnInit');
    }

    ngAfterViewInit() {
        console.log('INIT');
        //this.paginator.page.subscribe(() => this.loadData());
        //this.loadData(0); // Initial load
        //console.log('AFTER DATA LOAD')
        if (this.sort) {
            this.sort.sortChange.subscribe(sortEvent => {
                console.log('Sort changed:', sortEvent);

                // Set the property name based on the active column
                this.propertyName = sortEvent.active;

                // Optionally: Handle direction if your backend supports it
                // You might want to use this.sort.direction somewhere too

                // Reload data when sorting changes
                this.loadData(0);
            });
        }

        this.loadData(0); // Initial load
    }

    onPageChange(event: PageEvent) {
        this.pageSize = event.pageSize;
        this.currentPage = event.pageIndex;
        this.loadData(this.currentPage);
    }

    ngAfterContentInit() {
        this.columnDefs.forEach(columnDef =>
            this.table.addColumnDef(columnDef),
        );
        this.rowDefs.forEach(rowDef => this.table.addRowDef(rowDef));
        this.headerRowDefs.forEach(headerRowDef =>
            this.table.addHeaderRowDef(headerRowDef),
        );
        this.table.setNoDataRow(this.noDataRow);

        console.log('Sort received:', this.sort);
        console.log('Sort received:', this.sort2);
    }
    loadData(pageIndex: number) {
        console.log('LOAD DATA');
        const startkey = this.startKeyMap.get(pageIndex) || null;

        this.fetchDataFn(startkey, this.pageSize + 1).subscribe({
            next: (data: T[]) => {
                if (data.length < this.pageSize) {
                    this.dataSource.data = data;
                } else {
                    const trimmedData = data.slice(0, this.pageSize);
                    this.dataSource.data = trimmedData;
                }
                console.log(data);
                this.last_key = data[data.length - 1][this.propertyName];
                console.log(this.last_key);

                if (data.length > this.pageSize) {
                    const nextStartKey = data[this.pageSize][this.propertyName];
                    this.startKeyMap.set(pageIndex + 1, nextStartKey);
                    console.log(this.startKeyMap);
                    data = data.slice(0, this.pageSize); // Trim the extra item
                }
            },
            error: err => {
                console.error('Failed to fetch paginated data', err);
            },
        });
    }
}
