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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DatalakeQueryParameters, DataLakeMeasure, SpQueryResult } from '@streampipes/platform-services';

@Injectable()
export class DatalakeRestService {

  constructor(private http: HttpClient) {
  }

  private get baseUrl() {
    return '/streampipes-backend';
  }

  private get dataLakeUrlV3() {
    return this.baseUrl + '/api/v3' + '/datalake';
  }

  private get dataLakeUrlV4() {
    return this.baseUrl + '/api/v4' + '/datalake';
  }


  public getD(): Observable<DataLakeMeasure[]> {

    return null;
  }

  getAllInfos(): Observable<DataLakeMeasure[]> {
    return this.http.get(this.dataLakeUrlV4 + '/measurements').pipe(map(response => {
      return (response as any[]).map(p => DataLakeMeasure.fromData(p));
    }));
  }

  getData(index, startDate, endDate, aggregationTimeUnit, aggregationValue): Observable<SpQueryResult> {
    const timeInterval = aggregationValue + aggregationTimeUnit;
    const url = this.dataLakeUrlV4 + '/measurements/' + index;

    const queryParams: DatalakeQueryParameters = this.getQueryParameter(
      startDate,
      endDate,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      'mean',
      timeInterval
    );

    // @ts-ignore
    return this.http.get<SpQueryResult>(url, {
      // @ts-ignore
      params: queryParams
    });
  }


  getGroupedData(index, startDate, endDate, aggregationTimeUnit, aggregationValue, groupingTag): Observable<SpQueryResult> {
    const timeInterval = aggregationValue + aggregationTimeUnit;
    const url = this.dataLakeUrlV4 + '/measurements/' + index;

    const queryParams: DatalakeQueryParameters = this.getQueryParameter(
      startDate,
      endDate,
      undefined,
      undefined,
      undefined,
      undefined,
      groupingTag,
      undefined,
      'mean',
      timeInterval
    );

    // @ts-ignore
    return this.http.get<GroupedDataResult>(url, {
      // @ts-ignore
      params: queryParams
    });
  }

  getDataAutoAggregation(index, startDate, endDate) {
    return this.http.get<SpQueryResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate);
  }

  downloadRawData(index, format) {
    const url = this.dataLakeUrlV4 + '/measurements/' + index + '/download?format=' + format;

    const request = new HttpRequest('GET', url, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(request);
  }

  downloadRawDataTimeInterval(index, format, startDate, endDate) {
    const url = this.dataLakeUrlV4 + '/measurements/' + index + '/download?format=' + format +
      '&startDate=' + startDate + '&endDate=' + endDate;

    const request = new HttpRequest('GET', url, {
      reportProgress: true,
      responseType: 'text'
    });
    return this.http.request(request);
  }

  getDataPage(index, itemsPerPage, page) {
    const url = this.dataLakeUrlV4 + '/measurements/' + index;

    const queryParams: DatalakeQueryParameters = this.getQueryParameter(
      undefined,
      undefined,
      undefined,
      page,
      itemsPerPage,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined
    );

    // @ts-ignore
    return this.http.get<SpQueryResult>(url, {
      // @ts-ignore
      params: queryParams
    });
  }

  getDataPageWithoutPage(index, itemsPerPage) {
    const url = this.dataLakeUrlV4 + '/measurements/' + index;

    const queryParams: DatalakeQueryParameters = this.getQueryParameter(
      undefined,
      undefined,
      undefined,
      undefined,
      itemsPerPage,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined
    );

    // @ts-ignore
    return this.http.get<SpQueryResult>(url, {
      // @ts-ignore
      params: queryParams
    });
  }

  getImageUrl(imageRoute) {
    return this.dataLakeUrlV3 + '/data/image/' + imageRoute + '/file';
  }

  getCocoFileForImage(imageRoute) {
    return this.http.get(this.dataLakeUrlV3 + '/data/image/' + imageRoute + '/coco');
  }

  saveCocoFileForImage(imageRoute, data) {
    return this.http.post(this.dataLakeUrlV3 + '/data/image/' + imageRoute + '/coco', data);
  }

  removeAllData() {
    return this.http.delete(this.dataLakeUrlV3 + '/data/delete/all');
  }

  saveLabelsInDatabase(index, labelColumn, startDate, endDate, label, timestampColumn) {
    const request = new HttpRequest('POST', this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' +
      endDate + '/labeling/' + labelColumn + '/' + timestampColumn + '?label=' + label, {}, {
      reportProgress: true,
      responseType: 'text'
    });
    return this.http.request(request);
  }


  private getQueryParameter(
    _startDate,
    _endDate,
    _columns,
    _page,
    _limit,
    _offset,
    _goupBy,
    _order,
    _aggregationFunction,
    _timeInterval): DatalakeQueryParameters {

    const queryParams: DatalakeQueryParameters = new DatalakeQueryParameters();

    if (_startDate) {
      queryParams.startDate = _startDate;
    }

    if (_endDate) {
      queryParams.endDate = _endDate;
    }

    if (_columns) {
      queryParams.columns = _columns;
    }

    if (_page) {
      queryParams.page = _page;
    }

    if (_limit) {
      queryParams.limit = _limit;
    }

    if (_offset) {
      queryParams.offset = _offset;
    }

    if (_goupBy) {
      queryParams.groupBy = _goupBy;
    }

    if (_order) {
      queryParams.order = _order;
    }

    if (_aggregationFunction) {
      queryParams.aggregationFunction = _aggregationFunction;
    }

    if (_timeInterval) {
      queryParams.timeInterval = _timeInterval;
    }

    return queryParams;
  }
}
