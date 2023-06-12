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

import { JsplumbBridge } from '../../services/jsplumb-bridge.service';
import { JsplumbService } from '../../services/jsplumb.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import { RestApi } from '../../../services/rest-api.service';
import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
} from '@angular/core';
import { PipelineElementRecommendationService } from '../../services/pipeline-element-recommendation.service';
import { ObjectProvider } from '../../services/object-provider.service';
import {
    PipelineElementConfig,
    PipelineElementConfigurationStatus,
    PipelineElementUnion,
} from '../../model/editor.model';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
    WildcardTopicDefinition,
} from '@streampipes/platform-services';
import { EditorService } from '../../services/editor.service';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { CompatibleElementsComponent } from '../../dialog/compatible-elements/compatible-elements.component';
import { cloneDeep } from 'lodash';
import { Subscription } from 'rxjs';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';

@Component({
    selector: 'sp-pipeline-element-options',
    templateUrl: './pipeline-element-options.component.html',
    styleUrls: ['./pipeline-element-options.component.css'],
})
export class PipelineElementOptionsComponent implements OnInit, OnDestroy {
    recommendationsAvailable: any = false;
    possibleElements: PipelineElementUnion[];
    recommendedElements: PipelineElementUnion[];
    recommendationsShown: any = false;
    pipelineElementCssType: string;
    isDataSource: boolean;

    @Input()
    currentMouseOverElement: string;

    @Input()
    pipelineElementId: string;

    @Input()
    pipelineValid: boolean;

    @Input()
    internalId: string;

    @Input()
    pipelineElement: PipelineElementConfig;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    allElements: PipelineElementUnion[];

    @Output()
    delete: EventEmitter<PipelineElementConfig> =
        new EventEmitter<PipelineElementConfig>();

    @Output()
    customize: EventEmitter<PipelineElementConfig> =
        new EventEmitter<PipelineElementConfig>();

    pipelineElementConfiguredObservable: Subscription;

    JsplumbBridge: JsplumbBridge;

    constructor(
        private objectProvider: ObjectProvider,
        private pipelineElementRecommendationService: PipelineElementRecommendationService,
        private dialogService: DialogService,
        private editorService: EditorService,
        private jsplumbFactoryService: JsplumbFactoryService,
        private jsplumbService: JsplumbService,
        private pipelineValidationService: PipelineValidationService,
        private restApi: RestApi,
    ) {
        this.recommendationsAvailable = false;
        this.possibleElements = [];
        this.recommendedElements = [];
        this.recommendationsShown = false;
        this.JsplumbBridge = this.jsplumbFactoryService.getJsplumbBridge(false);
    }

    ngOnInit() {
        this.pipelineElementConfiguredObservable =
            this.editorService.pipelineElementConfigured$.subscribe(
                pipelineElementDomId => {
                    this.pipelineElement.settings.openCustomize = false;
                    this.restApi.updateCachedPipeline(this.rawPipelineModel);
                    if (
                        pipelineElementDomId ===
                            this.pipelineElement.payload.dom &&
                        !(
                            this.pipelineElement.payload instanceof
                            DataSinkInvocation
                        )
                    ) {
                        this.initRecs(this.pipelineElement.payload.dom);
                    }
                },
            );
        this.pipelineElementCssType = this.pipelineElement.type;

        this.isDataSource =
            this.pipelineElement.type === 'stream' ||
            this.pipelineElement.type === 'set';

        if (
            this.isDataSource ||
            (this.pipelineElement.payload instanceof DataProcessorInvocation &&
                this.pipelineElement.settings.completed ===
                    PipelineElementConfigurationStatus.OK)
        ) {
            this.initRecs(this.pipelineElement.payload.dom);
        }
    }

    removeElement(pipelineElement: PipelineElementConfig) {
        this.delete.emit(pipelineElement);
    }

    customizeElement(pipelineElement: PipelineElementConfig) {
        this.customize.emit(pipelineElement);
    }

    openHelpDialog() {
        this.editorService.openHelpDialog(this.pipelineElement.payload);
    }

    openCustomizeStreamDialog() {
        // this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
    }

    initRecs(pipelineElementDomId) {
        const clonedModel: PipelineElementConfig[] = cloneDeep(
            this.rawPipelineModel,
        );
        const currentPipeline = this.objectProvider.makePipeline(clonedModel);
        this.editorService
            .recommendPipelineElement(currentPipeline, pipelineElementDomId)
            .subscribe(result => {
                if (result.success) {
                    this.possibleElements = cloneDeep(
                        this.pipelineElementRecommendationService.collectPossibleElements(
                            this.allElements,
                            result.possibleElements,
                        ),
                    );
                    this.recommendedElements = cloneDeep(
                        this.pipelineElementRecommendationService.populateRecommendedList(
                            this.allElements,
                            result.recommendedElements,
                        ),
                    );
                    this.recommendationsAvailable = true;
                }
            });
    }

    openPossibleElementsDialog() {
        const dialogRef = this.dialogService.open(CompatibleElementsComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Compatible Elements',
            data: {
                rawPipelineModel: this.rawPipelineModel,
                possibleElements: this.possibleElements,
                pipelineElementDomId: this.pipelineElement.payload.dom,
            },
        });

        dialogRef.afterClosed().subscribe(c => {});
    }

    showRecommendations(e) {
        this.recommendationsShown = !this.recommendationsShown;
        e.stopPropagation();
    }

    isRootElement() {
        return (
            this.JsplumbBridge.getConnections({
                source: document.getElementById(
                    this.pipelineElement.payload.dom,
                ),
            }).length === 0
        );
    }

    isWildcardTopic() {
        return (
            (this.pipelineElement.payload as SpDataStream).eventGrounding
                .transportProtocols[0].topicDefinition instanceof
            WildcardTopicDefinition
        );
    }

    ngOnDestroy(): void {
        this.pipelineElementConfiguredObservable.unsubscribe();
    }
}
