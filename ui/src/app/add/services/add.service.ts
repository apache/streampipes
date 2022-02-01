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
import { PlatformServicesCommons } from '../../../../projects/streampipes/platform-services/src/lib/apis/commons.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ExtensionsServiceEndpointItem } from '../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model-client';

@Injectable()
export class AddService {

  constructor(private http: HttpClient,
              private platformServicesCommons: PlatformServicesCommons) {
  }

  getRdfEndpoints(): Observable<any> {
    return this.http.get(this.platformServicesCommons.apiBasePath + '/rdfendpoints');
  }

  getRdfEndpointItems(): Observable<ExtensionsServiceEndpointItem[]> {
    return this
        .http
        .get(this.platformServicesCommons.apiBasePath + '/rdfendpoints/items')
        .pipe(map(response => {
          return (response as any[]).map(item => ExtensionsServiceEndpointItem.fromData(item));
        }));
  }

  addRdfEndpoint(rdfEndpoint): Observable<any> {
    return this.http.post(this.platformServicesCommons.apiBasePath + '/rdfendpoints', rdfEndpoint);
  }

  removeRdfEndpoint(rdfEndpointId): Observable<any> {
    return this.http.delete(this.platformServicesCommons.apiBasePath + '/rdfendpoints/' + rdfEndpointId);
  }

  getRdfEndpointIcon(item: ExtensionsServiceEndpointItem): Observable<any> {
    return this.http.post(this.platformServicesCommons.apiBasePath
        + '/rdfendpoints/items/icon', item, {responseType: 'blob'});
  }
}
