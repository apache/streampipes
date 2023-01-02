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
import {
    FreeTextStaticProperty,
    PipelineOperationStatus,
    PipelineTemplateInvocation,
    StaticPropertyUnion,
} from '../model/gen/streampipes-model';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class PipelineTemplateService {
    constructor(private http: HttpClient) {}

    getServerUrl() {
        return '/streampipes-backend';
    }

    // getDataSets(): Observable<DataSetDescription[]> {
    //     return this.http
    //         .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates/streams')
    //         .pipe(map(response => {
    //
    //
    //
    //             // TODO remove this
    //             // quick fix to deserialize URIs
    //             response['@graph'].forEach(function (object) {
    //                if (object['sp:domainProperty'] != undefined) {
    //                    // object['sp:domainProperty']['@type'] = "sp:URI";
    //                    object['sp:domainProperty'] = object['sp:domainProperty']['@id'];
    //                    delete object['sp:domainProperty']['@id'];
    //                }
    //             });
    //
    //             const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:DataStreamContainer');
    //             return res.list;
    //         }));
    // }
    //
    // getOperators(dataSet: DataSetDescription): Observable<PipelineTemplateDescription[]> {
    //     return this.http
    //         .get(this.getServerUrl() + '/api/v2/users/'+ this.authStatusService.email + '/pipeline-templates?dataset=' + dataSet.id)
    //         .pipe(map(response => {
    //             const res = this.tsonLdSerializerService.fromJsonLd(response, 'sp:PipelineTemplateDescriptionContainer');
    //             return res.list;
    //         }));
    // }

    getPipelineTemplateInvocation(
        dataSetId: string,
        templateId: string,
    ): Observable<PipelineTemplateInvocation> {
        return this.http
            .get(
                `${this.getServerUrl()}/api/v2/pipeline-templates/invocation?streamId=${dataSetId}&templateId=${templateId}`,
            )
            .pipe(
                map(data => {
                    return PipelineTemplateInvocation.fromData(
                        data as PipelineTemplateInvocation,
                    );
                }),
            );

        // .pipe(map(response: PipelineTemplateInvocation => {

        // Currently tsonld dows not support objects that just contain one root object without an enclosing @graph array
        // const res = new PipelineTemplateInvocation(response['@id']);
        // res.dataSetId = response['sp:hasDataSetId'];
        // res.name = response['hasElementName'];
        // res.pipelineTemplateId = response['sp:hasInternalName'];

        // TODO find better solution
        // This will remove preconfigured values from the UI
        // res.list.forEach(property => {
        //   if (this.isFreeTextStaticProperty(property)) {
        //     if (this.asFreeTextStaticProperty(property).value !== undefined) {
        //       this.asFreeTextStaticProperty(property).render = false;
        //     }
        //   }
        // });
        // return res;
        // }));
    }

    isFreeTextStaticProperty(val) {
        return val instanceof FreeTextStaticProperty;
    }

    asFreeTextStaticProperty(val: StaticPropertyUnion): FreeTextStaticProperty {
        return val as FreeTextStaticProperty;
    }

    createPipelineTemplateInvocation(
        invocation: PipelineTemplateInvocation,
    ): Observable<PipelineOperationStatus> {
        return this.http
            .post(
                `${this.getServerUrl()}/api/v2/pipeline-templates`,
                invocation,
            )
            .pipe(
                map(result =>
                    PipelineOperationStatus.fromData(
                        result as PipelineOperationStatus,
                    ),
                ),
            );
    }
}
