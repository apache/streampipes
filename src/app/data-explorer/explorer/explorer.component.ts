import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {DatalakeRestService} from '../../core-services/datalake/datalake-rest.service';
import {InfoResult} from '../../core-model/datalake/InfoResult';
import {Observable} from 'rxjs/Observable';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {MatSnackBar} from '@angular/material';

@Component({
    selector: 'sp-explorer',
    templateUrl: './explorer.component.html',
    styleUrls: ['./explorer.css'],
})
export class ExplorerComponent implements OnInit {

    myControl = new FormControl();
    infoResult: InfoResult[];
    filteredIndexInfos: Observable<InfoResult[]>;

    page: number = 0;
    //selectedIndex: string = '';
    selectedInfoResult: InfoResult = undefined;

    //timeunit selection
    selectedTimeUnit = 'All';

    //aggregation / advanced options
    //group by
    enableAdvanceOptions = false;
    groupbyUnit = 'd';
    groupbyValue = 1;

    //key selections
    dataKeys: string[] = [];

    //y and x axe
    yAxesKeys: [] = undefined;
    xAxesKey = 'time';

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;

    data;

    isLoadingData;

    //TODO REMOVE
    itemsPerPage = 50;

    //custom time range
    customStartDate = new Date();
    customEndDate = new Date(this.customStartDate.getTime() + 60000 * 60 * 24);


    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

    }

    ngOnInit(): void {
        this.restService.getAllInfos().subscribe(res => {
                this.infoResult = res;
                this.filteredIndexInfos = this.myControl.valueChanges
                    .pipe(
                        startWith(''),
                        map(value => this._filter(value))
                    );
            }
        );
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

    loadData() {
        if (this.selectedTimeUnit === 'All') {
            this.loadAllData();
        } else if(this.selectedTimeUnit == 'Custom') {
            this.loadCustomData();
        } else {
            this.loadLastData();
        }
    }

    loadAllData() {
        this.isLoadingData = true;
        this.restService.getDataPageWithoutPage(this.selectedInfoResult.measureName,this.itemsPerPage).subscribe(
            res => {
                if(res.events.length > 0) {
                //    this.currentPage = res.page;
                //    this.maxPage = res.pageSum;
                    this.data = res.events as [];
               //     this.setDataKeys(res.events)
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
            timeunit = 'd';
            timevalue = 365;
        }

        this.isLoadingData = true;
        if (this.enableAdvanceOptions) {
            let groupbyUnit = this.groupbyUnit;
            let groupbyValue = this.groupbyValue;
            if (this.groupbyUnit === 'month') {
                groupbyUnit = 'd';
                groupbyValue = 30 * this.groupbyValue;
            } else if(this.groupbyUnit === 'year') {
                groupbyUnit = 'd';
                groupbyValue = 365 * this.groupbyValue;
            }

            this.restService.getLastData(this.selectedInfoResult.measureName, timeunit, timevalue, groupbyUnit, groupbyValue).subscribe(
                res => this.processRevicedData(res)
            );
        } else {
            this.restService.getLastDataAutoAggregation(this.selectedInfoResult.measureName, timeunit, timevalue).subscribe(
                res => this.processRevicedData(res)
            );
        }


    }

    processRevicedData(res) {
        if(res.events.length > 0) {
            this.data = res.events as [];
        //    this.setDataKeys(res.events);
       //     this.currentPage = undefined;
        } else {
            this.data = undefined;
        }
        this.isLoadingData = false;
    }

    loadCustomData() {
        this.isLoadingData = true;
        let groupbyUnit = this.groupbyUnit;
        let groupbyValue = this.groupbyValue;
        if (this.groupbyUnit === 'month') {
            groupbyUnit = 'w';
            this.groupbyValue = 4 * this.groupbyValue;
        } else if(this.groupbyUnit === 'year') {
            groupbyUnit = 'd';
            this.groupbyValue = 365 * this.groupbyValue;
        }
        if (this.enableAdvanceOptions) {
            this.restService.getData(this.selectedInfoResult.measureName, this.customStartDate.getTime(), this.customEndDate.getTime(), groupbyUnit, groupbyValue).subscribe(
                res => this.processRevicedData(res)
            );
        } else {
            this.restService.getDataAutoAggergation(this.selectedInfoResult.measureName, this.customStartDate.getTime(), this.customEndDate.getTime()).subscribe(
                res => this.processRevicedData(res)
            );
        }

    }

    selectIndex(index: string) {
        this.dataKeys = [];
        this.selectedInfoResult = this._filter(index)[0];
        this.selectedInfoResult.eventSchema.eventProperties.forEach(property => {
            if (property['domainProperties'] === undefined) {
                this.dataKeys.push(property['runtimeName']);
            } else if (property.domainProperty !== 'http://schema.org/DateTime'&& property['domainProperties'][0] != "http://schema.org/DateTime") {
                this.dataKeys.push(property['runtimeName']);
            }
            });
        this.loadData();
    }

    selectKey(value) {
        this.yAxesKeys = value;
    }

    _filter(value: string): InfoResult[] {
        const filterValue = value.toLowerCase();

        return this.infoResult.filter(option => option.measureName.toLowerCase().includes(filterValue));
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }
}