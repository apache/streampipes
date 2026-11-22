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

import {
    Component,
    Input,
    OnInit,
    ChangeDetectionStrategy,
} from '@angular/core';
import { PipelineElementUnion } from '../../../../../editor/model/editor.model';
import { PipelineElementTypeUtils } from '../../../../../editor/utils/editor.utils';
import { NgClass } from '@angular/common';
import { PipelineElementComponent } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-pipeline-elements-row',
    host: { class: 'flex-row' },
    templateUrl: './pipeline-elements-row.component.html',
    styleUrls: [
        '../../../../../editor/components/pipeline-element-icon-stand/pipeline-element-icon-stand-row/pipeline-element-icon-stand-row.component.scss',
    ],
    styles: [
        '.element-icon { flex: 1 1 50px; box-sizing: border-box; min-width: 50px; max-width: 50px; }',
    ],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [NgClass, PipelineElementComponent],
})
export class PipelineElementsRowComponent implements OnInit {
    elementType: string;

    @Input()
    pipelineElement: PipelineElementUnion;

    constructor() {}

    ngOnInit() {
        this.updateType();
    }

    updateType() {
        this.elementType = PipelineElementTypeUtils.toCssShortHand(
            PipelineElementTypeUtils.fromType(this.pipelineElement),
        );
    }
}
