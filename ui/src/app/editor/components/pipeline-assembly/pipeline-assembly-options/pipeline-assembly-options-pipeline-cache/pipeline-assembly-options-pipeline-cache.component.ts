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

import { Component, Input } from '@angular/core';
import { PipelineElementConfig } from '../../../../model/editor.model';
import { forkJoin } from 'rxjs';
import { PipelinePositioningService } from '../../../../services/pipeline-positioning.service';
import { EditorService } from '../../../../services/editor.service';
import { PipelineCanvasMetadata } from '@streampipes/platform-services';

@Component({
    selector: 'sp-pipeline-assembly-options-pipeline-cache',
    templateUrl: './pipeline-assembly-options-pipeline-cache.component.html',
    styleUrls: ['./pipeline-assembly-options-pipeline-cache.component.scss'],
})
export class PipelineAssemblyOptionsPipelineCacheComponent {
    pipelineCached = false;
    pipelineCacheRunning = false;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    constructor(
        private pipelinePositioningService: PipelinePositioningService,
        private editorService: EditorService,
    ) {}

    triggerPipelineCacheUpdate() {
        setTimeout(() => {
            this.pipelineCacheRunning = true;
            this.pipelineCached = false;
            this.pipelineCanvasMetadata =
                this.pipelinePositioningService.collectPipelineElementPositions(
                    this.pipelineCanvasMetadata,
                    this.rawPipelineModel,
                );
            forkJoin([
                this.editorService.updateCachedPipeline(this.rawPipelineModel),
                this.editorService.updateCachedCanvasMetadata(
                    this.pipelineCanvasMetadata,
                ),
            ]).subscribe(() => {
                this.pipelineCacheRunning = false;
                this.pipelineCached = true;
            });
        });
    }
}
