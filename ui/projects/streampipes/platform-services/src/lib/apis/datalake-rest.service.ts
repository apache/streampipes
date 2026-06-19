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

import { inject, Injectable } from '@angular/core';
import {
    HttpClient,
    HttpContext,
    HttpParams,
    HttpRequest,
    HttpHeaders,
} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { DataLakeMeasure, SpQueryResult } from '../model/gen/streampipes-model';
import { map } from 'rxjs/operators';
import { DatalakeQueryParameters } from '../model/datalake/DatalakeQueryParameters';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';
import {
    DatasetSummaryDto,
    ResourceSummaryDto,
} from '../model/resource/resource-summary.model';
import {
    CsvImportPreviewRequest,
    CsvImportPreviewResult,
    CsvImportRequest,
    CsvImportResult,
    CsvImportSchemaValidationRequest,
    CsvImportSchemaValidationResult,
} from '../model/datalake/csv-import.model';

@Injectable({
    providedIn: 'root',
})
export class DatalakeRestService {
    private http = inject(HttpClient);

    private get baseUrl() {
        return '/streampipes-backend';
    }

    public get dataLakeUrl() {
        return this.baseUrl + '/api/v4' + '/datalake';
    }

    public get dataLakeMeasureUrl() {
        return this.baseUrl + '/api/v4/datalake/measure';
    }

    public get dataLakeImportUrl() {
        return this.baseUrl + '/api/v4/datalake/import';
    }

    getMeasurementEntryCounts(
        measurementNames: string[],
        daysBack = -1,
    ): Observable<Record<string, number>> {
        return this.http
            .get(`${this.dataLakeMeasureUrl}/count`, {
                params: {
                    measurementNames,
                    daysBack,
                },
            })
            .pipe(map(r => r as Record<string, number>));
    }

