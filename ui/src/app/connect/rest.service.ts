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

import {HttpClient, HttpHeaders} from '@angular/common/http';

import {from, Observable} from 'rxjs';
import {map} from 'rxjs/operators';


import {AuthStatusService} from '../services/auth-status.service';
import {UnitDescription} from './model/UnitDescription';
import {
    AdapterDescription, DataSourceDescription,
    ErrorMessage,
    FormatDescriptionList,
    GuessSchema,
    Message,
    ProtocolDescriptionList,
    RuntimeOptionsResponse
} from "../core-model/gen/streampipes-model";
import {StatusMessage} from "./model/message/StatusMessage";

@Injectable()
export class RestService {
    private host = '/streampipes-backend/';

    constructor(
        private http: HttpClient,
        private authStatusService: AuthStatusService) { }

    addAdapter(adapter: AdapterDescription): Observable<Message> {
        return this.addAdapterDescription(adapter, '/master/adapters');
    }

    addAdapterTemplate(adapter: AdapterDescription): Observable<Message> {
        return this.addAdapterDescription(adapter, '/master/adapters/template');
    }

    fetchRemoteOptions(resolvableOptionsParameterRequest: any, adapterId: string): Observable<RuntimeOptionsResponse> {
        resolvableOptionsParameterRequest["@class"] = "org.apache.streampipes.model.runtime.RuntimeOptionsRequest";
        return this.http.post("/streampipes-connect/api/v1/"
            + this.authStatusService.email
            + "/master/resolvable/"
            + encodeURIComponent(adapterId)
            + "/configurations", resolvableOptionsParameterRequest)
            .pipe(map(response => {
                return RuntimeOptionsResponse.fromData(response as RuntimeOptionsResponse);
            }));
    }

    addAdapterDescription(adapter: AdapterDescription, url: String): Observable<Message> {
        adapter.userName = this.authStatusService.email;

        let promise = new Promise<Message>((resolve, reject) => {
            this.http
                .post(
                    '/streampipes-connect/api/v1/' + this.authStatusService.email + url,
                    adapter,
                )
                .pipe(map(response => {
                    var statusMessage = response as Message;
                    resolve(statusMessage);
                }))
                .subscribe();
        });
        return from(promise);
    }


    getGuessSchema(adapter: AdapterDescription): Observable<GuessSchema> {
        return this.http
            .post('/streampipes-connect/api/v1/' + this.authStatusService.email + '/master/guess/schema', adapter)
            .pipe(map(response => {
                return GuessSchema.fromData(response as GuessSchema);
        }))

    }

    getSourceDetails(sourceElementId): Observable<DataSourceDescription> {
        return this.http
            .get(this.makeUserDependentBaseUrl() + "/sources/" + encodeURIComponent(sourceElementId)).pipe(map(response => {
                return DataSourceDescription.fromData(response as DataSourceDescription);
            }));
    }

    getRuntimeInfo(sourceDescription): Observable<any> {
        return this.http.post(this.makeUserDependentBaseUrl() + "/pipeline-element/runtime", sourceDescription, {
            headers: { ignoreLoadingBar: '' }
        });
    }

    makeUserDependentBaseUrl() {
        return this.host + 'api/v2/users/' + this.authStatusService.email;
    }


    getFormats(): Observable<FormatDescriptionList> {
        var self = this;
        return this.http
            .get(
                '/streampipes-connect/api/v1/riemer@fzi.de/master/description/formats'
            )
            .pipe(map(response => {
                return FormatDescriptionList.fromData(response as FormatDescriptionList);
            }));
    }

    getProtocols(): Observable<ProtocolDescriptionList> {
        return this.http
            .get(this.host + 'api/v2/adapter/allProtocols')
            .pipe(map(response => {
                return response as ProtocolDescriptionList;
            }));
    }

    getFittingUnits(unitDescription: UnitDescription): Observable<UnitDescription[]> {
        return this.http
            .post<UnitDescription[]>('/streampipes-connect/api/v1/' + this.authStatusService.email + '/master/unit', unitDescription)
            .pipe(map(response => {
                const descriptions = response as UnitDescription[];
                return descriptions.filter(entry => entry.resource != unitDescription.resource)
            }));
    }


}
