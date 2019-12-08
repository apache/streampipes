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

import * as angular from 'angular';
import {JsplumbBridge} from "../../../services/jsplumb-bridge.service";
import {JsplumbService} from "../../../services/jsplumb.service";
import {PipelineValidationService} from "../../services/pipeline-validation.service";
import {TransitionService} from "../../../services/transition.service";

export class PipelineElementOptionsController {

    ObjectProvider: any;
    PipelineElementRecommendationService: any;
    PipelineValidationService: PipelineValidationService;
    InitTooltips: any;
    JsplumbBridge: JsplumbBridge;
    EditorDialogManager: any;
    JsplumbService: JsplumbService;
    recommendationsAvailable: any;
    possibleElements: any;
    recommendedElements: any;
    recommendationsShown: any;
    pipelineElement: any;
    rawPipelineModel: any;
    allElements: any;
    deleteFunction: any;
    TransitionService: TransitionService;
    $rootScope: any;
    $timeout: any;

    pipelineValid: boolean;

    constructor($rootScope, ObjectProvider, PipelineElementRecommendationService, InitTooltips, JsplumbBridge,
                EditorDialogManager, JsplumbService, TransitionService, PipelineValidationService, $timeout) {
        this.$rootScope = $rootScope;
        this.ObjectProvider = ObjectProvider;
        this.PipelineElementRecommendationService = PipelineElementRecommendationService;
        this.InitTooltips = InitTooltips;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.JsplumbService = JsplumbService;
        this.TransitionService = TransitionService;
        this.PipelineValidationService = PipelineValidationService;

        this.recommendationsAvailable = false;
        this.possibleElements = [];
        this.recommendedElements = [];
        this.recommendationsShown = false;

        this.$timeout = $timeout;
    }

    $onInit() {
        this.$rootScope.$on("SepaElementConfigured", (event, item) => {
            if (item === this.pipelineElement.payload.DOM) {
                this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
            }
        });

        if (this.pipelineElement.type === 'stream') {
            this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
        }
    }

    removeElement(pipelineElement) {
        this.deleteFunction(pipelineElement);
        this.$timeout(() => {
            this.pipelineValid = this.PipelineValidationService.isValidPipeline(this.rawPipelineModel);
        }, 200);

    }

    openCustomizeDialog() {
        this.EditorDialogManager.showCustomizeDialog($("#" + this.pipelineElement.payload.DOM), "", this.pipelineElement.payload)
            .then(() => {
                this.JsplumbService.activateEndpoint(this.pipelineElement.payload.DOM, !this.pipelineElement.payload.uncompleted);
            }, () => {
                this.JsplumbService.activateEndpoint(this.pipelineElement.payload.DOM, !this.pipelineElement.payload.uncompleted);
            });
    }

    openHelpDialog() {
        this.EditorDialogManager.openHelpDialog(this.pipelineElement.payload);
    }

    openCustomizeStreamDialog() {
        this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
    }

    initRecs(elementId, currentPipelineElements) {
        var currentPipeline = this.ObjectProvider.makePipeline(angular.copy(currentPipelineElements));
        this.PipelineElementRecommendationService.getRecommendations(this.allElements, currentPipeline).then((result) => {
            if (result.success) {
                this.possibleElements = result.possibleElements;
                this.recommendedElements = result.recommendations;
                this.recommendationsAvailable = true;
                this.InitTooltips.initTooltips();
            }
        });
    }

    showRecommendations(e) {
        this.recommendationsShown = !this.recommendationsShown;
        e.stopPropagation();
    }

    isRootElement() {
        return this.JsplumbBridge.getConnections({source: this.pipelineElement.payload.DOM}).length === 0;
    }

    isConfigured() {
        if (this.pipelineElement.type == 'stream') return true;
        else {
            return this.pipelineElement.payload.configured;
        }
    }

    isWildcardTopic() {
        return this.pipelineElement
            .payload
            .eventGrounding
            .transportProtocols[0]
            .properties
            .topicDefinition
            .type === "org.streampipes.model.grounding.WildcardTopicDefinition";

    }
}

PipelineElementOptionsController.$inject = ['$rootScope', 'ObjectProvider', 'PipelineElementRecommendationService',
    'InitTooltips', 'JsplumbBridge', 'EditorDialogManager', 'JsplumbService',
    'TransitionService', 'PipelineValidationService', '$timeout'];