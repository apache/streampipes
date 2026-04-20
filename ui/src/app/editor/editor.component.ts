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

import { Component, OnInit, inject } from '@angular/core';
import {
    Pipeline,
    PipelineCanvasMetadata,
    PipelineCanvasMetadataService,
    PipelineElementService,
    PipelineService,
} from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from './model/editor.model';
import {
    SpBasicViewComponent,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of, zip } from 'rxjs';
import { SpPipelineRoutes } from '../pipelines/pipelines.breadcrumb';
import { catchError, map } from 'rxjs/operators';
import { EditorService } from './services/editor.service';
import { JsplumbService } from './services/jsplumb.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { PipelineElementIconStandComponent } from './components/pipeline-element-icon-stand/pipeline-element-icon-stand.component';
import { PipelineAssemblyComponent } from './components/pipeline-assembly/pipeline-assembly.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatProgressSpinner,
        SpBasicViewComponent,
        PipelineElementIconStandComponent,
        PipelineAssemblyComponent,
        TranslatePipe,
    ],
})
export class EditorComponent implements OnInit {
    private pipelineElementService = inject(PipelineElementService);
    private activatedRoute = inject(ActivatedRoute);
    private breadcrumbService = inject(SpBreadcrumbService);
    private editorService = inject(EditorService);
    private pipelineService = inject(PipelineService);
    private jsplumbService = inject(JsplumbService);
    private pipelineCanvasMetadataService = inject(
        PipelineCanvasMetadataService,
    );

    allElements: PipelineElementUnion[] = [];

    rawPipelineModel: PipelineElementConfig[] = [];
    originalPipeline: Pipeline;

    allElementsLoaded = false;
    allMetadataLoaded = false;
    pipelineCanvasMetadata: PipelineCanvasMetadata;
    pipelineCanvasMetadataAvailable: boolean;

    ngOnInit() {
        const pipelineId = this.activatedRoute.snapshot.params.pipelineId;
        if (pipelineId) {
            this.loadPipelineToModify(pipelineId);
        } else {
            this.loadCachedPipeline();
            this.breadcrumbService.updateBreadcrumb([
                SpPipelineRoutes.BASE,
                { label: 'New Pipeline' },
            ]);
        }
        zip(
            this.pipelineElementService.getDataStreams(),
            this.pipelineElementService.getDataProcessors(),
            this.pipelineElementService.getDataSinks(),
        ).subscribe(response => {
            this.allElements = this.allElements
                .concat(response[0])
                .concat(response[1])
                .concat(response[2])
                .sort((a, b) => {
                    return a.name.localeCompare(b.name);
                });
            this.allElementsLoaded = true;
        });
    }

    loadCachedPipeline() {
        const cachedPipeline = this.editorService.getCachedPipeline();
        const cachedCanvasMetadata =
            this.editorService.getCachedPipelineCanvasMetadata();
        forkJoin([cachedPipeline, cachedCanvasMetadata]).subscribe(results => {
            if (results[0] && results[0].length > 0) {
                this.rawPipelineModel = results[0] as PipelineElementConfig[];
                this.handleCanvasMetadataResponse(results[1]);
            } else {
                this.pipelineCanvasMetadata = new PipelineCanvasMetadata();
                this.pipelineCanvasMetadataAvailable = false;
            }
            this.allMetadataLoaded = true;
        });
    }

    loadPipelineToModify(pipelineId: string) {
        const pipelineReq = this.pipelineService.getPipelineById(pipelineId);

        const canvasMetadataReq = this.pipelineCanvasMetadataService
            .getPipelineCanvasMetadata(pipelineId)
            .pipe(
                map(response => {
                    if (response === null) {
                        this.handleCanvasMetadataResponse(undefined);
                        return undefined;
                    }
                    return response;
                }),
                catchError(() => {
                    this.handleCanvasMetadataResponse(undefined);
                    return of(undefined);
                }),
            );

        forkJoin([pipelineReq, canvasMetadataReq]).subscribe(
            ([pipelineResp, canvasResp]) => {
                if (pipelineResp) {
                    this.originalPipeline = pipelineResp;
                    this.breadcrumbService.updateBreadcrumb([
                        SpPipelineRoutes.BASE,
                        { label: this.originalPipeline.name },
                        { label: 'Modify' },
                    ]);
                    this.rawPipelineModel = this.jsplumbService.makeRawPipeline(
                        this.originalPipeline,
                        false,
                    );
                }
                this.handleCanvasMetadataResponse(canvasResp);
                this.allMetadataLoaded = true;
            },
        );
    }

    handleCanvasMetadataResponse(canvasMetadata: PipelineCanvasMetadata) {
        if (canvasMetadata !== undefined) {
            this.pipelineCanvasMetadata = canvasMetadata;
            this.pipelineCanvasMetadataAvailable = true;
        } else {
            this.pipelineCanvasMetadataAvailable = false;
            this.pipelineCanvasMetadata = new PipelineCanvasMetadata();
        }
    }
}
