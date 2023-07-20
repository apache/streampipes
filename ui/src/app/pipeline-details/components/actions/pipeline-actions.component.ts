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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';
import { Pipeline } from '@streampipes/platform-services';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { CurrentUserService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-pipeline-actions',
    templateUrl: './pipeline-actions.component.html',
})
export class PipelineActionsComponent implements OnInit {
    starting = false;
    stopping = false;

    @Input()
    pipeline: Pipeline;

    @Output()
    reloadPipelineEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    hasPipelineDeletePrivileges = false;

    constructor(
        public pipelineOperationsService: PipelineOperationsService,
        private router: Router,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
    ) {}

    ngOnInit() {
        this.currentUserService.user$.subscribe(user => {
            this.hasPipelineDeletePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_DELETE_PIPELINE,
            );
        });
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);
        this.switchToPipelineView = this.switchToPipelineView.bind(this);
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !this.starting;
        } else {
            this.stopping = !this.stopping;
        }
    }

    switchToPipelineView() {
        this.router.navigate(['pipelines']);
    }

    startPipeline() {
        this.pipelineOperationsService.startPipeline(
            this.pipeline._id,
            this.reloadPipelineEmitter,
            this.toggleRunningOperation,
        );
    }

    stopPipeline() {
        this.pipelineOperationsService.stopPipeline(
            this.pipeline._id,
            this.reloadPipelineEmitter,
            this.toggleRunningOperation,
        );
    }
}
