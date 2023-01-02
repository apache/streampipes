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

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import {
    DataLakeMeasure,
    PageResult,
    SpQueryResult,
} from '../model/gen/streampipes-model';
import { map } from 'rxjs/operators';
import { DatalakeQueryParameters } from '../model/datalake/DatalakeQueryParameters';

@Injectable({
    providedIn: 'root',
})
export class DatalakeRestService {
    constructor(private http: HttpClient) {}

    private get baseUrl() {
        return '/streampipes-backend';
    }

    public get dataLakeUrl() {
        return this.baseUrl + '/api/v4' + '/datalake';
    }

    public get dataLakeMeasureUrl() {
        return this.baseUrl + '/api/v4/datalake/measure';
    }

    getAllMeasurementSeries(): Observable<DataLakeMeasure[]> {
        const url = this.dataLakeUrl + '/measurements/';
        return this.http.get(url).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    DataLakeMeasure.fromData(p),
                );
            }),
        );
    }

    getMeasurement(id: string): Observable<DataLakeMeasure> {
        return this.http
            .get(`${this.dataLakeMeasureUrl}/${id}`)
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
        ignoreLoadingBar?: boolean,
    ): Observable<SpQueryResult> {
        const columns = queryParams.columns;
        if (columns === '') {
            const emptyQueryResult = new SpQueryResult();
            emptyQueryResult.total = 0;
            return of(emptyQueryResult);
        } else {
            const url = this.dataLakeUrl + '/measurements/' + index;
            const headers = ignoreLoadingBar ? { ignoreLoadingBar: '' } : {};
            // @ts-ignore
            return this.http.get<SpQueryResult>(url, {
                params: queryParams as unknown as HttpParams,
                headers,
            });
        }
    }

    getPagedData(
        index: string,
        itemsPerPage: number,
        page: number,
        columns?: string,
        order?: string,
    ): Observable<PageResult> {
        const url = this.dataLakeUrl + '/measurements/' + index;

        const queryParams: DatalakeQueryParameters = this.getQueryParameters(
            columns,
            undefined,
            undefined,
            page,
            itemsPerPage,
            undefined,
            undefined,
            order,
            undefined,
            undefined,
        );

        // @ts-ignore
        return this.http.get<PageResult>(url, { params: queryParams });
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
                        index +
                        '/tags?fields=' +
                        fieldNames.toString(),
                )
                .pipe(map(r => r as Map<string, string[]>));
        }
    }

    downloadRawData(
        index: string,
        format: string,
        delimiter: string,
        missingValueBehaviour: string,
        startTime?: number,
        endTime?: number,
    ) {
        const queryParams =
            startTime && endTime
                ? { format, delimiter, startDate: startTime, endDate: endTime }
                : {
                      format,
                      delimiter,
                      missingValueBehaviour,
                  };
        return this.buildDownloadRequest(index, queryParams);
    }

    downloadQueriedData(
        index: string,
        format: string,
        delimiter: string,
        missingValueBehaviour: string,
        queryParams: DatalakeQueryParameters,
    ) {
        (queryParams as any).format = format;
        (queryParams as any).delimiter = delimiter;
        (queryParams as any).missingValueBehaviour = missingValueBehaviour;

        return this.buildDownloadRequest(index, queryParams);
    }

    buildDownloadRequest(index: string, queryParams: any) {
        const url = this.dataLakeUrl + '/measurements/' + index + '/download';
        const request = new HttpRequest('GET', url, {
            reportProgress: true,
            responseType: 'text',
            params: this.toHttpParams(queryParams),
        });

        return this.http.request(request);
    }

    toHttpParams(queryParamObject: any): HttpParams {
        return new HttpParams({ fromObject: queryParamObject });
    }

    removeData(index: string) {
        const url = this.dataLakeUrl + '/measurements/' + index;

        return this.http.delete(url);
    }

    dropSingleMeasurementSeries(index: string) {
        const url = this.dataLakeUrl + '/measurements/' + index + '/drop';
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
        const queryParams: DatalakeQueryParameters =
            new DatalakeQueryParameters();

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
