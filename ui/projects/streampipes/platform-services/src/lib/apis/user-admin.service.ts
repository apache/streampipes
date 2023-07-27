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

import { Observable } from 'rxjs';
import {
    ServiceAccount,
    UserAccount,
} from '../model/gen/streampipes-model-client';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class UserAdminService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    getAllUserAccounts(): Observable<UserAccount[]> {
        return this.http.get(`${this.usersAdminPath}?type=USER_ACCOUNT`).pipe(
            map(response => {
                return (response as any[]).map(p => UserAccount.fromData(p));
            }),
        );
    }

    getAllServiceAccounts(): Observable<ServiceAccount[]> {
        return this.http
            .get(`${this.usersAdminPath}?type=SERVICE_ACCOUNT`)
            .pipe(
                map(response => {
                    return (response as any[]).map(p =>
                        ServiceAccount.fromData(p),
                    );
                }),
            );
    }

    private get usersAdminPath() {
        return this.platformServicesCommons.apiBasePath + '/admin/users';
    }
}
