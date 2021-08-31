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
import { HttpClient, HttpRequest } from '@angular/common/http';
import { AuthStatusService } from '../../services/auth-status.service';
import { Observable } from 'rxjs';
import { DataLakeMeasure } from '../../core-model/gen/streampipes-model';
import { map } from 'rxjs/operators';
import { DataResult } from '../../core-model/datalake/DataResult';
import { DatalakeQueryParameters } from '../../core-services/datalake/DatalakeQueryParameters';
import { PageResult } from '../../core-model/datalake/PageResult';
import { GroupedDataResult } from '../../core-model/datalake/GroupedDataResult';

@Injectable()
export class DatalakeRestService {
  constructor(private http: HttpClient,
              private authStatusService: AuthStatusService) {
  }

  private get baseUrl() {
    return '/streampipes-backend';
  }

  private get dataLakeUrl() {
    return this.baseUrl + '/api/v4/users/' + this.authStatusService.email + '/datalake';
  }

  getAllMeasurementSeries(): Observable<DataLakeMeasure[]> {
    const url = this.dataLakeUrl + '/measurements/';
    return this.http.get(url).pipe(map(response => {
      return (response as any[]).map(p => DataLakeMeasure.fromData(p));
    }));
  }

  getData(index: string,
          queryParams: DatalakeQueryParameters): Observable<DataResult> {
    const url = this.dataLakeUrl + '/measurements/' + index;

    // @ts-ignore
    return this.http.get<DataResult>(url, { params: queryParams });
  }

  // getData(index, startDate, endDate, columns, aggregationFunction, aggregationTimeUnit, aggregationTimeValue): Observable<DataResult> {
  //     const timeInterval = aggregationTimeValue + aggregationTimeUnit;
  //
  //     const queryParams: DatalakeQueryParameters = this.getQueryParameters(columns, startDate, endDate, undefined, undefined,
  //         undefined, undefined, undefined, aggregationFunction, timeInterval);
  //
  //     // @ts-ignore
  //     return this.http.get<DataResult>(url, {params: queryParams});
  // }

  getPagedData(index: string, itemsPerPage: number, page: number, columns?: string, order?: string): Observable<PageResult> {
    const url = this.dataLakeUrl + '/measurements/' + index;

    const queryParams: DatalakeQueryParameters = this.getQueryParameters(columns, undefined, undefined, page,
      itemsPerPage, undefined, undefined, order, undefined, undefined);

    // @ts-ignore
    return this.http.get<PageResult>(url, { params: queryParams });
  }

  getGroupedData(index: string, groupingTags: string, aggregationFunction?: string, columns?: string, startDate?: number, endDate?:
    number, aggregationTimeUnit?: string, aggregationTimeValue?: number, order?: string, limit?: number):
    Observable<GroupedDataResult> {

    const url = this.dataLakeUrl + '/measurements/' + index;
    let _aggregationFunction = 'mean';
    let timeInterval = '2000ms';

    if (aggregationFunction) {
      _aggregationFunction = aggregationFunction;
    }

    if (aggregationTimeUnit && aggregationTimeValue) {
      timeInterval = aggregationTimeValue + aggregationTimeUnit;
    }

    const queryParams: DatalakeQueryParameters = this.getQueryParameters(columns, startDate, endDate, undefined, limit,
      undefined, groupingTags, order, _aggregationFunction, timeInterval);

    // @ts-ignore
    return this.http.get<GroupedDataResult>(url, { params: queryParams });
  }

  downloadRawData(index, format) {
    const url = this.dataLakeUrl + '/measurements/' + index + '/download?format=' + format;

    const request = new HttpRequest('GET', url, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(request);
  }

  downloadQueriedData(
    index,
    format,
    startDate?,
    endDate?,
    columns?,
    aggregationFunction?,
    aggregationTimeUnit?,
    aggregationTimeValue?,
    groupingsTags?,
    order?,
    limit?,
    offset?) {
    const url = this.dataLakeUrl + '/measurements/' + index + '/download?format=' + format;
    const timeInterval = aggregationTimeValue + aggregationTimeUnit;

    const queryParams: DatalakeQueryParameters = this.getQueryParameters(columns, startDate, endDate, undefined,
      limit, offset, groupingsTags, order, aggregationFunction, timeInterval);

    const request = new HttpRequest('GET', url, {
      reportProgress: true,
      responseType: 'text',
      params: queryParams
    });

    return this.http.request(request);
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

  private getQueryParameters(columns?: string, startDate?: number, endDate?: number, page?: number, limit?: number,
                             offset?: number, groupBy?: string, order?: string, aggregationFunction?: string, timeInterval?: string):
    DatalakeQueryParameters {
    const queryParams: DatalakeQueryParameters = new DatalakeQueryParameters();

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
