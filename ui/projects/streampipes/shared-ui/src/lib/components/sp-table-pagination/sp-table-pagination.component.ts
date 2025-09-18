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
import { BehaviorSubject } from 'rxjs';
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

    @Input() sort: MatSort;
    //Necessary if other refreshs than based on sort are crucial
    @Input() refresh: BehaviorSubject<boolean>;
    @Input() filter: BehaviorSubject<string>;

    @Input() fetchDataFn: (
        startKey?: any,
        pageSize?: number,
    ) => Observable<T[]>;
    // This is necessary in case the element names in the HTML and the keys used for sorting follow different naming conventions or are composite keys. (E.g., created in HTML and the database key is createdAt)
    // Provide the information as sortmap
    @Input() getViewFn: (sort: string) => string;

    dataSource = new MatTableDataSource<T>([]);
    pageSize = 20;
    totalItems = 1000000;
    last_key = undefined;
    currentPage = 0;
    propertyName = 'createdAt';
    isNextDisabled: boolean = false;
    filtering = '';

    startKeyMap: Map<number, number | null> = new Map();

    private sortInitialized = false;

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['sort'] && this.sort && !this.sortInitialized) {
            this.sort.sortChange.subscribe((sortChange: Sort) => {
                this.propertyName = this.getViewFn(sortChange.active);
                this.resetPagination();
            });
            this.sortInitialized = true;
        }

        if (changes.refresh) {
            this.refresh.subscribe(() => {
                this.loadData(this.currentPage);
            });
        }

        if (changes.filter) {
            this.filter.subscribe(() => {
                console.log('NEW FIlter Value', this.filter.value);
                this.filtering = this.filter.value;
                if (this.filter.value['category'] != '') {
                    this.propertyName = this.getViewFn('category');
                }
                this.resetPagination();
                this.loadData(0);
            });
        }
    }

    ngAfterViewInit() {
        this.loadData(0);
    }
    resetPagination() {
        this.startKeyMap.clear();
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
        const startkey = this.startKeyMap.get(pageIndex) || null;
        console.log('LOAD DARA start key', startkey);
        console.log(this.propertyName);

        this.fetchDataFn(startkey, this.pageSize + 1).subscribe({
            next: (data: T[]) => {
                if (data.length < this.pageSize) {
                    this.dataSource.data = data;
                    this.totalItems = data.length + pageIndex * this.pageSize;
                } else {
                    const trimmedData = data.slice(0, this.pageSize);
                    this.dataSource.data = trimmedData;
                    this.totalItems = data.length + pageIndex * this.pageSize;
                }

                //this.last_key = data[data.length - 1][this.propertyName];

                if (data.length > this.pageSize) {
                    let nextStartKey;

                    if (Array.isArray(this.propertyName)) {
                        nextStartKey = this.propertyName.map(
                            prop => data[this.pageSize][prop],
                        );
                    } else {
                        nextStartKey = data[this.pageSize][this.propertyName];
                    }
                    const nextStartKeyString = Array.isArray(nextStartKey)
                        ? JSON.stringify(nextStartKey)
                        : nextStartKey;
                    this.startKeyMap.set(pageIndex + 1, nextStartKeyString);
                    this.dataSource.data = data.slice(0, this.pageSize);
                }
                console.log(this.startKeyMap);
            },
            error: err => {
                console.error('Failed to fetch paginated data', err);
            },
        });
    }
}
