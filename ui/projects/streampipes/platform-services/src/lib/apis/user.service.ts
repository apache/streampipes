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
import { Observable } from 'rxjs';
import { PlatformServicesCommons } from './commons.service';
import {
    ServiceAccount,
    UserAccount,
} from '../model/gen/streampipes-model-client';
import { ChangePasswordRequest } from '../model/user/user.model';
import { ShortUserInfo } from '../model/gen/streampipes-model';

@Injectable({
    providedIn: 'root',
})
export class UserService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    public listUsers(): Observable<ShortUserInfo[]> {
        return this.http.get(this.usersPath).pipe(
            map(response => {
                return (response as any[]).map(p => ShortUserInfo.fromData(p));
            }),
        );
    }

    public updateUser(user: UserAccount): Observable<any> {
        return this.http.put(
            `${this.usersPath}/user/${user.principalId}`,
            user,
        );
    }

    public updateUsername(user: UserAccount): Observable<any> {
        return this.http.put(
            `${this.usersPath}/user/${user.principalId}/username`,
            user,
        );
    }

    public updatePassword(
        user: UserAccount,
        req: ChangePasswordRequest,
    ): Observable<any> {
        return this.http.put(
            `${this.usersPath}/user/${user.principalId}/password`,
            req,
        );
    }

    public updateService(user: ServiceAccount): Observable<any> {
        return this.http.put(
            `${this.usersPath}/service/${user.principalId}`,
            user,
        );
    }

    public createUser(user: UserAccount): Observable<any> {
        return this.http.post(`${this.usersPath}/user`, user);
    }

    public createServiceAccount(user: ServiceAccount): Observable<any> {
        return this.http.post(`${this.usersPath}/service`, user);
    }

    public deleteUser(principalId: string): Observable<any> {
        return this.http.delete(`${this.usersPath}/${principalId}`);
    }

    public getUserById(
        principalId: string,
    ): Observable<UserAccount | ServiceAccount> {
        return this.http.get(`${this.usersPath}/${principalId}`).pipe(
            map(response => {
                if ((response as any).principalType === 'USER_ACCOUNT') {
                    return UserAccount.fromData(response as any);
                } else {
                    return ServiceAccount.fromData(response as any);
                }
            }),
        );
    }

    private get usersPath() {
        return this.platformServicesCommons.apiBasePath + '/users';
    }
}
