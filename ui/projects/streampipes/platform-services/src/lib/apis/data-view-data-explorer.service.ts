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

import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { Dashboard } from '../model/dashboard/dashboard.model';
import { Injectable } from '@angular/core';
import {
    DataExplorerWidgetModel,
    DataLakeMeasure,
} from '../model/gen/streampipes-model';
import { SharedDatalakeRestService } from './shared-dashboard.service';

@Injectable({
    providedIn: 'root',
})
export class DataViewDataExplorerService {
    constructor(
        private http: HttpClient,
        private sharedDatalakeRestService: SharedDatalakeRestService,
    ) {}

    getDataViews(): Observable<Dashboard[]> {
        return this.sharedDatalakeRestService.getDashboards(this.dashboardUrl);
    }

    getDashboard(dashboardId: string): Observable<Dashboard> {
        return this.http
            .get(`${this.dashboardUrl}/${dashboardId}`)
            .pipe(map(data => data as Dashboard));
    }

    updateDashboard(dashboard: Dashboard): Observable<Dashboard> {
        return this.sharedDatalakeRestService.updateDashboard(
            this.dashboardUrl,
            dashboard,
        );
    }

    deleteDashboard(dashboard: Dashboard): Observable<any> {
        return this.sharedDatalakeRestService.deleteDashboard(
            this.dashboardUrl,
            dashboard,
        );
    }

    saveDataView(dataViewDashboard: Dashboard): Observable<any> {
        return this.sharedDatalakeRestService.saveDashboard(
            this.dashboardUrl,
            dataViewDashboard,
        );
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get persistedDataStreamsUrl() {
        return `${this.baseUrl}/api/v3/datalake/pipelines`;
    }

    private get dashboardUrl() {
        return `${this.baseUrl}/api/v3/datalake/dashboard`;
    }

    private get dashboardWidgetUrl() {
        return `${this.baseUrl}/api/v3/datalake/dashboard/widgets`;
    }

    getAllWidgets(): Observable<DataExplorerWidgetModel[]> {
        return this.http
            .get(this.dashboardWidgetUrl)
            .pipe(map(res => res as DataExplorerWidgetModel[]));
    }

    getWidget(widgetId: string): Observable<DataExplorerWidgetModel> {
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

    saveWidget(
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

    deleteWidget(widgetId: string): Observable<any> {
        return this.http.delete(`${this.dashboardWidgetUrl}/${widgetId}`);
    }

    updateWidget(widget: DataExplorerWidgetModel): Observable<any> {
        return this.http.put(
            this.dashboardWidgetUrl + '/' + widget.elementId,
            widget,
        );
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
