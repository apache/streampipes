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

import { HttpClient, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DataResult } from '../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../core-model/datalake/GroupedDataResult';
import { DataLakeMeasure } from '../../core-model/datalake/DataLakeMeasure';
import { PageResult } from '../../core-model/datalake/PageResult';
import { AuthStatusService } from '../../services/auth-status.service';
import { Observable } from "rxjs/Observable";

@Injectable()
export class DatalakeRestService {

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService) {
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dataLakeUrlV3() {
        return this.baseUrl + '/api/v3/users/' + this.authStatusService.email + '/datalake';
    }


    getAllInfos() {
        return this.http.get<DataLakeMeasure[]>(this.dataLakeUrlV3 + '/info');
    }

    getDataPage(index, itemsPerPage, page) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage + '&page=' + page);
    }

    getDataPageWithoutPage(index, itemsPerPage) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage);
    }

    getLastData(index, timeunit, value, aggregationTimeUnit, aggregationValue) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/last/' + value + '/' + timeunit + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getLastDataAutoAggregation(index, timeunit, value) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/last/' + value + '/' + timeunit);
    }

    getData(index, startDate, endDate, aggregationTimeUnit, aggregationValue): Observable<DataResult> {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getGroupedData(index, startDate, endDate, aggregationTimeUnit, aggregationValue, groupingTag) {
        return this.http.get<GroupedDataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '/grouping/' + groupingTag + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getDataAutoAggergation(index, startDate, endDate) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate);
    }

    getGroupedDataAutoAggergation(index, startDate, endDate, groupingTag) {
            return this.http.get<GroupedDataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '/grouping/' + groupingTag);
    }


    /*
        @deprecate
     */
    getFile(index, format) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + '?format=' + format,  {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request);
    }

    downloadRowData(index, format) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + '/download?format=' + format,  {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request);
    }

    downloadRowDataTimeInterval(index, format, startDate, endDate) {
        const request = new HttpRequest('GET', this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '/download' +
            '?format=' + format, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request);
    }

}
