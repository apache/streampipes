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
        this.loadAllData();
        this.enablePaging = true;
        this.enableItemsPerPage = true;
    }
    data;
    _index: string;

    //Line Chart configs
    yAxesKey = undefined;
    xAxesKey = 'time';
    currentPage: number = 0;
    maxPage: number = 0;
    itemsPerPage = 50;
    enablePaging = false;
    enableItemsPerPage = false;
    isLoadingData = false;

    //index selections
    myControl = new FormControl();
    dataKeys: string[] = [];

    //timeunit selection
    selectedTimeUnit = 'All';

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
        this.isLoadingData = true;
        if (this.selectedTimeUnit === 'All') {
            this.loadAllData();
            this.enablePaging = true;
            this.enableItemsPerPage = true;
        } else {
            this.enablePaging = false;
            this.enableItemsPerPage = false;
            this.loadLastData();
        }
        this.isLoadingData = false;
    }

    loadAllData() {
        this.restService.getDataPageWithoutPage(this._index,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                    this.setDataKeys(res.events[0])
                } else {
                    this.data = undefined;
                }
            }
        );
    }

    loadLastData() {
        let timeunit = '';
        let timevalue = 0;
        let aggregationunit = 'm';
        let aggreagtionvalue = 1;
        if (this.selectedTimeUnit === '24 Hours') {
            timeunit = 'h';
            timevalue = 24;
        } else if (this.selectedTimeUnit === '1 Week') {
            timeunit = 'w';
            timevalue = 1;
        } else if (this.selectedTimeUnit === '1 Month') {
            timeunit = 'w';
            timevalue = 4;
        } else if (this.selectedTimeUnit === '1 Year') {
            timeunit = 'w';
            timevalue = 4 * 12;
        }

        this.restService.getLastData(this._index, timeunit, timevalue, aggregationunit, aggreagtionvalue).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.data = res.events as [];
                    this.setDataKeys(res.events[0]);
                    this.currentPage = undefined;
                } else {
                    this.data = undefined;
                }
            }
        );
    }

    selectKey(value) {
        this.yAxesKey = value;
    }

    setDataKeys(event) {
        this.dataKeys = [];
        for (let key in event) {
            if (typeof event[key] == 'number') {
                this.dataKeys.push(key)
            }
        }
    }

    selectTimeUnit(value) {
        this.selectedTimeUnit = value;
        this.loadData();
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