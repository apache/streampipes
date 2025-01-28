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

import { HttpClient } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import {
    DataExplorerWidgetModel,
    DataLakeMeasure,
} from '../model/gen/streampipes-model';

@Injectable({
    providedIn: 'root',
})
export class ChartService {
    constructor(private http: HttpClient) {}

    getAllCharts(): Observable<DataExplorerWidgetModel[]> {
        return this.http
            .get(this.dashboardWidgetUrl)
            .pipe(map(res => res as DataExplorerWidgetModel[]));
    }

    getChart(widgetId: string): Observable<DataExplorerWidgetModel> {
        return this.http.get(this.dashboardWidgetUrl + '/' + widgetId).pipe(
            catchError(() => {
                return throwError(() => new Error('Failed to get widget data'));
            }),
            map(response => {
                return DataExplorerWidgetModel.fromData(
                    response as DataExplorerWidgetModel,
                );
            }),
        );
    }

    saveChart(
        widget: DataExplorerWidgetModel,
    ): Observable<DataExplorerWidgetModel> {
        return this.http.post(this.dashboardWidgetUrl, widget).pipe(
            map(response => {
                return DataExplorerWidgetModel.fromData(
                    response as DataExplorerWidgetModel,
                );
            }),
        );
    }

    deleteChart(widgetId: string): Observable<any> {
        return this.http.delete(`${this.dashboardWidgetUrl}/${widgetId}`);
    }

    updateChart(widget: DataExplorerWidgetModel): Observable<any> {
        return this.http.put(
            this.dashboardWidgetUrl + '/' + widget.elementId,
            widget,
        );
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get persistedDataStreamsUrl() {
        return `${this.baseUrl}/api/v3/datalake/pipelines`;
    }

    private get dashboardWidgetUrl() {
        return `${this.baseUrl}/api/v3/datalake/dashboard/widgets`;
    }

    getPersistedDataStream(
        pipelineId: string,
        measureName: string,
    ): Observable<DataLakeMeasure> {
        return this.http
            .get(`${this.persistedDataStreamsUrl}/${pipelineId}/${measureName}`)
            .pipe(map(response => DataLakeMeasure.fromData(response as any)));
    }

    getAllPersistedDataStreams(): Observable<DataLakeMeasure[]> {
        return this.http.get(this.persistedDataStreamsUrl).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    DataLakeMeasure.fromData(p),
                );
            }),
        );
    }
}
