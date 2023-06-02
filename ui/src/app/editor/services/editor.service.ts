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
import { HttpClient, HttpContext } from '@angular/common/http';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    Pipeline,
    PipelineCanvasMetadata,
    PipelineElementRecommendationMessage,
    PipelineModificationMessage,
    PipelinePreviewModel,
    PlatformServicesCommons,
    SpDataStream,
} from '@streampipes/platform-services';
import { Observable, Subject } from 'rxjs';
import {
    PeCategory,
    PipelineElementConfig,
    PipelineElementUnion,
} from '../model/editor.model';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { HelpComponent } from '../dialog/help/help.component';
import { map } from 'rxjs/operators';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';

@Injectable({ providedIn: 'root' })
export class EditorService {
    private pipelineElementConfigured = new Subject<string>();

    public pipelineElementConfigured$ =
        this.pipelineElementConfigured.asObservable();

    pipelineAssemblyEmpty = true;

    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
        private dialogService: DialogService,
    ) {}

    get apiBasePath() {
        return this.platformServicesCommons.apiBasePath;
    }

    recommendPipelineElement(
        pipeline: Pipeline,
        currentDomId: string,
    ): Observable<PipelineElementRecommendationMessage> {
        return this.http
            .post(
                this.pipelinesResourceUrl + '/recommend/' + currentDomId,
                pipeline,
            )
            .pipe(
                map(data =>
                    PipelineElementRecommendationMessage.fromData(data as any),
                ),
            );
    }

    updatePartialPipeline(pipeline): Observable<PipelineModificationMessage> {
        return this.http
            .post(this.pipelinesResourceUrl + '/update', pipeline)
            .pipe(
                map(data => {
                    return PipelineModificationMessage.fromData(data as any);
                }),
            );
    }

    getCachedPipeline(): Observable<PipelineElementConfig[]> {
        return this.http.get(this.apiBasePath + '/pipeline-cache').pipe(
            map(result => {
                if (result === null) {
                    return [];
                } else {
                    const configs: PipelineElementConfig[] =
                        result as PipelineElementConfig[];
                    configs.map(
                        config =>
                            (config.payload = this.convert(config.payload)),
                    );
                    return configs;
                }
            }),
        );
    }

    getCachedPipelineCanvasMetadata(): Observable<PipelineCanvasMetadata> {
        return this.http.get(this.apiBasePath + '/pipeline-canvas-cache').pipe(
            map(response => {
                return PipelineCanvasMetadata.fromData(response as any);
            }),
        );
    }

    convert(payload: any) {
        if (payload['@class'] === 'org.apache.streampipes.model.SpDataStream') {
            return SpDataStream.fromData(payload as SpDataStream);
        } else if (
            payload['@class'] ===
            'org.apache.streampipes.model.graph.DataProcessorInvocation'
        ) {
            return DataProcessorInvocation.fromData(
                payload as DataProcessorInvocation,
            );
        } else {
            return DataSinkInvocation.fromData(payload as DataSinkInvocation);
        }
    }

    getEpCategories(): Observable<PeCategory[]> {
        return this.http
            .get(this.platformServicesCommons.apiBasePath + '/categories/ep')
            .pipe(map(response => response as PeCategory[]));
    }

    getEpaCategories(): Observable<PeCategory[]> {
        return this.http
            .get(this.platformServicesCommons.apiBasePath + '/categories/epa')
            .pipe(map(response => response as PeCategory[]));
    }

    getEcCategories(): Observable<PeCategory[]> {
        return this.http
            .get(this.platformServicesCommons.apiBasePath + '/categories/ec')
            .pipe(map(response => response as PeCategory[]));
    }

    updateCachedPipeline(rawPipelineModel: any) {
        return this.http.post(
            this.apiBasePath + '/pipeline-cache',
            rawPipelineModel,
        );
    }

    updateCachedCanvasMetadata(pipelineCanvasMetadata: PipelineCanvasMetadata) {
        return this.http.post(
            this.platformServicesCommons.apiBasePath + '/pipeline-canvas-cache',
            pipelineCanvasMetadata,
        );
    }

    removePipelineFromCache() {
        return this.http.delete(this.apiBasePath + '/pipeline-cache');
    }

    removeCanvasMetadataFromCache() {
        return this.http.delete(this.apiBasePath + '/pipeline-canvas-cache');
    }

    private get pipelinesResourceUrl() {
        return this.platformServicesCommons.apiBasePath + '/pipelines';
    }

    announceConfiguredElement(pipelineElementDomId: string) {
        this.pipelineElementConfigured.next(pipelineElementDomId);
    }

    makePipelineAssemblyEmpty(status) {
        this.pipelineAssemblyEmpty = status;
    }

    openHelpDialog(pipelineElement: PipelineElementUnion) {
        this.dialogService.open(HelpComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: pipelineElement.name,
            width: '70vw',
            data: {
                pipelineElement: pipelineElement,
            },
        });
    }

    initiatePipelinePreview(
        pipeline: Pipeline,
    ): Observable<PipelinePreviewModel> {
        return this.http
            .post(this.pipelinePreviewBasePath, pipeline)
            .pipe(
                map(response => PipelinePreviewModel.fromData(response as any)),
            );
    }

    deletePipelinePreviewRequest(previewId: string): Observable<any> {
        return this.http.delete(this.pipelinePreviewBasePath + '/' + previewId);
    }

    getPipelinePreviewResult(
        previewId: string,
        pipelineElementDomId: string,
    ): Observable<any> {
        return this.http.get(
            this.pipelinePreviewBasePath +
                '/' +
                previewId +
                '/' +
                pipelineElementDomId,
            { context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true) },
        );
    }

    get pipelinePreviewBasePath() {
        return this.apiBasePath + '/pipeline-element-preview';
    }
}
