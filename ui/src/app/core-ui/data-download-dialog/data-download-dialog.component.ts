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

import { HttpEventType } from '@angular/common/http';
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import {
  DataExplorerDataConfig,
  DatalakeQueryParameters,
  DatalakeRestService,
  DataViewQueryGeneratorService,
  DateRange
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';


@Component({
  selector: 'sp-data-download-dialog',
  templateUrl: 'data-download-dialog.component.html',
  styleUrls: ['./data-download-dialog.component.scss']
})
// tslint:disable-next-line:component-class-suffix
export class DataDownloadDialogComponent implements OnInit {

  /**
   * Provide either measureName without additional configuration
   * or dataConfig to allow selection of multiple sources
   */
  @Input() measureName: string;
  @Input() dataConfig: DataExplorerDataConfig;

  @Input() date: DateRange;

  downloadFormat = 'csv';
  delimiter = 'comma';
  selectedData = 'visible';
  downloadFinish = false;
  downloadedMBs: number = undefined;
  selectedQueryIndex = 0;

  @ViewChild('stepper', {static: true}) stepper: MatStepper;

  downloadHttpRequestSubscribtion;

  dateRange: Date [] = []; // [0] start, [1] end


  constructor(public dialogRef: DialogRef<DataDownloadDialogComponent>,
              public datalakeRestService: DatalakeRestService,
              private dataViewQueryService: DataViewQueryGeneratorService) {
  }

  ngOnInit() {
    if (!this.date) {
      const endDate = new Date();
      endDate.setDate(endDate.getDate() - 5);
      this.date = {startDate: new Date(), endDate};
    }
    this.dateRange[0] = this.date.startDate;
    this.dateRange[1] = this.date.endDate;

    if (!this.dataConfig) {
      this.selectedData = 'all';
    }
  }

  downloadData() {
    const index = !this.dataConfig ?
      this.measureName : this.dataConfig.sourceConfigs[this.selectedQueryIndex].measureName;
    this.nextStep();
    const startTime = this.date.startDate.getTime();
    const endTime = this.date.endDate.getTime();
    const startDateString = this.getDateString(this.date.startDate);
    const endDateString = this.getDateString(this.date.endDate);
    switch (this.selectedData) {
      case 'all':
        this.performRequest(this.datalakeRestService.downloadRawData(
          index,
          this.downloadFormat,
          this.delimiter), '', '');
        break;
      case 'customInterval':
        this.performRequest(this.datalakeRestService.downloadRawData(
            index,
            this.downloadFormat,
            this.delimiter,
            startTime,
            endTime), startDateString,
          endDateString);
        break;
      case 'visible':
        this.performRequest(
          this.datalakeRestService
            .downloadQueriedData(
              index,
              this.downloadFormat,
              this.delimiter,
              this.generateQueryRequest(startTime, endTime)
            ),
          startDateString,
          endDateString
        );

    }
  }

  generateQueryRequest(startTime: number,
                       endTime: number): DatalakeQueryParameters {
    return this.dataViewQueryService
      .generateQuery(startTime, endTime, this.dataConfig.sourceConfigs[this.selectedQueryIndex]);
  }

  performRequest(request, startDate, endDate) {
    this.downloadHttpRequestSubscribtion = request.subscribe(event => {
      // progress
      if (event.type === HttpEventType.DownloadProgress) {
        this.downloadedMBs = event.loaded / 1024 / 1014;
      }

      // finished
      if (event.type === HttpEventType.Response) {
        this.createFile(event.body, this.downloadFormat, this.measureName, startDate, endDate);
        this.downloadFinish = true;
      }
    });
  }

  convertData(data, format, xAxesKey, yAxesKeys) {
    const indexXKey = data.headers.findIndex(headerName => headerName === xAxesKey);
    const indicesYKeys = [];
    yAxesKeys.forEach(key => {
      indicesYKeys.push(data.headers.findIndex(headerName => headerName === key));
    });

    if (format === 'json') {
      const resultJson = [];


      data.rows.forEach(row => {
        const tmp = {'time': new Date(row[indexXKey]).getTime()};
        indicesYKeys.forEach(index => {
          if (row[index] !== undefined) {
            tmp[data.headers[index]] = row[index];
          }
        });
        resultJson.push(tmp);
      });

      return JSON.stringify(resultJson);
    } else {
      // CSV
      let resultCsv = '';

      // header
      resultCsv += xAxesKey;
      yAxesKeys.forEach(key => {
        resultCsv += ';';
        resultCsv += key;
      });


      // content
      data.rows.forEach(row => {
        resultCsv += '\n';
        resultCsv += new Date(row[indexXKey]).getTime();
        indicesYKeys.forEach(index => {
          resultCsv += ';';
          if (row[index] !== undefined) {
            resultCsv += row[index];
          }
        });
      });

      return resultCsv;
    }
  }

  createFile(data, format, fileName, startDate, endDate) {
    const a = document.createElement('a');
    document.body.appendChild(a);
    a.style.display = 'display: none';

    let name = 'sp_' + startDate + '_' + fileName + '.' + this.downloadFormat;
    name = name.replace('__', '_');

    const url = window.URL.createObjectURL(new Blob([String(data)], {type: 'data:text/' + format + ';charset=utf-8'}));
    a.href = url;
    a.download = name;
    a.click();
    window.URL.revokeObjectURL(url);
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

  getDateString(date: Date): string {
    return date.toLocaleDateString() + 'T' + date.toLocaleTimeString().replace(':', '.')
      .replace(':', '.');
  }

}
