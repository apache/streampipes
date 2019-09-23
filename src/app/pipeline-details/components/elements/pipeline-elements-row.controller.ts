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

import * as angular from 'angular';

export class PipelineElementsRowController {

    ElementIconText: any;
    element: any;

    constructor(ElementIconText) {
        this.ElementIconText = ElementIconText;
    }

    elementTextIcon() {
        return this.ElementIconText.getElementIconText(this.element.name);
    }

    getElementType(pipeline, element) {
        var elementType = "action";

        angular.forEach(pipeline.streams, el => {
            if (element.DOM == el.DOM) {
                elementType = "stream";
            }
        });

        angular.forEach(pipeline.sepas, el => {
            if (element.DOM == el.DOM) {
                elementType = "sepa";
            }
        });

        return elementType;
    }
}

PipelineElementsRowController.$inject = ['ElementIconText'];
