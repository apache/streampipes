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
import {
    DatalakeQueryParameters,
    DatalakeRestService,
    DataViewQueryGeneratorService,
} from '@streampipes/platform-services';
import { HttpEventType } from '@angular/common/http';
import { DataDownloadDialogModel } from '../model/data-download-dialog.model';
import { DownloadProgress } from '../model/download-progress.model';
import { FileNameService } from './file-name.service';

@Injectable({
    providedIn: 'root',
})
export class DataExportService {
    public updateDownloadProgress: EventEmitter<DownloadProgress> =
        new EventEmitter();

    constructor(
        public dataLakeRestService: DatalakeRestService,
        public dataViewQueryGeneratorService: DataViewQueryGeneratorService,
        public fileNameService: FileNameService,
    ) {}

    public downloadData(
        exportConfig: ExportConfig,
        dataDownloadDialogModel: DataDownloadDialogModel,
    ) {
        let downloadRequest;

        if (
            exportConfig.dataExportConfig.dataRangeConfiguration === 'visible'
        ) {
            downloadRequest = this.dataLakeRestService.downloadQueriedData(
                exportConfig.dataExportConfig.measurement,
                exportConfig.formatExportConfig.exportFormat,
                exportConfig.formatExportConfig['delimiter'],
                exportConfig.dataExportConfig.missingValueBehaviour,
                this.generateQueryRequest(
                    exportConfig,
                    dataDownloadDialogModel,
                ),
            );
        } else {
            // case for 'all' and 'customInverval'
            let startTime,
                endTime = undefined;
            if (
                exportConfig.dataExportConfig.dataRangeConfiguration ===
                'customInterval'
            ) {
                startTime =
                    exportConfig.dataExportConfig.dateRange.startDate.getTime();
                endTime =
                    exportConfig.dataExportConfig.dateRange.endDate.getTime();
            }
            downloadRequest = this.dataLakeRestService.downloadRawData(
                exportConfig.dataExportConfig.measurement,
                exportConfig.formatExportConfig.exportFormat,
                exportConfig.formatExportConfig['delimiter'],
                exportConfig.dataExportConfig.missingValueBehaviour,
                startTime,
                endTime,
            );
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
                this.createFile(event.body, exportConfig);
                downloadProgress = {
                    downloadedMBs: event.loaded / 1024 / 1014,
                    finished: true,
                };
                this.updateDownloadProgress.emit(downloadProgress);
            }
        });
    }

    private generateQueryRequest(
        exportConfig: ExportConfig,
        dataDownloadDialogModel: DataDownloadDialogModel,
        selectedQueryIndex: number = 0,
    ): DatalakeQueryParameters {
        return this.dataViewQueryGeneratorService.generateQuery(
            exportConfig.dataExportConfig.dateRange.startDate.getTime(),
            exportConfig.dataExportConfig.dateRange.endDate.getTime(),
            dataDownloadDialogModel.dataExplorerDataConfig.sourceConfigs[
                selectedQueryIndex
            ],
            false,
        );
    }

    /**
     * The code in this method can be updated. Currently, it uses a native approach to download a file.
     * At other places we use the fileSaver library (https://www.npmjs.com/package/file-saver) to download files.
     * However, this library only supports files up to 2GB.
     * There are other alternatives like StreamSaver.js (https://github.com/jimmywarting/StreamSaver.js).
     * But then we probably should replace the fileSaver library with StreamSaver to only have one dependency.
     * In the documentation it is also stated that an alternative approach is under development.
     * It is probably also worth in looking into this one.
     */
    private createFile(data, exportConfig: ExportConfig) {
        const a = document.createElement('a');
        document.body.appendChild(a);
        a.style.display = 'display: none';

        const name = this.fileNameService.generateName(
            exportConfig,
            new Date(),
        );

        const url = window.URL.createObjectURL(
            new Blob([String(data)], {
                type: `data:text/${exportConfig.formatExportConfig.exportFormat};charset=utf-8`,
            }),
        );
        a.href = url;
        a.download = name;
        a.click();
        window.URL.revokeObjectURL(url);
    }
}
