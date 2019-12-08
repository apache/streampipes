
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

export class PipelineActionsController {

    PipelineOperationsService: any;
    VersionService: any;
    starting: any;
    stopping: any;
    pipeline: any;
    $state: any;
    loadPipeline: any;

    constructor(PipelineOperationsService, $state, VersionService) {
        this.PipelineOperationsService = PipelineOperationsService;
        this.starting = false;
        this.stopping = false;
        this.$state = $state;
    }

    $onInit() {
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);
        this.reload = this.reload.bind(this);
        this.switchToPipelineView = this.switchToPipelineView.bind(this);
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !(this.starting);
        } else {
            this.stopping = !(this.stopping);
        }
    }

    reload() {
        this.loadPipeline();
    }

    switchToPipelineView() {
        this.$state.go("streampipes.pipelines");
    }

}

PipelineActionsController.$inject = ['PipelineOperationsService', '$state', 'VersionService'];
