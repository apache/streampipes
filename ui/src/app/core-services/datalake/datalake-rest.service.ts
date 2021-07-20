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
import { DataResult } from '../../core-model/datalake/DataResult';
import { GroupedDataResult } from '../../core-model/datalake/GroupedDataResult';
import { PageResult } from '../../core-model/datalake/PageResult';
import { AuthStatusService } from '../../services/auth-status.service';
import {DataLakeMeasure} from "../../core-model/gen/streampipes-model";
import {map} from "rxjs/operators";

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

    private get dataLakeUrlV4() {
      return this.baseUrl + '/api/v4/users/' + this.authStatusService.email + '/datalake';
    }


    public getD(): Observable<DataLakeMeasure[]> {

      return null;
    }

  getAllInfos(): Observable<DataLakeMeasure[]> {
        return this.http.get(this.dataLakeUrlV3 + '/info').pipe(map(response => {
          return (response as any[]).map(p => DataLakeMeasure.fromData(p));
        }));
    }

    getDataPage(index, itemsPerPage, page) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage + '&page=' + page);
    }

    getDataPageWithoutPage(index, itemsPerPage) {
        return this.http.get<PageResult>(this.dataLakeUrlV3 + '/data/' + index + '/paging?itemsPerPage=' + itemsPerPage);
    }

    getData(index, startDate, endDate, aggregationTimeUnit, aggregationValue): Observable<DataResult> {
        // return this.http.get<DataResult>(
        //   this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);

      const url = this.dataLakeUrlV4 + '/measurements/' + index;

      return this.http.get<PageResult>(url, {
        params: {
          // columns: 'id1234',
          startDate: startDate,
          endDate: endDate,
          // page: 0,
          // limit: 1,
          // offset: 1,
          // groupBy: 'asd,sadf',
          // order: 'ASC',
          aggregationFunction: 'mean',
          timeInterval: '1s'
        }
      });
    }

    getGroupedData(index, startDate, endDate, aggregationTimeUnit, aggregationValue, groupingTag) {
        return this.http.get<GroupedDataResult>(
          this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate + '/grouping/' + groupingTag +
          '?aggregationUnit=' + aggregationTimeUnit + '&aggregationValue=' + aggregationValue);
    }

    getDataAutoAggregation(index, startDate, endDate) {
        return this.http.get<DataResult>(this.dataLakeUrlV3 + '/data/' + index + '/' + startDate + '/' + endDate);
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
            endDate + '/labeling/' + labelColumn + '/' + timestampColumn + '?label=' + label ,  {}, {
            reportProgress: true,
            responseType: 'text'
        });
        return this.http.request(request);
    }

}
