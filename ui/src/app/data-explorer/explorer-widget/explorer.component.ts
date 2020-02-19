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

import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { timer } from 'rxjs/internal/observable/timer';
import { Observable } from 'rxjs/Observable';
import { map, startWith } from 'rxjs/operators';
import { DataResult } from '../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../core-model/datalake/GroupedDataResult';
import { InfoResult } from '../../core-model/datalake/InfoResult';
import { DatalakeRestService } from '../../core-services/datalake/datalake-rest.service';
import { DataDownloadDialog } from './datadownloadDialog/dataDownload.dialog';

@Component({
    selector: 'sp-explorer',
    templateUrl: './explorer.component.html',
    styleUrls: ['./explorer.css'],
})
export class ExplorerComponent implements OnInit {

    myControl = new FormControl();
    infoResult: InfoResult[];
    filteredIndexInfos: Observable<InfoResult[]>;

    page = 0;
    // selectedIndex: string = '';
    selectedInfoResult: InfoResult = undefined;

    // timeunit selection
    selectedTimeUnit = '1 Hour';

    // aggregation / advanced options
    // group by
    enableAdvanceOptions = false;
    groupbyUnit = 'd';
    groupbyValue = 1;

    // key selections
    dataKeys: string[] = [];

    // grouped Data
    dimensionProperties: string[] = [];
    selectedGroup = undefined;

    // y and x axe
    yAxesKeys: [] = [];
    xAxesKey = 'time';

    data;

    isLoadingData;
    displayIsLoadingData = false;

    // user messages
    noDateFoundinTimeRange;
    noKeySelected;
    noIndexSelection;

    // custom time range
    dateRange: Date []; // [0] start, [1] end

    // Mat Group
    selectedMatGroup = new FormControl(0);

