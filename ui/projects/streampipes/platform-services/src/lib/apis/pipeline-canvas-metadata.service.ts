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
import { PipelineCanvasMetadata } from '../model/gen/streampipes-model';
import { PlatformServicesCommons } from './commons.service';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class PipelineCanvasMetadataService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    addPipelineCanvasMetadata(pipelineCanvasMetadata: PipelineCanvasMetadata) {
        return this.http.post(
            this.pipelineCanvasMetadataBasePath,
            pipelineCanvasMetadata,
        );
    }

    getPipelineCanvasMetadata(
        pipelineId: string,
    ): Observable<PipelineCanvasMetadata> {
        return this.http
            .get(this.pipelineCanvasMetadataPipelinePath + pipelineId)
            .pipe(
                map(response =>
                    PipelineCanvasMetadata.fromData(response as any),
                ),
            );
    }

    updatePipelineCanvasMetadata(
        pipelineCanvasMetadata: PipelineCanvasMetadata,
    ) {
        return this.http.put(
            this.pipelineCanvasMetadataBasePath +
                '/' +
                pipelineCanvasMetadata.pipelineId,
            pipelineCanvasMetadata,
        );
    }

    deletePipelineCanvasMetadata(pipelineId: string) {
        return this.http.delete(
            this.pipelineCanvasMetadataPipelinePath + pipelineId,
        );
    }

    private get pipelineCanvasMetadataBasePath() {
        return (
            this.platformServicesCommons.apiBasePath +
            '/pipeline-canvas-metadata'
        );
    }

    private get pipelineCanvasMetadataPipelinePath() {
        return this.pipelineCanvasMetadataBasePath + '/pipeline/';
    }
}
