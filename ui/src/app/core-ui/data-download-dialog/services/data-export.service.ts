/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { Injectable } from '@angular/core';
import { ExportConfig } from '../model/export-config.model';
import { DatalakeRestService } from '@streampipes/platform-services';
import { DataDownloadDialogModel } from '../model/data-download-dialog.model';

@Injectable({
  providedIn: 'root'
})
export class DataExportService {

  constructor(public datalakeRestService: DatalakeRestService) {
  }

  downloadData(exportConfig: ExportConfig) {

    // const index = !this.dataExplorerDataConfig ?
    //   this.measureName : this.dataExplorerDataConfig.sourceConfigs[this.selectedQueryIndex].measureName;
    // // this.nextStep();
    // const startTime = this.date.startDate.getTime();
    // const endTime = this.date.endDate.getTime();
    // const startDateString = this.getDateString(this.date.startDate);
    // const endDateString = this.getDateString(this.date.endDate);
    switch (exportConfig.dataExportConfig.dataRangeConfiguration) {
      case 'all':
        this.performRequest(this.datalakeRestService.downloadRawData(
          exportConfig.dataExportConfig.measurement,
          exportConfig.formatExportConfig.exportFormat,
          exportConfig.formatExportConfig.exportFormat['delimiter']),
          '',
          '');
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
      .generateQuery(startTime, endTime, this.dataExplorerDataConfig.sourceConfigs[this.selectedQueryIndex]);
  }

  performRequest(request, startDate, endDate) {
    this.downloadHttpRequestSubscription = request.subscribe(event => {
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

  getDateString(date: Date): string {
    return date.toLocaleDateString() + 'T' + date.toLocaleTimeString().replace(':', '.')
      .replace(':', '.');
  }


}
