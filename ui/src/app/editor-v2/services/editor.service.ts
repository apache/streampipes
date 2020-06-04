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

import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {AuthStatusService} from "../../services/auth-status.service";
import {TsonLdSerializerService} from "../../platform-services/tsonld-serializer.service";
import {Observable} from "rxjs";
import {DataProcessorInvocation, DataSinkInvocation} from "../../core-model/gen/streampipes-model";
import {DataSourceDescription} from "../../connect/model/DataSourceDescription";

@Injectable()
export class EditorService {

    constructor(private http: HttpClient,
                private authStatusService: AuthStatusService,
                private tsonLdSerializerService: TsonLdSerializerService) {
    }

    getDataProcessors(): Observable<Array<DataProcessorInvocation>> {
        return this.http.get(this.dataProcessorsUrl + "/own").map(data => {
            return (data as []).map(dpi => DataProcessorInvocation.fromData(dpi));
        })
    }

    getDataSinks(): Observable<Array<DataSinkInvocation>> {
        return this.http.get(this.dataSinksUrl + "/own", this.jsonLdHeaders()).map(data => {
            return this.tsonLdSerializerService.fromJsonLdContainer(data, "sp:DataSinkInvocation");
        })
    }

    getDataSources(): Observable<Array<DataSourceDescription>> {
        return this.http.get(this.dataSourcesUrl + "/own", this.jsonLdHeaders()).map(data => {
            return this.tsonLdSerializerService.fromJsonLdContainer(data, "sp:DataSourceDescription");
        })
    }

    private get baseUrl(): string {
        return '/streampipes-backend';
    }

    private get dataProcessorsUrl(): string {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/sepas'
    }

    private get dataSourcesUrl(): string {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/sources'
    }

    private get dataSinksUrl(): string {
        return this.baseUrl + '/api/v2/users/' + this.authStatusService.email + '/actions'
    }

    private get ownPath() {
        return "/own";
    }

    jsonLdHeaders(): any {
        return {
            headers: new HttpHeaders({
                'Accept': 'application/ld+json',
            }),
        };
    }

}