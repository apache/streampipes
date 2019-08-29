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

import {Component, Input} from '@angular/core';
import {MatSnackBar} from '@angular/material';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';

@Component({
    selector: 'sp-datalake-table',
    templateUrl: './datalake-table.component.html',
    styleUrls: ['./datalake-table.component.css']
})
export class DatalakeTableComponent {

    @Input() set index(value: string) {
        this._index = value;
        this.loadData();
    }
    data;
    _index: string;

    currentPage: number = 0;
    maxPage: number = 0;

    itemsPerPage = 10;

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

    }



    paging(page) {
        this.restService.getDataPage(this._index, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                } else {
                    this.openSnackBar('No data found on page ' + page);
                }
            });
    }

    loadData() {
        this.restService.getDataPageWithoutPage(this._index,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                }
            }
        );
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }

    handleItemsPerPageChange(value) {
        this.itemsPerPage = value;
        this.loadData()
    }

    handleNextPage() {
        this.paging(this.currentPage + 1);
    }

    handlePreviousPage() {
        if(this.currentPage >= 0)
            this.paging(this.currentPage - 1);
    }

    handleFirstPage() {
        this.paging(0);
    }

    handleLastPage() {
        this.loadData()
    }
}