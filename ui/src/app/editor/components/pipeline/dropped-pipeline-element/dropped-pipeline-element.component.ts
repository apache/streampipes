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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../model/editor.model';
import {
    PipelinePreviewModel,
    SpMetricsEntry,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-dropped-pipeline-element',
    templateUrl: './dropped-pipeline-element.component.html',
})
export class DroppedPipelineElementComponent implements OnInit {
    @Input()
    pipelineElementConfig: PipelineElementConfig;

    @Input()
    readonly: boolean;

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    metricsInfo: Record<string, SpMetricsEntry>;

    @Input()
    previewModeActive: boolean;

    @Input()
    pipelinePreview: PipelinePreviewModel;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    currentMouseOverElement = '';

    @Input()
    pipelineValid: boolean;

    @Output()
    deleteEmitter: EventEmitter<PipelineElementConfig> = new EventEmitter();

    @Output()
    showCustomizeEmitter: EventEmitter<PipelineElementConfig> =
        new EventEmitter();

    elementCssClasses: string;

    ngOnInit() {
        this.applyCssClasses();
    }

    applyCssClasses(): void {
        this.elementCssClasses = `${this.pipelineElementConfig.type} 
    ${this.pipelineElementConfig.settings.connectable} 
    ${this.pipelineElementConfig.settings.displaySettings}`;
    }
}
