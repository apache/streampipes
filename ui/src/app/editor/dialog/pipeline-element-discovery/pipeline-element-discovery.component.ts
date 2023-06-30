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
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../model/editor.model';

@Component({
    selector: 'sp-pipeline-element-discovery',
    templateUrl: './pipeline-element-discovery.component.html',
    styleUrls: ['./pipeline-element-discovery.component.scss'],
})
export class PipelineElementDiscoveryComponent implements OnInit {
    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    currentElements: PipelineElementUnion[];

    styles: any[] = [];

    constructor(
        private dialogRef: DialogRef<PipelineElementDiscoveryComponent>,
        private JsPlumbService: JsplumbService,
    ) {}

    ngOnInit() {
        this.currentElements.sort((a, b) => a.name.localeCompare(b.name));
        this.currentElements.forEach(pe => {
            this.styles.push(this.makeStandardStyle());
        });
    }

    create(selectedElement) {
        this.JsPlumbService.createElementWithoutConnection(
            this.rawPipelineModel,
            selectedElement,
            200,
            100,
        );
        this.hide();
    }

    hide() {
        this.dialogRef.close();
    }

    currentElementStyle(possibleElement: PipelineElementUnion) {
        if (possibleElement instanceof DataProcessorInvocation) {
            return 'sepa';
        } else if (possibleElement instanceof DataSinkInvocation) {
            return 'action';
        } else if (possibleElement instanceof SpDataStream) {
            return 'stream';
        }
    }

    makeStandardStyle() {
        return {
            background: 'white',
            cursor: 'auto',
        };
    }

    makeHoverStyle() {
        return {
            background: 'lightgrey',
            cursor: 'pointer',
        };
    }

    changeStyle(index: number, hover: boolean) {
        hover
            ? (this.styles[index] = this.makeHoverStyle())
            : (this.styles[index] = this.makeStandardStyle());
    }
}
