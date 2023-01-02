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
import { Group } from '../model/gen/streampipes-model-client';

@Injectable({
    providedIn: 'root',
})
export class UserGroupService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    public getAllUserGroups(): Observable<Group[]> {
        return this.http.get(`${this.userGroupPath}`).pipe(
            map(response => {
                return (response as any[]).map(p => Group.fromData(p));
            }),
        );
    }

    public createGroup(group: Group) {
        return this.http.post(this.userGroupPath, group);
    }

    public updateGroup(group: Group) {
        return this.http.put(`${this.userGroupPath}/${group.groupId}`, group);
    }

    public deleteGroup(group: Group) {
        return this.http.delete(`${this.userGroupPath}/${group.groupId}`);
    }

    public getGroup(groupId: string) {
        return this.http.get(`${this.userGroupPath}/${groupId}`);
    }

    private get userGroupPath() {
        return this.platformServicesCommons.apiBasePath + '/usergroups';
    }
}
