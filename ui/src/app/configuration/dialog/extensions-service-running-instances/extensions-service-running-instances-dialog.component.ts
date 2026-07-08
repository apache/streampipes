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

import { Component, Input, OnInit, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { SpServiceRegistration } from '@streampipes/platform-services';
import {
    DialogRef,
    SpAlertBannerComponent,
    SpLabelComponent,
    SpTableComponent,
    SpTableNameSearchConfig,
} from '@streampipes/shared-ui';
import {
    ConfigurationService,
    RunningAdapterInstance,
    RunningExtensionInstances,
    RunningPipelineElementInstance,
} from '../../shared/configuration.service';

type RunningAdapterInstanceRow = RunningAdapterInstance & {
    displayName: string;
    relation: string;
};

type RunningPipelineElementInstanceRow = RunningPipelineElementInstance & {
    displayName: string;
    pipelineDisplayName: string;
    relation: string;
};

type PendingRemoval =
    | {
          type: 'adapter';
          item: RunningAdapterInstanceRow;
      }
    | {
          type: 'pipelineElement';
          item: RunningPipelineElementInstanceRow;
      }
    | {
          type: 'all';
      };

@Component({
    selector: 'sp-extensions-service-running-instances-dialog',
    templateUrl: './extensions-service-running-instances-dialog.component.html',
    styleUrls: ['./extensions-service-running-instances-dialog.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        LayoutGapDirective,
        MatButton,
        MatCell,
        MatCellDef,
        MatColumnDef,
        MatDivider,
        MatHeaderCell,
        MatHeaderCellDef,
        MatIcon,
        MatIconButton,
        MatProgressSpinner,
        MatTooltip,
        SpAlertBannerComponent,
        SpLabelComponent,
        SpTableComponent,
        TranslatePipe,
    ],
})
export class SpExtensionsServiceRunningInstancesDialogComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<SpExtensionsServiceRunningInstancesDialogComponent>>(
            DialogRef,
        );
    private configurationService = inject(ConfigurationService);

    @Input()
    serviceReg: SpServiceRegistration;

    adapterColumns = ['name', 'state', 'relation', 'instanceId', 'action'];
    pipelineElementColumns = [
        'name',
        'pipelineName',
        'type',
        'relation',
        'instanceId',
        'action',
    ];
    runningInstances: RunningExtensionInstances;
    adapterDataSource = new MatTableDataSource<RunningAdapterInstanceRow>();
    pipelineElementDataSource =
        new MatTableDataSource<RunningPipelineElementInstanceRow>();
    adapterNameSearchConfig: SpTableNameSearchConfig<RunningAdapterInstanceRow> =
        {
            enabled: true,
            placeholder: 'Filter adapters',
            searchKey: [
                'displayName',
                'instanceId',
                'appId',
                'state',
                'relation',
            ],
        };
    pipelineElementNameSearchConfig: SpTableNameSearchConfig<RunningPipelineElementInstanceRow> =
        {
            enabled: true,
            placeholder: 'Filter pipeline elements',
            searchKey: [
                'displayName',
                'pipelineDisplayName',
                'instanceId',
                'appId',
                'pipelineId',
                'type',
                'relation',
            ],
        };
    loading = true;
    loadError = false;
    removing = false;
    pendingRemoval?: PendingRemoval;

    get totalInstanceCount(): number {
        return (
            this.adapterDataSource.data.length +
            this.pipelineElementDataSource.data.length
        );
    }

    ngOnInit(): void {
        this.loadRunningInstances();
    }

    loadRunningInstances(): void {
        this.loading = true;
        this.loadError = false;
        this.configurationService
            .getRunningExtensionInstances(this.serviceReg.svcId)
            .subscribe({
                next: response => {
                    this.runningInstances = response;
                    this.adapterDataSource.data = response.adapters.map(
                        adapter => this.makeAdapterRow(adapter),
                    );
                    this.pipelineElementDataSource.data =
                        response.pipelineElements.map(pipelineElement =>
                            this.makePipelineElementRow(pipelineElement),
                        );
                    this.loading = false;
                },
                error: () => {
                    this.loading = false;
                    this.loadError = true;
                },
            });
    }

    requestRemoveAdapterInstance(adapter: RunningAdapterInstanceRow): void {
        this.pendingRemoval = {
            type: 'adapter',
            item: adapter,
        };
    }

    requestRemovePipelineElementInstance(
        pipelineElement: RunningPipelineElementInstanceRow,
    ): void {
        this.pendingRemoval = {
            type: 'pipelineElement',
            item: pipelineElement,
        };
    }

    requestRemoveAllInstances(): void {
        this.pendingRemoval = {
            type: 'all',
        };
    }

    cancelRemoval(): void {
        this.pendingRemoval = undefined;
    }

    confirmRemoval(): void {
        const pendingRemoval = this.pendingRemoval;

        if (!pendingRemoval) {
            return;
        }

        this.pendingRemoval = undefined;

        if (pendingRemoval.type === 'adapter') {
            this.removeAdapterInstance(pendingRemoval.item);
        } else if (pendingRemoval.type === 'pipelineElement') {
            this.removePipelineElementInstance(pendingRemoval.item);
        } else {
            this.removeAllInstances();
        }
    }

    getPendingRemovalTitle(): string {
        if (!this.pendingRemoval) {
            return '';
        }

        if (this.pendingRemoval.type === 'adapter') {
            return 'Remove adapter instance?';
        } else if (this.pendingRemoval.type === 'pipelineElement') {
            return 'Remove pipeline element instance?';
        } else {
            return 'Remove all running instances?';
        }
    }

    getPendingRemovalDescription(): string {
        if (!this.pendingRemoval) {
            return '';
        }

        if (this.pendingRemoval.type === 'adapter') {
            return this.pendingRemoval.item.instanceId;
        } else if (this.pendingRemoval.type === 'pipelineElement') {
            return this.pendingRemoval.item.instanceId;
        } else {
            return 'This removes all instances currently reported by this extension service.';
        }
    }

    private removeAdapterInstance(adapter: RunningAdapterInstance): void {
        this.removeInstance(() =>
            this.configurationService.removeRunningAdapterInstance(
                this.serviceReg.svcId,
                adapter.instanceId,
            ),
        );
    }

    private removePipelineElementInstance(
        pipelineElement: RunningPipelineElementInstance,
    ): void {
        this.removeInstance(() =>
            this.configurationService.removeRunningPipelineElementInstance(
                this.serviceReg.svcId,
                pipelineElement.instanceId,
            ),
        );
    }

    private removeAllInstances(): void {
        this.removeInstance(() =>
            this.configurationService.removeAllRunningExtensionInstances(
                this.serviceReg.svcId,
            ),
        );
    }

    close(): void {
        this.dialogRef.close();
    }

    getAdapterStateTone(
        state: RunningAdapterInstance['state'],
    ): 'success' | 'warning' | 'info' | 'neutral' {
        if (state === 'RUNNING') {
            return 'success';
        } else if (state === 'STOPPING') {
            return 'warning';
        } else if (state === 'STARTING') {
            return 'info';
        } else {
            return 'neutral';
        }
    }

    getPipelineElementTypeTone(
        type: RunningPipelineElementInstance['type'],
    ): 'info' | 'neutral' | 'warning' {
        if (type === 'PROCESSOR') {
            return 'info';
        } else if (type === 'SINK') {
            return 'neutral';
        } else {
            return 'warning';
        }
    }

    getRelationLabel(orphaned: boolean): string {
        return orphaned ? 'Orphaned' : 'Configured';
    }

    getRelationTone(orphaned: boolean): 'success' | 'warning' {
        return orphaned ? 'warning' : 'success';
    }

    private makeAdapterRow(
        adapter: RunningAdapterInstance,
    ): RunningAdapterInstanceRow {
        return {
            ...adapter,
            displayName: adapter.orphaned
                ? 'Orphaned adapter instance'
                : (adapter.name ?? ''),
            relation: this.getRelationLabel(adapter.orphaned),
        };
    }

    private makePipelineElementRow(
        pipelineElement: RunningPipelineElementInstance,
    ): RunningPipelineElementInstanceRow {
        return {
            ...pipelineElement,
            displayName: pipelineElement.orphaned
                ? 'Orphaned pipeline element instance'
                : (pipelineElement.name ?? ''),
            pipelineDisplayName: pipelineElement.orphaned
                ? '-'
                : (pipelineElement.pipelineName ?? ''),
            relation: this.getRelationLabel(pipelineElement.orphaned),
        };
    }

    private removeInstance(removeFn: () => Observable<object>): void {
        this.removing = true;
        this.loadError = false;
        removeFn().subscribe({
            next: () => {
                this.removing = false;
                this.pendingRemoval = undefined;
                this.loadRunningInstances();
            },
            error: () => {
                this.removing = false;
                this.loadError = true;
            },
        });
    }
}
