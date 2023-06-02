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
import {
    AdapterDescription,
    DataProcessorInvocation,
    DataSinkInvocation,
    PipelineElementTemplate,
} from '../model/gen/streampipes-model';
import { PlatformServicesCommons } from './commons.service';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class PipelineElementTemplateService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    getPipelineElementTemplates(
        appId: string,
    ): Observable<PipelineElementTemplate[]> {
        return this.http
            .get(
                this.platformServicesCommons.apiBasePath +
                    '/pipeline-element-templates?appId=' +
                    appId,
            )
            .pipe(
                map(data => {
                    return (data as []).map(dpi =>
                        PipelineElementTemplate.fromData(dpi),
                    );
                }),
            );
    }

    deletePipelineElementTemplate(templateId: string): Observable<any> {
        return this.http.delete(
            `${this.platformServicesCommons.apiBasePath}/pipeline-element-templates/${templateId}`,
        );
    }

    getConfiguredDataProcessorForTemplate(
        templateId: string,
        invocation: DataProcessorInvocation,
    ): Observable<DataProcessorInvocation> {
        return this.http
            .post(
                this.platformServicesCommons.apiBasePath +
                    '/pipeline-element-templates/' +
                    templateId +
                    '/processor',
                invocation,
            )
            .pipe(
                map(response => {
                    return DataProcessorInvocation.fromData(
                        response as DataProcessorInvocation,
                    );
                }),
            );
    }

    getConfiguredDataSinkForTemplate(
        templateId: string,
        invocation: DataSinkInvocation,
    ): Observable<DataSinkInvocation> {
        return this.http
            .post(
                this.platformServicesCommons.apiBasePath +
                    '/pipeline-element-templates/' +
                    templateId +
                    '/sink',
                invocation,
            )
            .pipe(
                map(response => {
                    return DataSinkInvocation.fromData(
                        response as DataSinkInvocation,
                    );
                }),
            );
    }

    getConfiguredAdapterForTemplate(
        templateId: string,
        adapter: AdapterDescription,
    ): Observable<AdapterDescription> {
        return this.http
            .post(
                this.platformServicesCommons.apiBasePath +
                    '/pipeline-element-templates/' +
                    templateId +
                    '/adapter',
                adapter,
            )
            .pipe(
                map(res =>
                    AdapterDescription.fromData(res as AdapterDescription),
                ),
            );
    }

    storePipelineElementTemplate(template: PipelineElementTemplate) {
        return this.http.post(
            this.platformServicesCommons.apiBasePath +
                '/pipeline-element-templates',
            template,
        );
    }
}
