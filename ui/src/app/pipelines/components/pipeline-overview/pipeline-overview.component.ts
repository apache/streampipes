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
    inject,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { PipelineOperationsService } from '../../services/pipeline-operations.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { CurrentUserService } from '@streampipes/shared-ui';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-pipeline-overview',
    templateUrl: './pipeline-overview.component.html',
    styleUrls: ['./pipeline-overview.component.scss'],
    standalone: false,
})
export class PipelineOverviewComponent implements OnInit, OnDestroy {
    _pipelines: Pipeline[];

    @Output()
    refreshPipelinesEmitter: EventEmitter<boolean> =
        new EventEmitter<boolean>();

    displayedColumns: string[] = [
        'status',
        'start',
        'name',
        'lastModified',
        'actions',
    ];

    dataSource: MatTableDataSource<Pipeline> = new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;

    starting = false;
    stopping = false;
    hasPipelineWritePrivileges = false;

    userSub: Subscription;

    public pipelineOperationsService = inject(PipelineOperationsService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);

    ngOnInit() {
        this.userSub = this.currentUserService.user$.subscribe(user => {
            this.hasPipelineWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_PIPELINE,
            );
        });
        this.toggleRunningOperation = this.toggleRunningOperation.bind(this);
    }

    toggleRunningOperation(currentOperation: string) {
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

    addPipelinesToTable() {
        this.dataSource.data = this._pipelines;
        this.dataSource.sortingDataAccessor = (pipeline, column) => {
            if (column === 'status') {
                return pipeline.running;
            } else if (column === 'lastModified') {
                return pipeline.createdAt;
            }
            return pipeline[column];
        };
        setTimeout(() => {
            this.dataSource.sort = this.sort;
        });
    }

    ngOnDestroy() {
        this.userSub?.unsubscribe();
    }
}
