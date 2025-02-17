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
import { Pipeline, SpLogEntry } from '@streampipes/platform-services';
import { PipelineElementUnion } from '../../../editor/model/editor.model';

@Component({
    selector: 'sp-pipeline-details-expansion-panel',
    templateUrl: './pipeline-details-expansion-panel.component.html',
    styleUrls: ['./pipeline-details-expansion-panel.component.scss'],
})
export class PipelineDetailsExpansionPanelComponent implements OnInit {
    expanded = true;

    @Input()
    pipeline: Pipeline;

    @Input()
    logInfo: Record<string, SpLogEntry[]>;

    @Input()
    hasWritePipelinePrivileges: boolean;

    @Output()
    reloadPipelineEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    allPipelineElements: PipelineElementUnion[] = [];

    ngOnInit(): void {
        this.allPipelineElements = [
            ...this.pipeline.streams,
            ...this.pipeline.sepas,
            ...this.pipeline.actions,
        ];
    }
}
