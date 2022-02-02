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

import { Component, OnInit } from '@angular/core';
import { Pipeline, PipelineService } from '@streampipes/platform-services';
import { PipelineElementUnion } from '../editor/model/editor.model';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserPrivilege } from '../_enums/user-privilege.enum';

@Component({
    selector: 'pipeline-details',
    templateUrl: './pipeline-details.component.html',
    styleUrls: ['./pipeline-details.component.scss']
})
export class PipelineDetailsComponent implements OnInit {

    currentPipeline: string;
    pipeline: Pipeline;
    pipelineAvailable = false;

    selectedIndex = 0;
    selectedElement: PipelineElementUnion;

    hasPipelineWritePrivileges = false;
    hasPipelineDeletePrivileges = false;

    constructor(private activatedRoute: ActivatedRoute,
                private pipelineService: PipelineService,
                private authService: AuthService) {
    }

    ngOnInit() {
        this.authService.user$.subscribe(user => {
            this.hasPipelineWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_PIPELINE);
            this.hasPipelineDeletePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_DELETE_PIPELINE);
        });
        this.activatedRoute.queryParams.subscribe(params => {
            if (params['pipeline']) {
                this.currentPipeline = params['pipeline'];
                this.loadPipeline();
            }
        });
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
