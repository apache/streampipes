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

import * as angular from "angular";

export class PipelineDetailsController {

    pipelines: any;
    starting: any;
    stopping: any;
    refreshPipelines: any;
    PipelineOperationsService: any;
    VersionService: any;
    activeCategory: any;
    dtOptions = {paging: false, searching: false,   "order": [], "columns": [
            { "orderable": false },
            null,
            null,
            { "orderable": false },
        ]};

    constructor(PipelineOperationsService, VersionService) {
        this.PipelineOperationsService = PipelineOperationsService;
        this.VersionService = VersionService;
        this.starting = false;
        this.stopping = false;
    }

    $onInit() {
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);

        angular.forEach(this.pipelines, pipeline => {
            if (pipeline.immediateStart) {
                if (!pipeline.running) {
                    this.PipelineOperationsService.startPipeline(pipeline._id, this.toggleRunningOperation, this.refreshPipelines);
                }
            }
        });
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !(this.starting);
        } else {
            this.stopping = !(this.stopping);
        }
    }

}

PipelineDetailsController.$inject = ['PipelineOperationsService', 'VersionService'];