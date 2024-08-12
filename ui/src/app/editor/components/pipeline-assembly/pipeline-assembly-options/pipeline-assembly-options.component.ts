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
    EventEmitter,
    Input,
    Output,
    ViewChild,
} from '@angular/core';
import { JsplumbBridge } from '../../../services/jsplumb-bridge.service';
import { PipelinePositioningService } from '../../../services/pipeline-positioning.service';
import { PipelineValidationService } from '../../../services/pipeline-validation.service';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
} from '@streampipes/shared-ui';
import { EditorService } from '../../../services/editor.service';
import { MatDialog } from '@angular/material/dialog';
import { PipelineElementDiscoveryComponent } from '../../../dialog/pipeline-element-discovery/pipeline-element-discovery.component';
import {
    PipelineElementConfig,
    PipelineElementUnion,
} from '../../../model/editor.model';
import { PipelineAssemblyOptionsPipelineCacheComponent } from './pipeline-assembly-options-pipeline-cache/pipeline-assembly-options-pipeline-cache.component';
import { PipelineCanvasMetadata } from '@streampipes/platform-services';

@Component({
    selector: 'sp-pipeline-assembly-options',
    templateUrl: './pipeline-assembly-options.component.html',
    styleUrls: ['./pipeline-assembly-options.component.scss'],
})
export class PipelineAssemblyOptionsComponent {
    @Input()
    jsplumbBridge: JsplumbBridge;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineCanvasMetadata: PipelineCanvasMetadata;

    @Input()
    allElements: PipelineElementUnion[];

    @Input()
    previewModeActive: boolean;

    @Output()
    savePipelineEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    clearAssemblyEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    togglePreviewEmitter: EventEmitter<void> = new EventEmitter<void>();

    @ViewChild('assemblyOptionsPipelineCacheComponent')
    assemblyOptionsCacheComponent: PipelineAssemblyOptionsPipelineCacheComponent;

    constructor(
        public editorService: EditorService,
        public pipelineValidationService: PipelineValidationService,
        private pipelinePositioningService: PipelinePositioningService,
        private dialog: MatDialog,
        private dialogService: DialogService,
    ) {}

    autoLayout() {
        this.pipelinePositioningService.layoutGraph(
            '#assembly',
            "div[id^='jsplumb']",
            110,
            false,
        );
        this.jsplumbBridge.repaintEverything();
    }

    openDiscoverDialog() {
        this.dialogService.open(PipelineElementDiscoveryComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Discover pipeline elements',
            width: '50vw',
            data: {
                currentElements: this.allElements,
                rawPipelineModel: this.rawPipelineModel,
            },
        });
    }

    showClearAssemblyConfirmDialog(event: any) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: 'Do you really want to delete the current pipeline?',
                subtitle: 'This cannot be undone.',
                cancelTitle: 'No',
                okTitle: 'Yes',
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(ev => {
            if (ev) {
                this.clearAssemblyEmitter.emit();
            }
        });
    }

    isPipelineAssemblyEmpty() {
        return (
            this.rawPipelineModel.length === 0 ||
            this.rawPipelineModel.every(pe => pe.settings.disabled)
        );
    }

    triggerCacheUpdate(): void {
        this.assemblyOptionsCacheComponent.triggerPipelineCacheUpdate();
    }
}
