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
import { DataExplorerWidgetModel } from '../../core-model/datalake/DataExplorerWidgetModel';
import { DataLakeMeasure } from '../../core-model/datalake/DataLakeMeasure';
import { DatalakeRestService } from '../../core-services/datalake/datalake-rest.service';
import { SharedDatalakeRestService } from '../../core-services/shared/shared-dashboard.service';
import { TsonLdSerializerService } from '../../platform-services/tsonld-serializer.service';
import { AuthStatusService } from '../../services/auth-status.service';
import { IDataViewDashboard } from '../models/dataview-dashboard.model';


@Injectable()
export class DataViewDataExplorerService {

  localDashboards: IDataViewDashboard[] = [];

  constructor(private http: HttpClient,
              private authStatusService: AuthStatusService,
              private tsonLdSerializerService: TsonLdSerializerService,
              private dataLakeRestService: DatalakeRestService,
              private sharedDatalakeRestService: SharedDatalakeRestService) {
  }

  getVisualizableData(): Observable<DataLakeMeasure[]> {
    return this.dataLakeRestService.getAllInfos().map(data => {
      return this.tsonLdSerializerService.fromJsonLdContainer(data, 'sp:DataLakeMeasure');
    });
  }

  getDataViews(): Observable<IDataViewDashboard[]> {
    return this.sharedDatalakeRestService.getDashboards(this.getDashboardUrl());
  }

  updateDashboard(dashboard: IDataViewDashboard): Observable<IDataViewDashboard> {
    return this.sharedDatalakeRestService.updateDashboard(this.getDashboardUrl(), dashboard);
  }

  deleteDashboard(dashboard: IDataViewDashboard): Observable<any> {
    return this.sharedDatalakeRestService.deleteDashboard(this.getDashboardUrl(), dashboard);
  }

  saveDataView(dataViewDashboard: IDataViewDashboard): Observable<any> {
    return this.sharedDatalakeRestService.saveDashboard(this.getDashboardUrl(), dataViewDashboard);
  }

  private getbaseUrl() {
    return '/streampipes-backend';
  }

  private getDashboardUrl() {
    return this.getbaseUrl() + '/api/v3/users/' + this.authStatusService.email + '/datalake/dashboard';
  }

  private getDashboardWidgetUrl() {
    return this.getbaseUrl() + '/api/v3/users/' + this.authStatusService.email + '/datalake/dashboard/widgets';
  }

  getWidget(widgetId: string): Observable<DataExplorerWidgetModel> {
    const promise = new Promise<DataExplorerWidgetModel>((resolve, reject) => {
      this.http.get(this.getDashboardWidgetUrl() + '/' + widgetId).subscribe(response => {
        this.tsonLdSerializerService.jsonLdToFlattenJsonLd(response).subscribe(res => {
          const dashboardWidget: DataExplorerWidgetModel =
            this.tsonLdSerializerService.fromJsonLd(res, 'sp:DataExplorerWidgetModel');
          resolve(dashboardWidget);
        });
      });
    });
    return from(promise);
  }

  saveWidget(widget: DataExplorerWidgetModel): Observable<DataExplorerWidgetModel> {
    const promise = new Promise<DataExplorerWidgetModel>((resolve, reject) => {
      this.tsonLdSerializerService.toJsonLd(widget).subscribe(serialized => {
        this.http.post(this.getDashboardWidgetUrl(), serialized, this.jsonLdHeaders()).pipe(map(response => {

          this.tsonLdSerializerService.jsonLdToFlattenJsonLd(response).subscribe(res => {
            resolve(this.tsonLdSerializerService.fromJsonLd(res, 'sp:DataExplorerWidgetModel'));
          });

        })).subscribe();
      });
    });
    return from(promise);
  }

  deleteWidget(widgetId: string): Observable<any> {
    return this.http.delete(this.getDashboardWidgetUrl() + '/' + widgetId);
  }

  updateWidget(widget: DataExplorerWidgetModel): Observable<any> {
    const promise = new Promise<DataExplorerWidgetModel>((resolve, reject) => {
      this.tsonLdSerializerService.toJsonLd(widget).subscribe(serialized => {
        this.http.put(this.getDashboardWidgetUrl() + '/' + widget._id, serialized, this.jsonLdHeaders()).subscribe(result => {
          resolve();
        });
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
