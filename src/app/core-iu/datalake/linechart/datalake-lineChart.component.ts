import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';
import {MatSnackBar} from '@angular/material';
import {FormControl} from '@angular/forms';

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
    yAxesKeys: [] = undefined;
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

    //aggregation / advanced options
    //group by
    enableAdvanceOptions = false;
    groupbyUnit = 'd';
    groupbyValue = 1;

    //custom time range
    customStartDate = new Date();
    customEndDate = new Date(this.customStartDate.getTime() + 60000 * 60 * 24);

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

    }

    paging(page) {
        this.isLoadingData = true;
        this.restService.getDataPage(this._index, this.itemsPerPage, page).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.currentPage = res.page;
                    this.maxPage = res.pageSum;
                    this.data = res.events as [];
                } else {
                    this.openSnackBar('No data found on page ' + page);
                }
                this.isLoadingData = false;
            });
    }

    loadData() {
        if (this.selectedTimeUnit === 'All') {
            this.loadAllData();
            this.enablePaging = true;
            this.enableItemsPerPage = true;
        } else if(this.selectedTimeUnit == 'Custom') {
            this.loadCustomData();
            this.enablePaging = false;
            this.enableItemsPerPage = false;
        } else {
            this.enablePaging = false;
            this.enableItemsPerPage = false;
            this.loadLastData();
        }
    }

    loadAllData() {
        this.isLoadingData = true;
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
                this.isLoadingData = false;
            }
        );
    }

    loadLastData() {
        let timeunit = '';
        let timevalue = 0;
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
            timevalue = 12*4;
        }

        this.isLoadingData = true;
        this.restService.getLastData(this._index, timeunit, timevalue, this.groupbyUnit, this.groupbyValue).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.data = res.events as [];
                    this.setDataKeys(res.events[0]);
                    this.currentPage = undefined;
                } else {
                    this.data = undefined;
                }
                this.isLoadingData = false;
            }
        );
    }

    loadCustomData() {
        let aggregationunit = 'm';
        let aggreagtionvalue = 1;
        this.isLoadingData = true;
        this.restService.getData(this._index, this.customStartDate.getTime(), this.customEndDate.getTime(), aggregationunit, aggreagtionvalue).subscribe(
            res => {
                if(res.events.length > 0) {
                    this.data = res.events as [];
                    this.setDataKeys(res.events[0]);
                    this.currentPage = undefined;
                } else {
                    this.data = undefined;
                }
                this.isLoadingData = false;
            }
        );
    }

    selectKey(value) {
        this.yAxesKeys = value;
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

        if (this.selectedTimeUnit === '24 Hours') {
            this.groupbyUnit = 'm';
            this.groupbyValue = 1;
        } else if (this.selectedTimeUnit === '1 Week') {
            this.groupbyUnit = 'm';
            this.groupbyValue = 30;
        } else if (this.selectedTimeUnit === '1 Month') {
            this.groupbyUnit = 'h';
            this.groupbyValue = 4;
        } else if (this.selectedTimeUnit === '1 Year') {
            this.groupbyUnit = 'h';
            this.groupbyValue = 12;
        }

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

    handleFirstPage() {
        this.paging(0);
    }

    handleLastPage() {
        this.loadAllData()
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }

}