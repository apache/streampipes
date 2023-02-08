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
import { DialogRef } from '@streampipes/shared-ui';
import { JsplumbService } from '../../services/jsplumb.service';
import { DataProcessorInvocation } from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../model/editor.model';

@Component({
    selector: 'sp-compatible-elements',
    templateUrl: './compatible-elements.component.html',
    styleUrls: ['./compatible-elements.component.scss'],
})
export class CompatibleElementsComponent implements OnInit {
    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineElementDomId: any;

    @Input()
    possibleElements: PipelineElementUnion[];

    styles: any[] = [];

    constructor(
        private dialogRef: DialogRef<CompatibleElementsComponent>,
        private JsPlumbService: JsplumbService,
    ) {
        // this.ElementIconText = ElementIconText;
    }

    ngOnInit() {
        this.possibleElements.sort((a, b) => a.name.localeCompare(b.name));
        this.possibleElements.forEach(pe => {
            this.styles.push(this.makeStandardStyle());
        });
    }

    create(possibleElement) {
        this.JsPlumbService.createElement(
            this.rawPipelineModel,
            possibleElement,
            this.pipelineElementDomId,
        );
        this.hide();
    }

    iconText(elementId) {
        // return this.ElementIconText.getElementIconText(elementId);
    }

    hide() {
        // this.$mdDialog.hide();
        this.dialogRef.close();
    }

    isDataProcessor(possibleElement: PipelineElementUnion) {
        return possibleElement instanceof DataProcessorInvocation;
    }

    makeStandardStyle() {
        return {
            background: 'var(--color-bg-dialog)',
            cursor: 'auto',
        };
    }

    makeHoverStyle() {
        return {
            background: 'var(--color-bg-1)',
            cursor: 'pointer',
        };
    }

    changeStyle(index: number, hover: boolean) {
        hover
            ? (this.styles[index] = this.makeHoverStyle())
            : (this.styles[index] = this.makeStandardStyle());
    }
}
