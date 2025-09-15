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
import { AdapterDescription } from '@streampipes/platform-services';

@Component({
    selector: 'sp-table-pagination',
    templateUrl: './sp-table-pagination.component.html',
    styleUrls: ['./sp-table-pagination.component.scss'],
    standalone: false,
})
export class SpTablePaginationComponent implements AfterViewInit {
    @ContentChildren(MatHeaderRowDef) headerRowDefs: QueryList<MatHeaderRowDef>;
    @ContentChildren(MatRowDef) rowDefs: QueryList<
        MatRowDef<AdapterDescription>
    >;
    @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;
    @ContentChild(MatNoDataRow) noDataRow: MatNoDataRow;

    @ViewChild(MatTable, { static: true }) table: MatTable<AdapterDescription>;
    @Input() columns: string[] = [];

    @ViewChild('paginator') paginator: MatPaginator;

    dataSource = new MatTableDataSource<AdapterDescription>([]);
    pageSize = 20;
    totalItems = 1000000; // Optional: Use if backend returns total count
    last_key = undefined;
    currentPage = 0;

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
    }
    loadData(pageIndex: number) {
        console.log('LOAD DATA');
        //const pageSize = this.paginator.pageSize;
        //const pageIndex = this.paginator.pageIndex;

        // NOTE: Replace with actual offset logic if needed (e.g., startkey)
        //const offset = undefined;
        const startkey = this.startKeyMap.get(pageIndex) || null;

        this.adapterService
            .getAdaptersPaginated(startkey, this.pageSize + 1)
            .subscribe({
                next: (data: AdapterDescription[]) => {
                    this.dataSource.data = data;
                    console.log(data);
                    this.last_key = data[data.length - 1].createdAt;
                    console.log(this.last_key);

                    if (data.length > this.pageSize) {
                        const nextStartKey = data[this.pageSize - 1].createdAt;
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
