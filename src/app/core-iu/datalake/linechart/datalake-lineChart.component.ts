import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {time} from '@ngtools/webpack/src/benchmark';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';
import {MatSnackBar} from '@angular/material';
import {FormControl} from '@angular/forms';
import {InfoResult} from '../../../core-model/datalake/InfoResult';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'sp-datalake-lineChart',
    templateUrl: './datalake-lineChart.component.html',
    styleUrls: ['./datalake-lineChart.component.css']
})
export class DatalakeLineChartComponent {

    @Input() set index(value: string) {
        this._index = value;
        this.loadData();
    }
    data;
    _index: string;

    yAxesKey = undefined;
    xAxesKey = 'time';

    currentPage: number = 0;
    maxPage: number = 0;

    itemsPerPage = 50;

    myControl = new FormControl();
    dataKeys: string[] = [];

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
                    this.setDataKeys(res.events[0])
                }
            }
        );
    }

    selectKey(value) {
        this.yAxesKey = value;
        console.log(value)
    }

    setDataKeys(event) {
        this.dataKeys = [];
        for (let key in event) {
            if (typeof event[key] == 'number') {
                this.dataKeys.push(key)
            }
        }
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

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }


}