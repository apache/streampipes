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
import { MultipartUtils } from './multipart-utils';
import {
    MessagingSettings,
    SpServiceConfiguration,
    SpServiceRegistration,
} from '@streampipes/platform-services';

@Injectable()
export class ConfigurationService {
    constructor(private http: HttpClient) {}

    getServerUrl() {
        return '/streampipes-backend';
    }

    generateKeyPair(): Observable<string[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/admin/general-config/keys', {
                responseType: 'text',
                observe: 'response',
            })
            .pipe(
                map(response => {
                    return new MultipartUtils().extractMultipartPlainTextContent(
                        response,
                    );
                }),
            );
    }

    getMessagingSettings(): Observable<MessagingSettings> {
        return this.http.get(this.getServerUrl() + '/api/v2/messaging').pipe(
            map(response => {
                return response as MessagingSettings;
            }),
        );
    }

    getRegisteredExtensionsServices(): Observable<SpServiceRegistration[]> {
        return this.http
            .get(this.getServerUrl() + '/api/v2/extensions-services')
            .pipe(
                map(response => {
                    return response as SpServiceRegistration[];
                }),
            );
    }

    getExtensionsServiceConfigs(): Observable<SpServiceConfiguration[]> {
        return this.http
            .get(
                this.getServerUrl() +
                    '/api/v2/extensions-services-configurations',
            )
            .pipe(
                map(response => {
                    return response as SpServiceConfiguration[];
                }),
            );
    }

    updateExtensionsServiceConfigs(
        config: SpServiceConfiguration,
    ): Observable<Object> {
        return this.http.put(
            this.getServerUrl() +
                `/api/v2/extensions-services-configurations/${config.serviceGroup}`,
            config,
        );
    }

    updateMessagingSettings(
        messagingSettings: MessagingSettings,
    ): Observable<Object> {
        return this.http.post(
            this.getServerUrl() + '/api/v2/messaging',
            messagingSettings,
        );
    }

    adjustConfigurationKey(consulKey) {
        const removedKey = consulKey.substr(
            consulKey.lastIndexOf('/') + 1,
            consulKey.length,
        );

        // console.log(removedKey);

        let str1 = removedKey.replace(/SP/g, '');
        str1 = str1.replace(/_/g, ' ');
        if (str1.startsWith(' ')) {
            str1 = str1.slice(1, str1.length);
        }
        return str1;
    }
}
