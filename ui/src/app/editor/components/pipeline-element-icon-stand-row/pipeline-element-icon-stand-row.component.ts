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

import { Component, Input, OnInit } from '@angular/core';
import {
    PipelineElementType,
    PipelineElementUnion,
} from '../../model/editor.model';
import { PipelineElementTypeUtils } from '../../utils/editor.utils';
import { EditorService } from '../../services/editor.service';

@Component({
    selector: 'sp-pe-icon-stand-row',
    templateUrl: './pipeline-element-icon-stand-row.component.html',
    styleUrls: ['./pipeline-element-icon-stand-row.component.scss'],
})
export class PipelineElementIconStandRowComponent implements OnInit {
    @Input()
    element: PipelineElementUnion;

    activeCssClass: string;
    cypressName: string;

    currentMouseOver = false;

    constructor(private editorService: EditorService) {}

    ngOnInit(): void {
        const activeType = PipelineElementTypeUtils.fromClassName(
            this.element['@class'],
        );
        this.activeCssClass = this.makeActiveCssClass(activeType);
        this.cypressName = this.element.name.toLowerCase().replace(' ', '_');
    }

    makeActiveCssClass(elementType: PipelineElementType): string {
        return PipelineElementTypeUtils.toCssShortHand(elementType);
    }

    updateMouseOver(e: string) {
        this.currentMouseOver = !this.currentMouseOver;
    }

    openHelpDialog(pipelineElement) {
        this.editorService.openHelpDialog(pipelineElement);
    }
}