    // auto update data
    optionAutoUpdateData = false;
    autoUpdateData = false;
    autoUpdatePeriod = 10;
    autoUpdateTimer;
    autoUpdateTimerSubcribtion;

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar, public dialog: MatDialog) {
        const dateTmp = new Date();
        this.setDateRange(dateTmp, new Date(dateTmp.getTime() - 60000 * 60 * 1));
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
            this.groupbyUnit = 's';
            this.groupbyValue = 10;
        } else if (this.selectedTimeUnit === '1 Day') {
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

     this.loadData(false);
    }

    loadData(silentLoading?) {
        this.isLoadingData = true;
        this.noDateFoundinTimeRange = false;
        this.noIndexSelection = false;

        if (!silentLoading) {
           this.displayIsLoadingData = true;
        }

        if (this.selectedTimeUnit !== 'Custom') {
            const endDateTmp = new Date();
            let startDateTmp;

            if (this.selectedTimeUnit === '1 Hour') {
                startDateTmp = new Date(endDateTmp.getTime() - 60000 * 60 * 1); // 1 Hour
            } else if (this.selectedTimeUnit === '1 Day') {
                startDateTmp = new Date(endDateTmp.getTime() - 60000 * 60 * 24 * 1); // 1 Day
            } else if (this.selectedTimeUnit === '1 Week') {
                startDateTmp = new Date(endDateTmp.getTime() - 60000 * 60 * 24 * 7); // 7 Days
            } else if (this.selectedTimeUnit === '1 Month') {
                startDateTmp = new Date(endDateTmp.getTime() - 60000 * 60 * 24 * 30); // 30 Days
            } else if (this.selectedTimeUnit === '1 Year') {
                startDateTmp = new Date(endDateTmp.getTime() - 60000 * 60 * 24 * 365); // 365 Days
            }
            this.setDateRange(startDateTmp, endDateTmp);
        }

        if (this.enableAdvanceOptions) {
            let groupbyUnit = this.groupbyUnit;
            let groupbyValue = this.groupbyValue;
            if (this.groupbyUnit === 'month') {
                groupbyUnit = 'w';
                groupbyValue = 4 * groupbyValue;
            } else if (this.groupbyUnit === 'year') {
                groupbyUnit = 'd';
                groupbyValue = 365 * groupbyValue;
            }
            if (this.selectedGroup === undefined) {
                const startTime = new Date().getTime();
                this.restService.getData(this.selectedInfoResult.measureName, this.dateRange[0].getTime(), this.dateRange[1].getTime(),
                    groupbyUnit, groupbyValue).subscribe(
                    res => {
                        this.processReceivedData(res);

                    }
                );
            } else {
                this.restService.getGroupedData(this.selectedInfoResult.measureName, this.dateRange[0].getTime(),
                    this.dateRange[1].getTime(), groupbyUnit, groupbyValue, this.selectedGroup).subscribe(
                        res => {
                            this.processReceivedGroupedData(res);
                        }
                );
            }

        } else {
            if (this.selectedGroup === undefined) {
                this.restService.getDataAutoAggergation(this.selectedInfoResult.measureName, this.dateRange[0].getTime(), this.dateRange[1].getTime()).subscribe(
                    res => {
                        this.processReceivedData(res);
                    }
                );
            } else {
                this.restService.getGroupedDataAutoAggergation(this.selectedInfoResult.measureName, this.dateRange[0].getTime(),
                    this.dateRange[1].getTime(), this.selectedGroup).subscribe(
                    res => {
                        this.processReceivedGroupedData(res);
                    }
                );
            }

        }

    }

    reloadData() {
        if (this.optionAutoUpdateData) {
            if (this.autoUpdateData) {
                this.autoUpdateData = false;
                this.autoUpdateTimerSubcribtion.unsubscribe();
            } else {
                this.autoUpdateData = true;
                this.autoUpdateTimer = timer(this.autoUpdatePeriod * 1000, this.autoUpdatePeriod * 1000);
                this.autoUpdateTimerSubcribtion = this.autoUpdateTimer.subscribe(val => {
                    // Just Load new data if last request finished
                    if (!this.isLoadingData) {
                        this.loadData(true);
                    }
                });
            }
        } else {
            this.loadData(false);
        }
    }

    processReceivedData(res) {
        if (res.total > 0) {
            this.data = res as DataResult;
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
        this.displayIsLoadingData = false;
    }

    processReceivedGroupedData(res) {
        if (res.total > 0) {
            this.data = res as GroupedDataResult;
        } else {
            this.data = undefined;
            this.noDateFoundinTimeRange = true;
            this.noKeySelected = false;
        }
        this.isLoadingData = false;
        this.displayIsLoadingData = false;
    }

    selectIndex(index: string) {
        this.dataKeys = [];
        this.dimensionProperties = [];
        this.selectedGroup = undefined;
        this.selectedInfoResult = this._filter(index)[0];
        this.selectedInfoResult.eventSchema.eventProperties.forEach(property => {

            // Check if property is Primitive (only primitives has a runtimeType
            if (property['runtimeType'] !== undefined) {
                if (property['propertyScope'] !== undefined && property['propertyScope'] === 'DIMENSION_PROPERTY') {
                    this.dimensionProperties.push(property['runtimeName']);
                } else if (this.isNumberProperty(property) &&
                    (property['domainProperties'] === undefined || (property.domainProperty !== 'http://schema.org/DateTime' &&
                        property['domainProperties'][0] != 'http://schema.org/DateTime'))) {

                    this.dataKeys.push(property['runtimeName']);
                }
            } else {
                // list and nested properties
                this.dataKeys.push(property['runtimeName']);
            }
        });
        this.selectKey(this.dataKeys.slice(0, 3));
        this.loadData(false);
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

    selectDimensionProperty(value) {
        if (value !== this.selectedGroup) {
            // remove group property from the "data selection"
            this.dataKeys = this.dataKeys.filter(key => key !== value);
            this.selectKey(this.dataKeys.filter(key => key !== value));

            // add last grouped property
            if (this.selectedGroup !== undefined) {
                this.dataKeys.push(this.selectedGroup);
            }


            this.selectedGroup = value;
            this.loadData(false);
        }
    }

    downloadDataAsFile() {
        const dialogRef = this.dialog.open(DataDownloadDialog, {
            width: '600px',
            data: {data: this.data, xAxesKey: this.xAxesKey, yAxesKeys: this.yAxesKeys, index: this.selectedInfoResult.measureName, date: this.dateRange},
            panelClass: 'custom-dialog-container'

        });
    }


    handleNextPage() {
        let offset;
        if (this.selectedTimeUnit === 'Custom') {
            offset = this.dateRange[1].getTime() - this.dateRange[0].getTime();
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
        this.setDateRange(new Date(this.dateRange[0].getTime() + offset), new Date(this.dateRange[1].getTime() + offset));
        this.loadData(false);
    }

    handlePreviousPage() {
        let offset;
        if (this.selectedTimeUnit === 'Custom') {
            offset = -(this.dateRange[1].getTime() - this.dateRange[0].getTime());
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
        this.setDateRange(new Date(this.dateRange[0].getTime() + offset), new Date(this.dateRange[1].getTime() + offset));
        this.loadData(false);
    }

    handleFirstPage() {
        // TODO
    }

    handleLastPage() {
        // TODO
    }

    setDateRange(start, end) {
        this.dateRange = [];
        this.dateRange[0] = start;
        this.dateRange[1] = end;
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


    isNumberProperty(prop) {
        if (prop.runtimeType === 'http://schema.org/Number' ||
            prop.runtimeType === 'http://www.w3.org/2001/XMLSchema#float' ||
            prop.runtimeType === 'http://www.w3.org/2001/XMLSchema#integer' ||
            prop.runtimeType === 'http://www.w3.org/2001/XMLSchema#double' ||
            prop.runtimeType === 'http://www.w3.org/2001/XMLSchema#decimal') {

            return true;
        } else {
            return false;
        }
    }

    zoomEventHandler(timeRange) {
      this.selectedTimeUnit = 'Custom';
      if (timeRange[0] !== undefined) {
        this.setDateRange(new Date(timeRange[0]), new Date(timeRange[1]));
      }
      this.loadData(true);
    }
}
