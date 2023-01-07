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
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { MeasurementUnit } from '../model/measurement-unit/MeasurementUnit';
import { PlatformServicesCommons } from './commons.service';
import {
    DashboardWidgetModel,
    Pipeline,
    VisualizablePipeline,
} from '../model/gen/streampipes-model';
import { Dashboard } from '../model/dashboard/dashboard.model';

@Injectable({
    providedIn: 'root',
})
export class DashboardService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    getPipelineById(id: string): Observable<Pipeline> {
        return this.http.get(this.pipelinesUrl + '/' + id).pipe(
            map(data => {
                return Pipeline.fromData(data as any);
            }),
        );
    }

    getVisualizablePipelines(): Observable<VisualizablePipeline[]> {
        return this.http.get(this.visualizablePipelineUrl).pipe(
            map(data => {
                return (data as []).map(p =>
                    VisualizablePipeline.fromData(p as VisualizablePipeline),
                );
            }),
        );
    }

    getVisualizablePipelineByPipelineIdAndVisualizationName(
        pipelineId: string,
        visualizationName: string,
    ): Observable<VisualizablePipeline> {
        return this.http
            .get(
                this.visualizablePipelineUrl +
                    '/' +
                    pipelineId +
                    '/' +
                    visualizationName,
            )
            .pipe(
                map(data => {
                    return VisualizablePipeline.fromData(
                        data as VisualizablePipeline,
                    );
                }),
            );
    }

    getDashboards(): Observable<Dashboard[]> {
        return this.http.get(this.dashboardUrl).pipe(
            map(data => {
                return data as Dashboard[];
            }),
        );
    }

    getDashboard(dashboardId: string): Observable<Dashboard> {
        return this.http.get(this.dashboardUrl + '/' + dashboardId).pipe(
            map(data => {
                return data as Dashboard;
            }),
        );
    }

    getMeasurementUnitInfo(
        measurementUnitResource: string,
    ): Observable<MeasurementUnit> {
        return this.http
            .get(
                this.measurementUnitsUrl +
                    '/' +
                    encodeURIComponent(measurementUnitResource),
            )
            .pipe(
                map(data => {
                    return data as MeasurementUnit;
                }),
            );
    }

    updateDashboard(dashboard: Dashboard): Observable<Dashboard> {
        return this.http
            .put(this.dashboardUrl + '/' + dashboard._id, dashboard)
            .pipe(
                map(data => {
                    return data as Dashboard;
                }),
            );
    }

    deleteDashboard(dashboard: Dashboard): Observable<any> {
        return this.http.delete(this.dashboardUrl + '/' + dashboard._id);
    }

    saveDashboard(dashboard: Dashboard): Observable<any> {
        return this.http.post(this.dashboardUrl, dashboard);
    }

    private get baseUrl() {
        return this.platformServicesCommons.apiBasePath;
    }

    private get measurementUnitsUrl() {
        return this.baseUrl + '/measurement-units';
    }

    private get dashboardUrl() {
        return this.baseUrl + '/dashboard/dashboards';
    }

    private get pipelinesUrl() {
        return this.baseUrl + '/pipelines';
    }

    private get dashboardWidgetUrl() {
        return this.baseUrl + '/dashboard/widgets';
    }

    private get visualizablePipelineUrl() {
        return this.baseUrl + '/dashboard/pipelines';
    }

    getWidget(widgetId: string): Observable<DashboardWidgetModel> {
        return this.http.get(this.dashboardWidgetUrl + '/' + widgetId).pipe(
            map(d => {
                return DashboardWidgetModel.fromData(d as DashboardWidgetModel);
            }),
        );
    }

    saveWidget(widget: DashboardWidgetModel): Observable<DashboardWidgetModel> {
        return this.http.post(this.dashboardWidgetUrl, widget).pipe(
            map(response => {
                return DashboardWidgetModel.fromData(
                    response as DashboardWidgetModel,
                );
            }),
        );
    }

    deleteWidget(widgetId: string): Observable<any> {
        return this.http.delete(this.dashboardWidgetUrl + '/' + widgetId);
    }

    updateWidget(widget: DashboardWidgetModel): Observable<any> {
        return this.http.put(
            this.dashboardWidgetUrl + '/' + widget._id,
            widget,
        );
    }
}
