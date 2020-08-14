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

import {Injectable} from "@angular/core";
import {EditorService} from "./editor.service";
import {PipelineElementUnion} from "../model/editor.model";
import {
    InvocableStreamPipesEntity,
    PipelineElementRecommendation,
    SpDataStream
} from "../../core-model/gen/streampipes-model";

@Injectable()
export class PipelineElementRecommendationService {

    constructor(private EditorService: EditorService) {
    }

    collectPossibleElements(allElements: PipelineElementUnion[], possibleElements: PipelineElementRecommendation[]) {
        var possibleElementConfigs = [];
        possibleElements.forEach(pe => {
            possibleElementConfigs.push(this.getPipelineElementContents(allElements, pe.elementId)[0]);
        })
        return possibleElementConfigs;
    }

    populateRecommendedList(allElements, recs) {
        let elementRecommendations: any = [];
        recs.sort(function (a, b) {
            return (a.count > b.count) ? -1 : ((b.count > a.count) ? 1 : 0);
        });
        let maxRecs = recs.length > 7 ? 7 : recs.length;
        for (let i = 0; i < maxRecs; i++) {
            let el = recs[i];
            let elements = this.getPipelineElementContents(allElements, el.elementId);
            let element = elements[0];
            (element as any).weight = el.weight;
            elementRecommendations.push(element);
        }
        return elementRecommendations;

    }

    getPipelineElementContents(allElements: PipelineElementUnion[], belongsTo: string) {
        return allElements
                .filter(pe => (pe instanceof SpDataStream && pe.elementId === belongsTo)
                    || (pe instanceof InvocableStreamPipesEntity && pe.belongsTo === belongsTo));
    }
}