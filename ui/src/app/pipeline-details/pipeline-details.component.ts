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

import {Component, Inject, OnInit} from "@angular/core";
import {PipelineService} from "../platform-services/apis/pipeline.service";
import {Pipeline} from "../core-model/gen/streampipes-model";
import {PipelineElementUnion} from "../editor/model/editor.model";

@Component({
    selector: 'pipeline-details',
    templateUrl: './pipeline-details.component.html',
    styleUrls: ['./pipeline-details.component.scss']
})
export class PipelineDetailsComponent implements OnInit {

    currentPipeline: string;
    pipeline: Pipeline;
    pipelineAvailable: boolean = false;

    selectedIndex: number = 0;
    selectedElement: PipelineElementUnion;

    constructor(@Inject('$stateParams') private $stateParams,
                private pipelineService: PipelineService) {
        this.currentPipeline = $stateParams.pipeline;
    }

    ngOnInit() {
        this.loadPipeline();
    }

    setSelectedIndex(index: number) {
        this.selectedIndex = index;
    }

    loadPipeline() {
        this.pipelineService.getPipelineById(this.currentPipeline)
            .subscribe(pipeline => {
                this.pipeline = pipeline;
                this.pipelineAvailable = true;
            });
    }

    selectElement(element: PipelineElementUnion) {
        this.selectedElement = element;
    }

}
