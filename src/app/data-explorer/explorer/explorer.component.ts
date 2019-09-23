import {Component, OnInit} from '@angular/core';
import {DatalakeRestService} from '../../core-services/datalake/datalake-rest.service';
import {InfoResult} from '../../core-model/datalake/InfoResult';
import {Observable} from 'rxjs/Observable';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {MatDialog, MatSnackBar} from '@angular/material';
import {DataDownloadDialog} from './datadownloadDialog/dataDownload.dialog';

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
    selectedTimeUnit = '1 Day';

    //aggregation / advanced options
    //group by
    enableAdvanceOptions = false;
    groupbyUnit = 'd';
    groupbyValue = 1;

    //key selections
    dataKeys: string[] = [];

    //y and x axe
    yAxesKeys: [] = [];
    xAxesKey = 'time';

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;

    data;

    isLoadingData;

    //user messages
    noDateFoundinTimeRange;
    noKeySelected;
    noIndexSelection;

    //custom time range
    customEndDate: Date;
    customStartDate: Date;

    //Mat Group
    selectedMatGroup = new FormControl(0);


    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar, public dialog: MatDialog) {
        this.customEndDate = new Date();
        this.customEndDate.setHours(0,0,0,0);
        this.customStartDate = new Date(this.customEndDate.getTime() - 60000 * 60 * 24);
    }

    ngOnInit(): void {
        this.restService.getAllInfos().subscribe(res => {
                this.infoResult = res;
                this.filteredIndexInfos = this.myControl.valueChanges
                    .pipe(
                        startWith(''),
                        map(value => this._filter(value))
                    );
                this.noIndexSelection = true;
            }
        );
    }

    selectTimeUnit(value) {
        this.selectedTimeUnit = value;

        if (this.selectedTimeUnit === '1 Day') {
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
        this.isLoadingData = true;
        this.noDateFoundinTimeRange = false;
        this.noIndexSelection = false;

        if (this.selectedTimeUnit !== 'Custom') {
            this.customEndDate = new Date();
            this.customEndDate.setHours(0,0,0,0);

            if (this.selectedTimeUnit === '1 Day') {
                this.customStartDate = new Date(this.customEndDate.getTime() - 60000 * 60 * 24 * 1);
            } else if (this.selectedTimeUnit === '1 Week') {
                this.customStartDate = new Date(this.customEndDate.getTime() - 60000 * 60 * 24 * 7);
            } else if (this.selectedTimeUnit === '1 Month') {
                this.customStartDate = new Date(this.customEndDate.getTime() - 60000 * 60 * 24 * 30);
            } else if (this.selectedTimeUnit === '1 Year') {
                this.customStartDate = new Date(this.customEndDate.getTime() - 60000 * 60 * 24 * 365);
            }
        }

        if (this.enableAdvanceOptions) {
            let groupbyUnit = this.groupbyUnit;
            let groupbyValue = this.groupbyValue;
            if (this.groupbyUnit === 'month') {
                groupbyUnit = 'w';
                groupbyValue = 4 * groupbyValue;
            } else if(this.groupbyUnit === 'year') {
                groupbyUnit = 'd';
                groupbyValue = 365 * groupbyValue;
            }
            this.restService.getData(this.selectedInfoResult.measureName, this.customStartDate.getTime(), this.customEndDate.getTime(), groupbyUnit, groupbyValue).subscribe(
                res => this.processReceivedData(res)
            );
        } else {
            this.restService.getDataAutoAggergation(this.selectedInfoResult.measureName, this.customStartDate.getTime(), this.customEndDate.getTime()).subscribe(
                res => this.processReceivedData(res)
            );
        }

    }

    processReceivedData(res) {
        if(res.events.length > 0) {
            this.data = res.events as [];
            this.noDateFoundinTimeRange = false;
            if (this.yAxesKeys.length === 0) {
                this.noKeySelected = true;
            }
        } else {
            this.data = undefined;
            this.noDateFoundinTimeRange = true;
            this.noKeySelected = false;
        }
        this.isLoadingData = false;
    }

    selectIndex(index: string) {
        this.dataKeys = [];
        this.selectedInfoResult = this._filter(index)[0];
        this.selectedInfoResult.eventSchema.eventProperties.forEach(property => {
            if (property['domainProperties'] === undefined) {
                this.dataKeys.push(property['runtimeName']);
            } else if (property.domainProperty !== 'http://schema.org/DateTime'&& property['domainProperties'][0] != 'http://schema.org/DateTime') {
                this.dataKeys.push(property['runtimeName']);
            }
        });
        this.loadData();
    }

    selectKey(value) {
        if (this.data === undefined) {
            this.noDateFoundinTimeRange = true;
        } else {
            this.noDateFoundinTimeRange = false;
        }
        if (value.length === 0 && !this.noDateFoundinTimeRange) {
            this.noKeySelected = true;
        } else {
            this.noKeySelected = false;
        }
        this.yAxesKeys = value;

    }

    downloadDataAsFile() {
        const dialogRef = this.dialog.open(DataDownloadDialog, {
            width: '600px',
            data: {data: this.data, xAxesKey: this.xAxesKey, yAxesKeys: this.yAxesKeys, index: this.selectedInfoResult.measureName},
            panelClass: 'custom-dialog-container'

        });
    }


    handleNextPage() {
        let offset;
        if (this.selectedTimeUnit === 'Custom') {
            offset = this.customEndDate.getTime() - this.customStartDate.getTime();
        } else {
            if (this.selectedTimeUnit === '1 Day') {
                offset =  60000 * 60 * 24 * 1;
            } else if (this.selectedTimeUnit === '1 Week') {
                offset =  60000 * 60 * 24 * 7;
            } else if (this.selectedTimeUnit === '1 Month') {
                offset =  60000 * 60 * 24 * 30;
            } else if (this.selectedTimeUnit === '1 Year') {
                offset =  60000 * 60 * 24 * 365;
            }
            this.selectedTimeUnit = 'Custom';
        }
        this.customStartDate = new Date(this.customStartDate.getTime() + offset);
        this.customEndDate = new Date(this.customEndDate.getTime() + offset);
        this.loadData();
    }

    handlePreviousPage() {
        let offset;
        if (this.selectedTimeUnit === 'Custom') {
            offset = -(this.customEndDate.getTime() - this.customStartDate.getTime());
        } else {
            if (this.selectedTimeUnit === '1 Day') {
                offset =  -60000 * 60 * 24 * 1;
            } else if (this.selectedTimeUnit === '1 Week') {
                offset =  -60000 * 60 * 24 * 7;
            } else if (this.selectedTimeUnit === '1 Month') {
                offset =  -60000 * 60 * 24 * 30;
            } else if (this.selectedTimeUnit === '1 Year') {
                offset =  -60000 * 60 * 24 * 365;
            }
            this.selectedTimeUnit = 'Custom';
        }
        this.customStartDate = new Date(this.customStartDate.getTime() + offset);
        this.customEndDate = new Date(this.customEndDate.getTime() + offset);
        this.loadData();
    }

    handleFirstPage() {
        //TODO
    }

    handleLastPage() {
        //TODO
    }

    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }

    _filter(value: string): InfoResult[] {
        const filterValue = value.toLowerCase();

        return this.infoResult.filter(option => option.measureName.toLowerCase().includes(filterValue));
    }
}