    getAllMeasurementSeries(): Observable<DataLakeMeasure[]> {
        const url = this.dataLakeUrl + '/measurements';
        return this.http.get(url).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    DataLakeMeasure.fromData(p),
                );
            }),
        );
    }

    getMeasurementSummary(): Observable<ResourceSummaryDto<DatasetSummaryDto>> {
        return this.http.get<ResourceSummaryDto<DatasetSummaryDto>>(
            `${this.dataLakeMeasureUrl}/summary`,
        );
    }

    getMeasurement(id: string): Observable<DataLakeMeasure> {
        return this.http
            .get(`${this.dataLakeMeasureUrl}/${id}`)
            .pipe(map(res => res as DataLakeMeasure));
    }

    getMeasurementByName(name: string): Observable<DataLakeMeasure> {
        return this.http
            .get(
                `${this.dataLakeMeasureUrl}/byName/${encodeURIComponent(name)}`,
            )
            .pipe(map(res => res as DataLakeMeasure));
    }

    performMultiQuery(
        queryParams: DatalakeQueryParameters[],
    ): Observable<SpQueryResult[]> {
        return this.http
            .post(`${this.dataLakeUrl}/query`, queryParams, {
                headers: { ignoreLoadingBar: '' },
            })
            .pipe(map(response => response as SpQueryResult[]));
    }

    getData(
        index: string,
        queryParams: DatalakeQueryParameters,
        ignoreLoadingBar = false,
    ): Observable<SpQueryResult> {
        const columns = queryParams.columns;
        const context = ignoreLoadingBar
            ? new HttpContext().set(NGX_LOADING_BAR_IGNORED, true)
            : undefined;
        if (columns === '') {
            const emptyQueryResult = new SpQueryResult();
            emptyQueryResult.total = 0;
            return of(emptyQueryResult);
        } else {
            const url =
                this.dataLakeUrl + '/measurements/' + encodeURIComponent(index);
            return this.http.get<SpQueryResult>(url, {
                params: queryParams as unknown as HttpParams,
                context,
            });
        }
    }

    getTagValues(
        index: string,
        fieldNames: string[],
    ): Observable<Map<string, string[]>> {
        if (fieldNames.length === 0) {
            return of(new Map<string, string[]>());
        } else {
            return this.http
                .get(
                    this.dataLakeUrl +
                        '/measurements/' +
                        encodeURIComponent(index) +
                        '/tags?fields=' +
                        fieldNames.toString(),
                )
                .pipe(map(r => r as Map<string, string[]>));
        }
    }

    downloadRawData(
        index: string,
        formatConfig: Record<string, any>,
        missingValueBehaviour: string,
        startTime?: number,
        endTime?: number,
    ) {
        const queryParams =
            startTime && endTime
                ? { ...formatConfig, startDate: startTime, endDate: endTime }
                : {
                      ...formatConfig,
                      missingValueBehaviour,
                  };

        return this.buildDownloadRequest(index, queryParams);
    }

    downloadQueriedData(
        index: string,
        formatConfig: Record<string, any>,
        missingValueBehaviour: string,
        queryParams: DatalakeQueryParameters,
    ) {
        const qp = { ...formatConfig, ...queryParams, missingValueBehaviour };

        return this.buildDownloadRequest(index, qp);
    }

    cleanup(index: string, config: any) {
        const url = `${this.dataLakeUrl}/${encodeURIComponent(index)}/cleanup`;
        const request = new HttpRequest('POST', url, config, {
            headers: new HttpHeaders({ 'Content-Type': 'application/json' }), // optional if already handled globally
        });

        return this.http.request(request);
    }

    deleteData(
        measurementName: string,
        startTime: number,
        endTime: number,
    ): Observable<void> {
        const params = new HttpParams()
            .set('startDate', startTime.toString())
            .set('endDate', endTime.toString());
        return this.http.delete<void>(
            `${this.dataLakeUrl}/measurements/${encodeURIComponent(measurementName)}`,
            { params },
        );
    }

    deleteCleanup(index: string) {
        const url = `${this.dataLakeUrl}/${encodeURIComponent(index)}/cleanup`;
        return this.http.delete(url);
    }

    runCleanupNow(index: string) {
        const url = `${this.dataLakeUrl}/${encodeURIComponent(index)}/runSyncNow`;
        const request = new HttpRequest('POST', url, {});

        return this.http.request(request);
    }

    buildDownloadRequest(index: string, queryParams: any) {
        const url =
            this.dataLakeUrl +
            '/measurements/' +
            encodeURIComponent(index) +
            '/download';
        const request = new HttpRequest('GET', url, {
            reportProgress: true,
            responseType: 'blob',
            params: this.toHttpParams(queryParams),
        });

        return this.http.request(request);
    }

    store(
        measureName: string,
        spQueryResult: SpQueryResult,
        _ignoreSchemaMismatch = true,
    ): Observable<void> {
        return this.http.post<void>(
            `${this.dataLakeUrl}/measurements/${encodeURIComponent(measureName)}`,
            spQueryResult,
            {},
        );
    }

    toHttpParams(queryParamObject: any): HttpParams {
        return new HttpParams({ fromObject: queryParamObject });
    }

    removeData(index: string) {
        const url =
            this.dataLakeUrl + '/measurements/' + encodeURIComponent(index);

        return this.http.delete(url);
    }

    previewImport(
        request: CsvImportPreviewRequest,
        file?: File,
    ): Observable<CsvImportPreviewResult> {
        if (file) {
            const formData = new FormData();
            formData.append('file', file, file.name);
            formData.append(
                'request',
                new Blob([JSON.stringify(request)], {
                    type: 'application/json',
                }),
            );

            return this.http.post<CsvImportPreviewResult>(
                `${this.dataLakeImportUrl}/preview`,
                formData,
            );
        }

        return this.http.post<CsvImportPreviewResult>(
            `${this.dataLakeImportUrl}/preview`,
            request,
        );
    }

    validateImportSchema(
        request: CsvImportSchemaValidationRequest,
    ): Observable<CsvImportSchemaValidationResult> {
        return this.http.post<CsvImportSchemaValidationResult>(
            `${this.dataLakeImportUrl}/validate-schema`,
            request,
        );
    }

    importCsvData(request: CsvImportRequest): Observable<CsvImportResult> {
        return this.http.post<CsvImportResult>(this.dataLakeImportUrl, request);
    }

    dropSingleMeasurementSeries(index: string) {
        const url =
            this.dataLakeUrl +
            '/measurements/' +
            encodeURIComponent(index) +
            '/drop';
        return this.http.delete(url);
    }

    dropAllMeasurementSeries() {
        const url = this.dataLakeUrl + '/measurements/';
        return this.http.delete(url);
    }

    private getQueryParameters(
        columns?: string,
        startDate?: number,
        endDate?: number,
        page?: number,
        limit?: number,
        offset?: number,
        groupBy?: string,
        order?: string,
        aggregationFunction?: string,
        timeInterval?: string,
    ): DatalakeQueryParameters {
        const queryParams: DatalakeQueryParameters = {};

        if (columns) {
            queryParams.columns = columns;
        }

        if (startDate) {
            queryParams.startDate = startDate;
        }

        if (endDate) {
            queryParams.endDate = endDate;
        }

        if (page) {
            queryParams.page = page;
        }

        if (limit) {
            queryParams.limit = limit;
        }

        if (offset) {
            queryParams.offset = offset;
        }

        if (groupBy) {
            queryParams.groupBy = groupBy;
        }

        if (order) {
            queryParams.order = order;
        }

        if (aggregationFunction) {
            queryParams.aggregationFunction = aggregationFunction;
        }

        if (timeInterval) {
            queryParams.timeInterval = timeInterval;
        }

        return queryParams;
    }
}
