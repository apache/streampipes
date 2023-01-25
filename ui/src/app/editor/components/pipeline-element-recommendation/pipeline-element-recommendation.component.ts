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

import { JsplumbService } from '../../services/jsplumb.service';
import { Component, Input } from '@angular/core';
import {
    InvocablePipelineElementUnion,
    PipelineElementConfig,
} from '../../model/editor.model';
import { DataProcessorInvocation } from '@streampipes/platform-services';
import { SafeCss } from '../../utils/style-sanitizer';

@Component({
    selector: 'sp-pipeline-element-recommendation',
    templateUrl: './pipeline-element-recommendation.component.html',
    styleUrls: ['./pipeline-element-recommendation.component.scss'],
})
export class PipelineElementRecommendationComponent {
    @Input()
    recommendationsShown: boolean;

    @Input()
    rawPipelineModel: PipelineElementConfig[];

    @Input()
    pipelineElementDomId: string;

    _recommendedElements: any;

    recommendationsPrepared = false;

    constructor(
        private jsplumbService: JsplumbService,
        public safeCss: SafeCss,
    ) {}

    prepareStyles(recommendedElements) {
        recommendedElements.forEach((element, index) => {
            this.setLayoutSettings(element, index, recommendedElements);
        });
    }

    setLayoutSettings(element, index, recommendedElements) {
        element.layoutSettings = {
            skewStyle: element.name
                ? this.getSkewStyle(index, recommendedElements)
                : { opacity: 0 },
            unskewStyle: this.getUnskewStyle(
                element,
                index,
                recommendedElements,
            ),
            unskewStyleLabel: this.getUnskewStyleLabel(
                index,
                recommendedElements,
            ),
            type:
                element instanceof DataProcessorInvocation ? 'sepa' : 'action',
        };
    }

    create(recommendedElement: InvocablePipelineElementUnion) {
        this.recommendationsShown = false;
        this.jsplumbService.createElement(
            this.rawPipelineModel,
            recommendedElement,
            this.pipelineElementDomId,
        );
    }

    getUnskewStyle(recommendedElement, index, recommendedElements) {
        const unskew = -this.getSkew(recommendedElements);
        const rotate = -(90 - this.getSkew(recommendedElements) / 2);

        return (
            'transform: skew(' +
            unskew +
            'deg)' +
            ' rotate(' +
            rotate +
            'deg)' +
            ' scale(1);' +
            'background-color: ' +
            this.getBackgroundColor(recommendedElement, index)
        );
    }

    getBackgroundColor(recommendedElement, index) {
        let alpha =
            recommendedElement.weight < 0.2
                ? 0.2
                : recommendedElement.weight - 0.2;
        alpha = Math.round(alpha * 10) / 10;
        const rgb =
            recommendedElement instanceof DataProcessorInvocation
                ? this.getSepaColor(index)
                : this.getActionColor(index);
        return 'rgba(' + rgb + ',' + alpha + ')';
    }

    getSepaColor(index) {
        return index % 2 === 0 ? '0, 150, 136' : '0, 164, 150';
    }

    getActionColor(index) {
        return index % 2 === 0 ? '63, 81, 181' : '79, 101, 230';
    }

    getSkewStyle(index, recommendedElements) {
        // transform: rotate(72deg) skew(18deg);
        const skew = this.getSkew(recommendedElements);
        const rotate = (index + 1) * this.getAngle(recommendedElements);

        return 'transform: rotate(' + rotate + 'deg) skew(' + skew + 'deg);';
    }

    getUnskewStyleLabel(index, recommendedElements) {
        const unskew = -this.getSkew(recommendedElements);
        const rotate = (index + 1) * this.getAngle(recommendedElements);
        const unrotate = -360 + rotate * -1;

        return (
            'transform: skew(' +
            unskew +
            'deg)' +
            ' rotate(' +
            unrotate +
            'deg)' +
            ' scale(1);' +
            'z-index: -1;' +
            'margin-left: 50%;' +
            'margin-top: 50%;' +
            'position: absolute;' +
            'background: white;' +
            'height: 50px;' +
            'width: 50px;' +
            'font-size: 16px;' +
            'text-align: center;' +
            'line-height: 50px;' +
            'top: 0px;'
        );
    }

    getSkew(recommendedElements) {
        return 90 - this.getAngle(recommendedElements);
    }

    getAngle(recommendedElements) {
        return 360 / recommendedElements.length;
    }

    fillRemainingItems(recommendedElements) {
        if (recommendedElements.length < 6) {
            for (let i = recommendedElements.length; i < 6; i++) {
                const element = { fakeElement: true, weight: 0 };
                recommendedElements.push(element);
            }
        }
    }

    get recommendedElements() {
        return this._recommendedElements;
    }

    @Input()
    set recommendedElements(recommendedElements: any) {
        this.fillRemainingItems(recommendedElements);
        this.prepareStyles(recommendedElements);
        this._recommendedElements = recommendedElements;
        this.recommendationsPrepared = true;
    }
}
