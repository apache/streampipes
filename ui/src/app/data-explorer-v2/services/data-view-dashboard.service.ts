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

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';
import 'rxjs-compat/add/observable/of';
import { map } from 'rxjs/operators';
import { DashboardWidget } from '../../core-model/dashboard/DashboardWidget';
import { InfoResult } from '../../core-model/datalake/InfoResult';
import { DatalakeRestService } from '../../core-services/datalake/datalake-rest.service';
import { TsonLdSerializerService } from '../../platform-services/tsonld-serializer.service';
import { AuthStatusService } from '../../services/auth-status.service';
import { IDataViewDashboard } from '../models/dataview-dashboard.model';


@Injectable()
export class DataViewDashboardService {

    localDashboards: IDataViewDashboard[] = [];

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService,
                private tsonLdSerializerService: TsonLdSerializerService,
                private dataLakeRestService: DatalakeRestService) {
    }

    getVisualizableData(): Observable<InfoResult[]> {
        return this.dataLakeRestService.getAllInfos();
        // return this.http
        //     .get(this.visualizablePipelineUrl)
        //     .map(data => {
        //         return this.tsonLdSerializerService.fromJsonLdContainer(data, 'sp:VisualizablePipeline');
        //     });
    }

    getDataViews(): Observable<IDataViewDashboard[]> {
       return Observable.of(this.localDashboards);
    }

    updateDashboard(dashboard: IDataViewDashboard): Observable<IDataViewDashboard> {
        return this.http.put(this.dashboardUrl + '/' + dashboard._id, dashboard).map(data => {
            return data as IDataViewDashboard;
        });
    }

    deleteDashboard(dashboard: IDataViewDashboard): Observable<any> {
      return Observable.of(this.localDashboards);
    }

    saveDataView(dataViewDashboard: IDataViewDashboard): Observable<any> {
      this.localDashboards.push(dataViewDashboard);
      return Observable.of(this.localDashboards);
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dashboardUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/dashboards';
    }

    private get dashboardWidgetUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/widgets';
    }

    private get visualizablePipelineUrl() {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/ld/pipelines';
    }

    getWidget(widgetId: string): Observable<DashboardWidget> {
        const promise = new Promise<DashboardWidget>((resolve, reject) => {
            this.http.get(this.dashboardWidgetUrl + '/' + widgetId).subscribe(response => {
                const dashboardWidget: DashboardWidget = this.tsonLdSerializerService.fromJsonLd(response, 'sp:DashboardWidgetModel');
                dashboardWidget.dashboardWidgetSettings.config.sort((a, b) => {
                    return a.index - b.index;
                });
                resolve(dashboardWidget);
            });
        });
        return from(promise);
    }

    saveWidget(widget: DashboardWidget): Observable<DashboardWidget> {
        return this.serializeAndPost(this.dashboardWidgetUrl, widget);
    }

    deleteWidget(widgetId: string): Observable<any> {
        return this.http.delete(this.dashboardWidgetUrl + '/' + widgetId);
    }

    updateWidget(widget: DashboardWidget): Observable<any> {
        const promise = new Promise<DashboardWidget>((resolve, reject) => {
            this.tsonLdSerializerService.toJsonLd(widget).subscribe(serialized => {
                this.http.put(this.dashboardWidgetUrl + '/' + widget._id, serialized, this.jsonLdHeaders()).subscribe(result => {
                    resolve();
                });
            });
        });
        return from(promise);
    }

    serializeAndPost(url: string, object: any): Observable<DashboardWidget> {
        const promise = new Promise<DashboardWidget>((resolve, reject) => {
            this.tsonLdSerializerService.toJsonLd(object).subscribe(serialized => {
                this.http.post(url, serialized, this.jsonLdHeaders()).pipe(map(response => {
                    resolve(this.tsonLdSerializerService.fromJsonLd(response, 'sp:DashboardWidgetModel'));
                })).subscribe();
            });
        });
        return from(promise);
    }

    jsonLdHeaders(): any {
        return {
            headers: new HttpHeaders({
                'Content-Type': 'application/ld+json',
            }),
        };
    }
}
