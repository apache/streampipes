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

import { EventEmitter, Injectable } from '@angular/core';
import { ExportConfig } from '../model/export-config.model';
import { DatalakeQueryParameters, DatalakeRestService, DataViewQueryGeneratorService } from '@streampipes/platform-services';
import { HttpEventType } from '@angular/common/http';
import { DataDownloadDialogModel } from '../model/data-download-dialog.model';
import { DownloadProgress } from '../model/download-progress.model';

@Injectable({
  providedIn: 'root'
})
export class DataExportService {

  public updateDownloadProgress: EventEmitter<DownloadProgress> = new EventEmitter();

  constructor(public dataLakeRestService: DatalakeRestService,
              public dataViewQueryGeneratorService: DataViewQueryGeneratorService) {
  }

  public downloadData(exportConfig: ExportConfig,
               dataDownloadDialogModel: DataDownloadDialogModel) {

    let downloadRequest;

    if (exportConfig.dataExportConfig.dataRangeConfiguration === 'visible') {
      downloadRequest = this.dataLakeRestService.downloadQueriedData(
        exportConfig.dataExportConfig.measurement,
        exportConfig.formatExportConfig.exportFormat,
        exportConfig.formatExportConfig.exportFormat['delimiter'],
        this.generateQueryRequest(exportConfig, dataDownloadDialogModel));
    } else {
      // case for 'all' & 'customInterval'
      downloadRequest = this.dataLakeRestService.downloadRawData(
        exportConfig.dataExportConfig.measurement,
        exportConfig.formatExportConfig.exportFormat,
        exportConfig.formatExportConfig.exportFormat['delimiter']);
    }

    downloadRequest.subscribe(event => {
      let downloadProgress: DownloadProgress;
      // progress
      if (event.type === HttpEventType.DownloadProgress) {
        downloadProgress = {
          downloadedMBs: event.loaded / 1024 / 1014,
          finished: false,
        };
        this.updateDownloadProgress.emit(downloadProgress);
      }

      // finished
      if (event.type === HttpEventType.Response) {
        // this.createFile(event.body, this.downloadFormat, this.measureName, startDate, endDate);
        downloadProgress = {
          downloadedMBs: event.loaded / 1024 / 1014,
          finished: true,
        };
        this.updateDownloadProgress.emit(downloadProgress);
      }
    });


    // const index = !this.dataExplorerDataConfig ?
    //   this.measureName : this.dataExplorerDataConfig.sourceConfigs[this.selectedQueryIndex].measureName;
    // // this.nextStep();
    // const startTime = this.date.startDate.getTime();
    // const endTime = this.date.endDate.getTime();
    // const startDateString = this.getDateString(this.date.startDate);
    // const endDateString = this.getDateString(this.date.endDate);

    // this.performRequest(
    //   this.datalakeRestService.downloadRawData(
    //     exportConfig.dataExportConfig.measurement,
    //     exportConfig.formatExportConfig.exportFormat,
    //     exportConfig.formatExportConfig.exportFormat['delimiter']),
    //   '',
    //   '');
    // this.performRequest(
    //   this.datalakeRestService
    //     .downloadQueriedData(
    //       index,
    //       this.downloadFormat,
    //       this.delimiter,
    //       this.generateQueryRequest(startTime, endTime)
    //     ),
    //   startDateString,
    //   endDateString
    // );
    //   this.performRequest(
    //     this.datalakeRestService.downloadRawData(
    //       exportConfig.dataExportConfig.measurement,
    //       exportConfig.formatExportConfig.exportFormat,
    //       exportConfig.formatExportConfig.exportFormat['delimiter']),
    //     exportConfig.dataExportConfig.dateRange.startDate,
    //     exportConfig.dataExportConfig.dateRange.endDate),
    //     this.getDateString(exportConfig.dataExportConfig.dateRange.startDate),
    //     this.getDateString(exportConfig.dataExportConfig.dateRange.endDate);
    // )
    //   ;
    //   break;
    // case 'visible':
    //   this.performRequest(
    //     this.datalakeRestService
    //       .downloadQueriedData(
    //         index,
    //         this.downloadFormat,
    //         this.delimiter,
    //         this.generateQueryRequest(startTime, endTime)
    //       ),
    //     startDateString,
    //     endDateString
    //   );

  }

  // TODO how to set the selected Query Index
  private generateQueryRequest(
    exportConfig: ExportConfig,
    dataDownloadDialogModel: DataDownloadDialogModel,
    selectedQueryIndex: number = 0): DatalakeQueryParameters {
    return this.dataViewQueryGeneratorService
      .generateQuery(
        exportConfig.dataExportConfig.dateRange.startDate.getTime(),
        exportConfig.dataExportConfig.dateRange.startDate.getTime(),
        dataDownloadDialogModel.dataExplorerDataConfig.sourceConfigs[selectedQueryIndex]);
  }

  // performRequest(request, startDate, endDate) {
  //   const downloadHttpRequestSubscription = request.subscribe(event => {
  //     let downloadProgress: DownloadProgress;
  //     // progress
  //     if (event.type === HttpEventType.DownloadProgress) {
  //       downloadProgress = {
  //         downloadedMBs: event.loaded / 1024 / 1014,
  //         finished: false,
  //       };
  //       // this.downloadedMBs = event.loaded / 1024 / 1014;
  //     }
  //
  //     // finished
  //     if (event.type === HttpEventType.Response) {
  //       // this.createFile(event.body, this.downloadFormat, this.measureName, startDate, endDate);
  //       downloadProgress = {
  //         finished: true,
  //       };
  //
  //       // this.downloadFinish = true;
  //     }
  //   });
  // }


  private createFile(data, format, fileName, startDate, endDate, downloadFormat) {
    const a = document.createElement('a');
    document.body.appendChild(a);
    a.style.display = 'display: none';

    let name = 'sp_' + startDate + '_' + fileName + '.' + downloadFormat;
    name = name.replace('__', '_');

    const url = window.URL.createObjectURL(new Blob([String(data)], {type: 'data:text/' + format + ';charset=utf-8'}));
    a.href = url;
    a.download = name;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  private getDateString(date: Date): string {
    return date.toLocaleDateString() + 'T' + date.toLocaleTimeString().replace(':', '.')
      .replace(':', '.');
  }

  private convertData(data, format, xAxesKey, yAxesKeys) {
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

}
