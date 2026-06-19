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

import { PipelineSummaryDto } from '@streampipes/platform-services';
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
import { StartAllPipelinesDialogComponent } from '../../dialog/start-all-pipelines/start-all-pipelines-dialog.component';
import { PipelineOperationsService } from '../../services/pipeline-operations.service';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
    SpTableActionsDirective,
    SpTableAssetContextConfig,
    SpTableComponent,
    SpTableMultiActionExecuteEvent,
    SpTableMultiActionOption,
} from '@streampipes/shared-ui';
import { Subscription } from 'rxjs';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { DatePipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-pipeline-overview',
    templateUrl: './pipeline-overview.component.html',
    styleUrls: ['./pipeline-overview.component.scss'],
    imports: [
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MatTooltip,
        MatIconButton,
        MatIcon,
        LayoutGapDirective,
        SpTableActionsDirective,
        MatMenuItem,
        DatePipe,
        TranslatePipe,
    ],
})
export class PipelineOverviewComponent implements OnInit, OnDestroy {
    _pipelines: PipelineSummaryDto[];

    @Output()
    refreshPipelinesEmitter: EventEmitter<boolean> =
        new EventEmitter<boolean>();

    displayedColumns: string[] = [
        'status',
        'start',
        'name',
        'assetContext',
        'lastModified',
        'actions',
    ];

    dataSource: MatTableDataSource<PipelineSummaryDto> =
        new MatTableDataSource();
    @ViewChild(MatSort) sort: MatSort;

    starting = false;
    stopping = false;
    hasPipelineWritePrivileges = false;
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'pipeline',
        resourceIdKey: 'elementId',
    };
    readonly bulkPipelineActionOptions: SpTableMultiActionOption[] = [
        { value: 'start', label: 'Start selected', icon: 'play_arrow' },
        { value: 'stop', label: 'Stop selected', icon: 'stop' },
        { value: 'forceStop', label: 'Force stop selected', icon: 'stop' },
    ];

    userSub: Subscription;

    public pipelineOperationsService = inject(PipelineOperationsService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private dialogService = inject(DialogService);

    ngOnInit() {
        this.userSub = this.currentUserService.user$.subscribe(() => {
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

    openPipelineNotificationsDialog(pipeline: PipelineSummaryDto) {
        this.pipelineOperationsService.showPipelineNotificationsDialog(
            pipeline,
            this.refreshPipelinesEmitter,
        );
    }

    get pipelines() {
        return this._pipelines;
    }

    @Input()
    set pipelines(pipelines: PipelineSummaryDto[]) {
        this._pipelines = pipelines;
        this.addPipelinesToTable();
    }

    addPipelinesToTable() {
        this.dataSource.data = this._pipelines ?? [];
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

    startStopSelectedPipelines(
        selectedPipelines: PipelineSummaryDto[],
        action: boolean,
        forceStop = false,
    ) {
        const pipelines = selectedPipelines.filter(pipeline =>
            action ? !pipeline.running && pipeline.valid : pipeline.running,
        );

        if (!pipelines.length) {
            return;
        }

        const dialogRef: DialogRef<StartAllPipelinesDialogComponent> =
            this.dialogService.open(StartAllPipelinesDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: (action ? 'Start' : 'Stop') + ' selected pipelines',
                width: '70vw',
                data: {
                    pipelines,
                    action,
                    forceStop,
                },
            });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.refreshPipelinesEmitter.emit(true);
            }
        });
    }

    executeSelectedPipelineAction(
        event: SpTableMultiActionExecuteEvent<PipelineSummaryDto>,
    ) {
        if (
            !this.hasPipelineWritePrivileges ||
            this.starting ||
            this.stopping
        ) {
            return;
        }

        if (
            event.action !== 'start' &&
            event.action !== 'stop' &&
            event.action !== 'forceStop'
        ) {
            return;
        }

        this.startStopSelectedPipelines(
            event.selectedRows,
            event.action === 'start',
            event.action === 'forceStop',
        );
    }

    ngOnDestroy() {
        this.userSub?.unsubscribe();
    }
}
