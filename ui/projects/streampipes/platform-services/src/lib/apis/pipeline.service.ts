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
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';
import {
    Message,
    Pipeline,
    PipelineCategory,
    PipelineOperationStatus,
    PipelineStatusMessage,
} from '../model/gen/streampipes-model';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class PipelineService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    getPipelineCategories(): Observable<PipelineCategory[]> {
        return this.http.get(`${this.apiBasePath}/pipelinecategories`).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    PipelineCategory.fromData(p),
                );
            }),
        );
    }

    storePipelineCategory(pipelineCategory: PipelineCategory) {
        return this.http.post(
            `${this.apiBasePath}/pipelinecategories`,
            pipelineCategory,
        );
    }

    deletePipelineCategory(categoryId: string) {
        return this.http.delete(
            `${this.apiBasePath}/pipelinecategories/${categoryId}`,
        );
    }

    startPipeline(pipelineId): Observable<PipelineOperationStatus> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}/start`)
            .pipe(
                map(result =>
                    PipelineOperationStatus.fromData(
                        result as PipelineOperationStatus,
                    ),
                ),
            );
    }

    stopPipeline(
        pipelineId: string,
        forceStop?: boolean,
    ): Observable<PipelineOperationStatus> {
        const queryAppendix = forceStop ? '?forceStop=' + forceStop : '';
        return this.http
            .get(
                `${this.apiBasePath}/pipelines/${pipelineId}/stop${queryAppendix}`,
            )
            .pipe(
                map(result =>
                    PipelineOperationStatus.fromData(
                        result as PipelineOperationStatus,
                    ),
                ),
            );
    }

    getPipelineById(pipelineId: string): Observable<Pipeline> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}`)
            .pipe(map(response => Pipeline.fromData(response as Pipeline)));
    }

    storePipeline(pipeline: Pipeline): Observable<Message> {
        return this.http.post(`${this.apiBasePath}/pipelines`, pipeline).pipe(
            map(response => {
                return Message.fromData(response as Message);
            }),
        );
    }

    updatePipeline(pipeline: Pipeline): Observable<Message> {
        const pipelineId = pipeline._id;
        return this.http
            .put(`${this.apiBasePath}/pipelines/${pipelineId}`, pipeline)
            .pipe(
                map(response => {
                    return Message.fromData(response as Message);
                }),
            );
    }

    getOwnPipelines(): Observable<Pipeline[]> {
        return this.http.get(`${this.apiBasePath}/pipelines/own`).pipe(
            map(response => {
                return (response as any[]).map(p => Pipeline.fromData(p));
            }),
        );
    }

    deleteOwnPipeline(pipelineId): Observable<any> {
        return this.http.delete(`${this.apiBasePath}/pipelines/${pipelineId}`);
    }

    getSystemPipelines(): Observable<Pipeline[]> {
        return this.http.get(`${this.apiBasePath}/pipelines/system`).pipe(
            map(response => {
                return (response as any[]).map(p => Pipeline.fromData(p));
            }),
        );
    }

    getPipelineStatusById(pipelineId): Observable<PipelineStatusMessage[]> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}/status`)
            .pipe(
                map(response => {
                    return (response as any[]).map(r =>
                        PipelineStatusMessage.fromData(r),
                    );
                }),
            );
    }

    getPipelinesContainingElementId(elementId: string): Observable<Pipeline[]> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/contains/${elementId}`)
            .pipe(
                map(response => {
                    return (response as any[]).map(p => Pipeline.fromData(p));
                }),
            );
    }

    get apiBasePath() {
        return this.platformServicesCommons.apiBasePath;
    }
}
