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

import { Pipeline } from '@streampipes/platform-services';
import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { PipelineOperationsService } from '../../services/pipeline-operations.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AuthService } from '../../../services/auth.service';
import { UserRole } from '../../../_enums/user-role.enum';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { CurrentUserService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-pipeline-overview',
    templateUrl: './pipeline-overview.component.html',
    styleUrls: ['./pipeline-overview.component.scss'],
})
export class PipelineOverviewComponent implements OnInit {
    _pipelines: Pipeline[];
    _activeCategoryId: string;

    filteredPipelinesAvailable = false;

    @Input()
    pipelineToStart: Pipeline;

    @Output()
    refreshPipelinesEmitter: EventEmitter<boolean> =
        new EventEmitter<boolean>();

    displayedColumns: string[] = [
        'status',
        'start',
        'name',
        'lastModified',
        'action',
    ];

    dataSource: MatTableDataSource<Pipeline>;

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSize = 1;

    @ViewChild(MatSort) sort: MatSort;

    starting: any;
    stopping: any;

    isAdmin = false;
    hasPipelineWritePrivileges = false;
    hasPipelineDeletePrivileges = false;

    constructor(
        public pipelineOperationsService: PipelineOperationsService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
    ) {
        this.starting = false;
        this.stopping = false;
    }

    ngOnInit() {
        this.currentUserService.user$.subscribe(user => {
            this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
            this.hasPipelineWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
            );
            this.hasPipelineDeletePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_DELETE_PIPELINE,
            );
        });
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);

        if (this.pipelineToStart) {
            if (!this.pipelineToStart.running) {
                this.pipelineOperationsService.startPipeline(
                    this.pipelineToStart._id,
                    this.refreshPipelinesEmitter,
                    this.toggleRunningOperation,
                );
            }
        }
    }

    toggleRunningOperation(currentOperation) {
        if (currentOperation === 'starting') {
            this.starting = !this.starting;
        } else {
            this.stopping = !this.stopping;
        }
    }

    openPipelineNotificationsDialog(pipeline: Pipeline) {
        this.pipelineOperationsService.showPipelineNotificationsDialog(
            pipeline,
            this.refreshPipelinesEmitter,
        );
    }

    get pipelines() {
        return this._pipelines;
    }

    @Input()
    set pipelines(pipelines: Pipeline[]) {
        this._pipelines = pipelines;
        this.addPipelinesToTable();
    }

    get activeCategoryId(): string {
        return this._activeCategoryId;
    }

    @Input()
    set activeCategoryId(activeCategoryId: string) {
        this._activeCategoryId = activeCategoryId;
        if (this._pipelines) {
            this.addPipelinesToTable();
        }
    }

    addPipelinesToTable() {
        this.dataSource = new MatTableDataSource<Pipeline>(
            this.filterPipelines(),
        );
        setTimeout(() => {
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
        });
    }

    filterPipelines(): Pipeline[] {
        const filteredPipelines: Pipeline[] = this._pipelines.filter(
            pipeline =>
                !this._activeCategoryId ||
                (pipeline.pipelineCategories &&
                    pipeline.pipelineCategories.some(
                        pc => pc === this.activeCategoryId,
                    )),
        );
        this.filteredPipelinesAvailable = filteredPipelines.length > 0;
        return filteredPipelines.sort((a, b) => a.name.localeCompare(b.name));
    }
}
