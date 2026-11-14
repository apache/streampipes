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

import { EventEmitter, Injectable, OnDestroy, inject } from '@angular/core';
import { ExportConfig } from '../model/export-config.model';
import {
    DatalakeQueryParameters,
    DatalakeRestService,
    DataViewQueryGeneratorService,
} from '@streampipes/platform-services';
import {
    HttpErrorResponse,
    HttpEvent,
    HttpEventType,
} from '@angular/common/http';
import { DataDownloadDialogModel } from '../model/data-download-dialog.model';
import {
    DownloadProgress,
    FailedDownload,
} from '../model/download-progress.model';
import { FileNameService } from './file-name.service';
import {
    catchError,
    concatMap,
    EMPTY,
    from,
    Observable,
    Subscription,
    tap,
} from 'rxjs';

/**
 * Holds the state of one download run. Provide it at the dialog level so that
 * each dialog owns its run and closing the dialog cancels it.
 */
@Injectable()
export class DataExportService implements OnDestroy {
    dataLakeRestService = inject(DatalakeRestService);
    dataViewQueryGeneratorService = inject(DataViewQueryGeneratorService);
    fileNameService = inject(FileNameService);

    public updateDownloadProgress: EventEmitter<DownloadProgress> =
        new EventEmitter();

    private activeDownload?: Subscription;
    private lastProgress?: DownloadProgress;

    /**
     * Downloads each measurement as a separate file, one after another,
     * applying the same export configuration to all of them.
     */
    public downloadMeasurements(
        exportConfig: ExportConfig,
        dataDownloadDialogModel: DataDownloadDialogModel,
        measurements: string[],
    ) {
        this.cancelDownload();

        const totalFiles = measurements.length;
        const failedDownloads: FailedDownload[] = [];
        let completedFiles = 0;
        let downloadedMBs = 0;

        this.activeDownload = from(measurements)
            .pipe(
                concatMap(measurement => {
                    const measurementExportConfig = this.forMeasurement(
                        exportConfig,
                        measurement,
                    );
                    downloadedMBs = 0;
                    this.emitProgress({
                        downloadedMBs,
                        finished: false,
                        totalFiles,
                        completedFiles,
                        currentMeasurement: measurement,
                    });
                    return this.makeDownloadRequest(
                        measurementExportConfig,
                        dataDownloadDialogModel,
                    ).pipe(
                        tap(event => {
                            if (
                                event.type === HttpEventType.DownloadProgress ||
                                event.type === HttpEventType.Response
                            ) {
                                downloadedMBs = this.toMBs(
                                    event.type === HttpEventType.Response
                                        ? (event.body?.size ?? 0)
                                        : event.loaded,
                                );
                            }
                            if (event.type === HttpEventType.DownloadProgress) {
                                this.emitProgress({
                                    downloadedMBs,
                                    finished: false,
                                    totalFiles,
                                    completedFiles,
                                    currentMeasurement: measurement,
                                });
                            }
                            if (event.type === HttpEventType.Response) {
                                this.createFile(
                                    event.body,
                                    measurementExportConfig,
                                );
                                completedFiles++;
                            }
                        }),
                        catchError(error => {
                            failedDownloads.push({
                                measurement,
                                reason: this.describeError(error),
                            });
                            return EMPTY;
                        }),
                    );
                }),
            )
            .subscribe({
                complete: () => {
                    this.activeDownload = undefined;
                    this.emitProgress({
                        downloadedMBs,
                        finished: true,
                        totalFiles,
                        completedFiles,
                        failedDownloads,
                    });
                },
            });
    }

    public cancelDownload(): void {
        if (!this.activeDownload) {
            return;
        }
        this.activeDownload.unsubscribe();
        this.activeDownload = undefined;
        this.emitProgress({
            ...this.lastProgress,
            finished: false,
            cancelled: true,
        });
    }

    ngOnDestroy(): void {
        this.activeDownload?.unsubscribe();
        this.activeDownload = undefined;
    }

    private describeError(error: unknown): string {
        if (error instanceof HttpErrorResponse) {
            return error.status
                ? `HTTP ${error.status}${error.statusText ? ' ' + error.statusText : ''}`
                : error.message;
        }
        return error instanceof Error ? error.message : String(error);
    }

    private emitProgress(progress: DownloadProgress): void {
        this.lastProgress = progress;
        this.updateDownloadProgress.emit(progress);
    }

    private forMeasurement(
        exportConfig: ExportConfig,
        measurement: string,
    ): ExportConfig {
        return {
            ...exportConfig,
            dataExportConfig: {
                ...exportConfig.dataExportConfig,
                measurement,
            },
        };
    }

    private makeDownloadRequest(
        exportConfig: ExportConfig,
        dataDownloadDialogModel: DataDownloadDialogModel,
    ): Observable<HttpEvent<Blob>> {
        if (
            exportConfig.dataExportConfig.dataRangeConfiguration === 'visible'
        ) {
            return this.dataLakeRestService.downloadQueriedData(
                exportConfig.dataExportConfig.measurement,
                exportConfig.formatExportConfig,
                exportConfig.dataExportConfig.missingValueBehaviour,
                this.generateQueryRequest(
                    exportConfig,
                    dataDownloadDialogModel,
                ),
            ) as Observable<HttpEvent<Blob>>;
        }

        // case for 'all' and 'customInterval'
        let startTime: number | undefined;
        let endTime: number | undefined;
        if (
            exportConfig.dataExportConfig.dataRangeConfiguration ===
            'customInterval'
        ) {
            startTime =
                exportConfig.dataExportConfig.dateRange.startDate.getTime();
            endTime = exportConfig.dataExportConfig.dateRange.endDate.getTime();
        }
        return this.dataLakeRestService.downloadRawData(
            exportConfig.dataExportConfig.measurement,
            exportConfig.formatExportConfig,
            exportConfig.dataExportConfig.missingValueBehaviour,
            startTime,
            endTime,
        ) as Observable<HttpEvent<Blob>>;
    }

    private toMBs(bytes: number): number {
        return bytes / 1024 / 1024;
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
        a.style.display = 'none';
        document.body.appendChild(a);

        const name = this.fileNameService.generateName(
            exportConfig,
            new Date(),
        );
        const blob = new Blob([data]);

        const url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = name;
        a.click();
        a.remove();
        // Revoking synchronously can abort the download in some browsers.
        setTimeout(() => window.URL.revokeObjectURL(url), 60000);
    }
}
