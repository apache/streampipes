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

import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatStepper} from '@angular/material';
import {HttpEventType} from '@angular/common/http';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';

@Component({
    selector: 'sp-dataDownload-dialog',
    templateUrl: 'dataDownload.dialog.html',
    styleUrls: ['./dataDownload.dialog.css']
})
export class DataDownloadDialog {


    downloadFormat: string = 'csv';
    selectedData: string = 'visible';
    downloadFinish: boolean = false;
    downloadedMBs: number = undefined;

    @ViewChild('stepper') stepper: MatStepper;

    downloadHttpRequestSubscribtion;

    dateRange: Date [] = []; // [0] start, [1] end


    constructor(public dialogRef: MatDialogRef<DataDownloadDialog>,
                @Inject(MAT_DIALOG_DATA) public data, private restService: DatalakeRestService,) {
        this.dateRange[0] = new Date();
        this.dateRange[1] = new Date(this.dateRange[0].getTime() + 60000 * 60 * 24);
    }

    downloadData() {
        this.nextStep();
        switch (this.selectedData) {
            case "visible":

                if (this.data.yAxesKeys === undefined) {
                    this.createFile('', this.downloadFormat, this.data.index, this.getDateString(this.data.date[0]),
                      this.getDateString((this.data.date[1])));

                } else if (this.data.data["headers"] !== undefined) {
                 //Single Data
                    let result = this.convertData(this.data.data, this.downloadFormat, this.data.xAxesKey, this.data.yAxesKeys);
                    this.createFile(result, this.data.downloadFormat, this.data.index, this.getDateString(this.data.date[0]),
                      this.getDateString(this.data.date[1]));
                } else {
                    //group data
                    Object.keys(this.data.data.dataResults).forEach( groupName => {
                        let dataResult = this.data.data.dataResults[groupName];
                        let result = this.convertData(dataResult, this.downloadFormat, this.data.xAxesKey, this.data.yAxesKeys);
                        let fileName = this.data.index + ' ' + groupName;
                        this.createFile(result, this.data.downloadFormat, fileName, this.getDateString(this.data.date[0]),
                          this.getDateString(this.data.date[1]));
                    });

                }
                this.downloadFinish = true;
                break;
            case "all":
                this.performRequest(this.restService.downloadRowData(this.data.index, this.downloadFormat), '', '');
                break;
            case "customInterval":
                this.performRequest(this.restService.downloadRowDataTimeInterval(this.data.index, this.downloadFormat,
                    this.dateRange[0].getTime(), this.dateRange[1].getTime()), this.getDateString(this.dateRange[0]),
                  this.getDateString(this.dateRange[1]));

        }
    }

    performRequest(request, startDate, endDate) {
        this.downloadHttpRequestSubscribtion = request.subscribe(event => {
            // progress
            if (event.type === HttpEventType.DownloadProgress) {
                this.downloadedMBs = event.loaded / 1024 / 1014
            }

            // finished
            if (event.type === HttpEventType.Response) {
                this.createFile(event.body, this.downloadFormat, this.data.index, startDate, endDate);
                this.downloadFinish = true
            }
        });
    }

    convertData(data, format, xAxesKey, yAxesKeys) {
        let indexXKey = data.headers.findIndex(headerName => headerName === xAxesKey);
        let indicesYKeys = [];
        yAxesKeys.forEach(key => {
            indicesYKeys.push(data.headers.findIndex(headerName => headerName === key))
        });

        if (format === "json") {
            let resultJson = [];


            data.rows.forEach(row => {
                let tmp = {"time": new Date(row[indexXKey]).getTime()};
                indicesYKeys.forEach(index => {
                    if (row[index] !== undefined) {
                        tmp[data.headers[index]] = row[index]
                    }
                });
                resultJson.push(tmp)
            });

            return JSON.stringify(resultJson);
        } else {
            //CSV
            let resultCsv: string = '';

            //header
            resultCsv += xAxesKey;
            yAxesKeys.forEach(key => {
                resultCsv += ';';
                resultCsv += key;
            });


            //content
            data.rows.forEach(row => {
                resultCsv += '\n';
                resultCsv += new Date(row[indexXKey]).getTime();
                indicesYKeys.forEach(index => {
                    resultCsv += ';';
                    if (row[index] !== undefined) {
                        resultCsv += row[index]
                    }
                })
            });

            return resultCsv;
        }
    }

    createFile(data, format, fileName, startDate, endDate) {
        var a = document.createElement("a");
        document.body.appendChild(a);
        a.style.display = "display: none";

        //let name = 'sp_' + startDate + '_' + endDate + '_' + fileName + '.' + this.downloadFormat;
        let name = 'sp_' + startDate + '_' + fileName + '.' + this.downloadFormat;
        name = name.replace('__','_');

        var url = window.URL.createObjectURL(new Blob([String(data)], { type: 'data:text/' + format + ';charset=utf-8' }));
        a.href = url;
        a.download = name;
        a.click();
        window.URL.revokeObjectURL(url)
    }

    cancelDownload() {
        try {
            this.downloadHttpRequestSubscribtion.unsubscribe();
        } finally {
            this.exitDialog();
        }
    }


    exitDialog(): void {
        this.dialogRef.close();
    }

    nextStep() {
        this.stepper.next();
    }

    previousStep() {
        this.stepper.previous();
    }

    getDateString(date: Date) {
        return date.toLocaleDateString() + 'T' + date.toLocaleTimeString().replace(':','.')
                                                                          .replace(':','.');
    }

}