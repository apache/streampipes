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