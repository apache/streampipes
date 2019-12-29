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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { VersionInfo } from './version-info.model';
import { SystemInfo } from "./system-info.model";

@Injectable()
export class VersionInfoService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getVersionInfo(): Observable<VersionInfo> {
        return this.http.get(this.getServerUrl() + '/api/v2/info/versions')
            .pipe(
                map(response => {
                    return response as VersionInfo;
                })
            );
    }

    getSysteminfo(): Observable<SystemInfo> {
        return this.http.get(this.getServerUrl() + '/api/v2/info/system')
            .pipe(
                map(response => {
                    return response as SystemInfo;
                })
            );
    }

}