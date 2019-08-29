/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

export class PossibleElementsController {

    $mdDialog: any;
    ElementIconText: any;
    rawPipelineModel: any;
    pipelineElementDomId: any;
    possibleElements: any;
    JsplumbService: any;

    constructor($mdDialog, ElementIconText, JsplumbService, rawPipelineModel, possibleElements, pipelineElementDomId) {
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
        this.rawPipelineModel = rawPipelineModel;
        this.pipelineElementDomId = pipelineElementDomId;
        this.possibleElements = possibleElements;
        this.JsplumbService = JsplumbService;
    }

    create(possibleElement) {
        this.JsplumbService.createElement(this.rawPipelineModel, possibleElement, this.pipelineElementDomId);
        this.hide();
    }

    iconText(elementId) {
        return this.ElementIconText.getElementIconText(elementId);
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

PossibleElementsController.$inject = ['$mdDialog', 'ElementIconText', 'JsplumbService', 'rawPipelineModel', 'possibleElements', 'pipelineElementDomId'];