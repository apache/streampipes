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
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { AuthStatusService } from '../../services/auth-status.service';
import { ConnectService } from './connect.service';
import {
  AdapterDescription,
  AdapterDescriptionUnion,
  GenericAdapterSetDescription,
  GenericAdapterStreamDescription,
  Message,
  SpecificAdapterSetDescription,
  SpecificAdapterStreamDescription
} from '../../core-model/gen/streampipes-model';
import { Observable } from 'rxjs';
import { PlatformServicesCommons } from '../../platform-services/apis/commons.service';

@Injectable()
export class DataMarketplaceService {

  constructor(
    private http: HttpClient,
    private authStatusService: AuthStatusService,
    private connectService: ConnectService,
    private platformServicesCommons: PlatformServicesCommons) {
  }

  get connectPath() {
    return `${this.platformServicesCommons.apiBasePath()}/connect`;
  }

  getAdapterDescriptions(): Observable<AdapterDescriptionUnion[]> {
    return this.requestAdapterDescriptions('/master/description/adapters');

  }

  getAdapters(): Observable<AdapterDescriptionUnion[]> {
    return this.requestAdapterDescriptions('/master/adapters');
  }

  requestAdapterDescriptions(path: string): Observable<AdapterDescriptionUnion[]> {
    return this.http
      .get(
        this.connectPath +
        path
      )
      .pipe(map(response => {
        return (response as any[]).map(p => AdapterDescription.fromDataUnion(p));
      }));
  }

  stopAdapter(adapter: AdapterDescriptionUnion): Observable<Message> {
    return this.http.post(this.adapterMasterUrl
      + adapter.elementId
      + '/stop', {})
      .pipe(map(response => Message.fromData(response as any)));
  }

  startAdapter(adapter: AdapterDescriptionUnion): Observable<Message> {
    return this.http.post(this.adapterMasterUrl
      + adapter.elementId
      + '/start', {})
      .pipe(map(response => Message.fromData(response as any)));
  }

  get adapterMasterUrl() {
    return `${this.connectPath}/master/adapters/`;
  }

  deleteAdapter(adapter: AdapterDescription): Observable<any> {
    return this.deleteRequest(adapter, '/master/adapters/');
  }

  getAdapterCategories(): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/api/v2/categories/adapter`);
  }

  private deleteRequest(adapter: AdapterDescription, url: string) {
    return this.http.delete(
      this.connectPath +
      url +
      adapter.elementId
    );
  }

  cloneAdapterDescription(toClone: AdapterDescriptionUnion): AdapterDescriptionUnion {
    let result: AdapterDescriptionUnion;

    if (this.connectService.isGenericDescription(toClone)) {
      if (toClone instanceof GenericAdapterStreamDescription) {
        result = GenericAdapterStreamDescription.fromData(toClone, new GenericAdapterStreamDescription());
      } else if (toClone instanceof GenericAdapterSetDescription) {
        result = GenericAdapterSetDescription.fromData(toClone, new GenericAdapterSetDescription());
      }
    } else {
      if (toClone instanceof SpecificAdapterStreamDescription) {
        result = SpecificAdapterStreamDescription.fromData(toClone, new SpecificAdapterStreamDescription());
      } else if (toClone instanceof SpecificAdapterSetDescription) {
        result = SpecificAdapterSetDescription.fromData(toClone, new SpecificAdapterSetDescription());
      }
    }

    return result;
  }

  getAssetUrl(appId) {
    return `${this.connectPath}/master/description/${appId}/assets`;
  }

  private get baseUrl() {
    return '/streampipes-backend';
  }
}
