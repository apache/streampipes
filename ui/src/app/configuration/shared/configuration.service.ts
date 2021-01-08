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

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {StreampipesPeContainer} from './streampipes-pe-container.model';
import {MessagingSettings} from './messaging-settings.model';
import {AuthStatusService} from "../../services/auth-status.service";
import {NodeInfoDescription} from "../../core-model/gen/streampipes-model";

@Injectable()
export class ConfigurationService {

    
    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService,) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }

    getMessagingSettings(): Observable<MessagingSettings> {
        return this.http.get(this.getServerUrl() + '/api/v2/consul/messaging')
            .pipe(
                map(response => {
                    return response as MessagingSettings;
                })
            )
    }

    getConsulServices(): Observable<StreampipesPeContainer[]> {
        return this.http.get(this.getServerUrl() + '/api/v2/consul')
            .pipe(
                map(response => {
                    for (let service of response as any[]) {
                        for (let config of service['configs']) {
                            if (config.valueType === 'xs:integer') {
                                config.value = parseInt(config.value);
                            } else if (config.valueType === 'xs:double') {
                                config.value = parseFloat('xs:double');
                            } else if (config.valueType === 'xs:boolean') {
                                config.value = (config.value === 'true');
                            }
                        }
                    }
                    return response as StreampipesPeContainer[];
                })
            );
    }

    updateConsulService(consulService: StreampipesPeContainer): Observable<Object> {
        return this.http.post(this.getServerUrl() + '/api/v2/consul', consulService);
    }

    updateMessagingSettings(messagingSettings: MessagingSettings):Observable<Object> {
        return this.http.post(this.getServerUrl() + '/api/v2/consul/messaging', messagingSettings);
    }


    adjustConfigurationKey(consulKey) {

            var removedKey = consulKey.substr(consulKey.lastIndexOf("/") + 1, consulKey.length);

            // console.log(removedKey);

            var str1 = removedKey.replace(/SP/g,"");
            str1 = str1.replace(/_/g," "); 
            if(str1.startsWith(" ")){
                str1 = str1.slice(1,str1.length)
            }
            return str1 
    }
}