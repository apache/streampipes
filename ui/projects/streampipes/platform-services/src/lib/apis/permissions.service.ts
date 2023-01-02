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
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Permission } from '../model/gen/streampipes-model-client';

@Injectable({
    providedIn: 'root',
})
export class PermissionsService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    public getPermissionsForObject(
        objectInstanceId: string,
    ): Observable<Permission[]> {
        return this.http
            .get(`${this.permissionsBasePath}/objects/${objectInstanceId}`)
            .pipe(map(response => response as Permission[]));
    }

    public updatePermission(permission: Permission): Observable<any> {
        return this.http.put(
            `${this.permissionsBasePath}/${permission.permissionId}`,
            permission,
        );
    }

    get permissionsBasePath() {
        return `${this.platformServicesCommons.apiBasePath}/admin/permissions`;
    }
}
