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
    SimpleChanges,
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
import { MatSort, Sort } from '@angular/material/sort';

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

    @Input() columns: string[] = [];

    @ViewChild('paginator') paginator: MatPaginator;
    //@ViewChild(MatSort) sort: MatSort;
    @Input() sort: MatSort;
    @Input() fetchDataFn: (
        startKey?: any,
        pageSize?: number,
    ) => Observable<T[]>;

    @Input() getViewFn: (sort: string) => string;

    dataSource = new MatTableDataSource<T>([]);
    pageSize = 20;
    totalItems = 1000000; // Optional: Use if backend returns total count
    last_key = undefined;
    currentPage = 0;
    propertyName = 'createdAt';
    isNextDisabled: boolean = false;

    // Keep track of keys for pagination
    startKeyMap: Map<number, number | null> = new Map();

    private sortInitialized = false;

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['sort'] && this.sort && !this.sortInitialized) {
            this.sort.sortChange.subscribe((sortChange: Sort) => {
                console.log('[Sort Changed]', sortChange);
                this.propertyName = this.getViewFn(sortChange.active);
                this.resetPagination();
                //this.loadData(0);
            });
            this.sortInitialized = true;
        }
    }

    constructor(private adapterService: AdapterService) {
        console.log('[SpTablePagination] Constructor');
    }

    ngOnInit() {
        console.log('[SpTablePagination] ngOnInit');
    }

    ngAfterViewInit() {
        console.log('INIT');
        this.loadData(0); // Initial load
    }
    resetPagination() {
        console.log('RESET Pagination');
        this.startKeyMap.clear();
        console.log(this.startKeyMap);
        this.currentPage = 0;
        this.last_key = null;
        this.paginator.firstPage();
        this.loadData(this.currentPage);
        this.isNextDisabled = false;
        this.totalItems = 1000000;
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
    }
    loadData(pageIndex: number) {
        console.log('LOAD DATA');
        const startkey = this.startKeyMap.get(pageIndex) || null;
        console.log('PageIndex', pageIndex);
        console.log('StartKey', startkey);

        this.fetchDataFn(startkey, this.pageSize + 1).subscribe({
            next: (data: T[]) => {
                console.log('PROPERTY KEY', this.propertyName);

                // Handle pagination logic based on the page size
                if (data.length < this.pageSize) {
                    this.dataSource.data = data;
                    this.totalItems = data.length + pageIndex * this.pageSize;
                } else {
                    const trimmedData = data.slice(0, this.pageSize);
                    this.dataSource.data = trimmedData;
                    this.totalItems = data.length + pageIndex * this.pageSize;
                }

                console.log(data);
                // Get the last key for the current page
                this.last_key = data[data.length - 1][this.propertyName];
                console.log(this.last_key);

                if (data.length > this.pageSize) {
                    // Build the next start key for pagination
                    let nextStartKey;

                    // If propertyName is an array, handle as a composite key
                    if (Array.isArray(this.propertyName)) {
                        nextStartKey = this.propertyName.map(
                            prop => data[this.pageSize][prop],
                        );
                    } else {
                        nextStartKey = data[this.pageSize][this.propertyName];
                    }

                    // Convert the next start key to a string (if it's an array, stringify it)
                    const nextStartKeyString = Array.isArray(nextStartKey)
                        ? JSON.stringify(nextStartKey)
                        : nextStartKey;

                    console.log('NEXT KEY AS ARRAY ?  ', nextStartKeyString);

                    // Update the startKeyMap with the new start key for the next page
                    this.startKeyMap.set(pageIndex + 1, nextStartKeyString);
                    console.log(this.startKeyMap);

                    // Trim the extra item to maintain consistent page size
                    data = data.slice(0, this.pageSize);
                }
            },
            error: err => {
                console.error('Failed to fetch paginated data', err);
            },
        });
    }
}
