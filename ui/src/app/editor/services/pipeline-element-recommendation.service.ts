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

import { Injectable } from '@angular/core';
import { PipelineElementUnion } from '../model/editor.model';
import {
    InvocableStreamPipesEntity,
    PipelineElementRecommendation,
    SpDataStream,
} from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class PipelineElementRecommendationService {
    constructor() {}

    collectPossibleElements(
        allElements: PipelineElementUnion[],
        possibleElements: PipelineElementRecommendation[],
    ) {
        const possibleElementConfigs = [];
        possibleElements.forEach(pe => {
            possibleElementConfigs.push(
                this.getPipelineElementContents(allElements, pe.elementId)[0],
            );
        });
        return possibleElementConfigs;
    }

    populateRecommendedList(allElements, recs) {
        const elementRecommendations: any = [];
        recs.sort((a, b) => {
            return a.count > b.count ? -1 : b.count > a.count ? 1 : 0;
        });
        const maxRecs = recs.length > 7 ? 7 : recs.length;
        for (let i = 0; i < maxRecs; i++) {
            const el = recs[i];
            const elements = this.getPipelineElementContents(
                allElements,
                el.elementId,
            );
            const element = elements[0];
            (element as any).weight = el.weight;
            elementRecommendations.push(element);
        }
        return elementRecommendations;
    }

    getPipelineElementContents(
        allElements: PipelineElementUnion[],
        belongsTo: string,
    ) {
        return allElements.filter(
            pe =>
                (pe instanceof SpDataStream && pe.elementId === belongsTo) ||
                (pe instanceof InvocableStreamPipesEntity &&
                    pe.belongsTo === belongsTo),
        );
    }
}
