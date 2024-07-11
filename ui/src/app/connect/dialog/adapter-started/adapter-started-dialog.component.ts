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

import { Component, Input, OnInit } from '@angular/core';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    AdapterDescription,
    AdapterService,
    ErrorMessage,
    PipelineOperationStatus,
    PipelineTemplateService,
    PipelineUpdateInfo,
    SpLogMessage,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineInvocationBuilder } from '../../../core-services/template/PipelineInvocationBuilder';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-started-dialog.component.html',
    styleUrls: ['./adapter-started-dialog.component.scss'],
})
export class AdapterStartedDialog implements OnInit {
    adapterInstalled = false;
    pollingActive = false;
    public pipelineOperationStatus: PipelineOperationStatus;

    /**
     * AdapterDescription that should be persisted and started
     */
    @Input() adapter: AdapterDescription;

    /**
     * Indicates if a pipeline to store the adapter events should be started
     */
    @Input() saveInDataLake: boolean;

    /**
     * Timestamp field of event. Required when storing events in the data lake.
     */
    @Input() dataLakeTimestampField: string;

    /**
     * When true a user edited an existing AdapterDescription
     */
    @Input() editMode = false;

    /**
     * This option will immediately start the adapter, when false it the adapter is only created and not started
     */
    @Input() startAdapterNow = true;

    templateErrorMessage: ErrorMessage;
    adapterUpdatePreflight = false;
    adapterPipelineUpdateInfos: PipelineUpdateInfo[];
    loading = false;
    loadingText = '';
    showPreview = false;
    adapterInstallationSuccessMessage = '';
    adapterElementId = '';
    adapterErrorMessage: SpLogMessage;

    constructor(
        public dialogRef: DialogRef<AdapterStartedDialog>,
        private adapterService: AdapterService,
        private shepherdService: ShepherdService,
        private pipelineTemplateService: PipelineTemplateService,
    ) {}

    ngOnInit() {
        if (this.editMode) {
            this.initAdapterUpdatePreflight();
        } else {
            this.addAdapter();
        }
    }

    initAdapterUpdatePreflight(): void {
        this.loadingText = `Checking migrations for adapter ${this.adapter.name}`;
        this.loading = true;
        this.adapterService
            .performPipelineMigrationPreflight(this.adapter)
            .subscribe(res => {
                if (res.length === 0) {
                    this.updateAdapter();
                } else {
                    this.adapterUpdatePreflight = true;
                    this.adapterPipelineUpdateInfos = res;
                    this.loading = false;
                }
            });
    }

    updateAdapter(): void {
        this.loadingText = `Updating adapter ${this.adapter.name}`;
        this.loading = true;
        this.adapterService.updateAdapter(this.adapter).subscribe(
            res => {
                this.onAdapterReady(
                    `Adapter ${this.adapter.name} was successfully updated and is available in the pipeline editor.`,
                );
            },
            error => {
                this.onAdapterFailure(error.error);
            },
        );
    }

    addAdapter() {
        this.loadingText = `Creating adapter ${this.adapter.name}`;
        this.loading = true;
        this.adapterService.addAdapter(this.adapter).subscribe(
            status => {
                if (status.success) {
                    const adapterElementId = status.notifications[0].title;
                    if (this.saveInDataLake) {
                        this.startSaveInDataLakePipeline(adapterElementId);
                    } else {
                        this.startAdapter(adapterElementId, true);
                    }
                } else {
                    const errorMsg: SpLogMessage = {
                        cause:
                            status.notifications.length > 0
                                ? status.notifications[0].title
                                : 'Unknown Error',
                        detail: '',
                        fullStackTrace: '',
                        level: 'ERROR',
                        title: 'Unknown Error',
                    };

                    this.onAdapterFailure(errorMsg);
                }
            },
            error => {
                this.onAdapterFailure(error.error);
            },
        );
    }

    startAdapter(adapterElementId: string, showPreview = false) {
        const successMessage =
            'Your new data stream is now available in the pipeline editor.';
        if (this.startAdapterNow) {
            this.adapterElementId = adapterElementId;
            this.loadingText = `Starting adapter ${this.adapter.name}`;
            this.adapterService
                .startAdapterByElementId(adapterElementId)
                .subscribe(
                    startStatus => {
                        this.onAdapterReady(successMessage, showPreview);
                    },
                    error => {
                        this.onAdapterFailure(error.error);
                    },
                );
        } else {
            this.onAdapterReady(successMessage, false);
        }
    }

    onAdapterFailure(adapterErrorMessage: SpLogMessage) {
        this.adapterInstalled = true;

        this.adapterErrorMessage = adapterErrorMessage;

        this.loading = false;
    }

    onAdapterReady(successMessage: string, showPreview = false): void {
        this.adapterInstallationSuccessMessage = successMessage;
        this.adapterInstalled = true;
        this.loading = false;
        if (showPreview) {
            this.showPreview = true;
        }
    }

    onCloseConfirm() {
        this.pollingActive = false;
        this.dialogRef.close('Confirm');
        this.shepherdService.trigger('confirm_adapter_started_button');
    }

    private startSaveInDataLakePipeline(adapterElementId: string) {
        this.loadingText = 'Creating pipeline to persist data stream';
        this.adapterService.getAdapter(adapterElementId).subscribe(adapter => {
            const pipelineId =
                'org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate';
            this.pipelineTemplateService
                .getPipelineTemplateInvocation(
                    adapter.correspondingDataStreamElementId,
                    pipelineId,
                )
                .subscribe(
                    res => {
                        const pipelineName = 'Persist ' + this.adapter.name;

                        const indexName = this.adapter.name;

                        const pipelineInvocation =
                            PipelineInvocationBuilder.create(res)
                                .setName(pipelineName)
                                .setTemplateId(pipelineId)
                                .setFreeTextStaticProperty(
                                    'db_measurement',
                                    indexName,
                                )
                                .setMappingPropertyUnary(
                                    'timestamp_mapping',
                                    's0::' + this.dataLakeTimestampField,
                                )
                                .setOneOfStaticProperty(
                                    'schema_update',
                                    'Update schema',
                                )
                                .build();

                        this.pipelineTemplateService
                            .createPipelineTemplateInvocation(
                                pipelineInvocation,
                            )
                            .subscribe(
                                pipelineOperationStatus => {
                                    this.pipelineOperationStatus =
                                        pipelineOperationStatus;
                                    this.startAdapter(adapterElementId, true);
                                },
                                error => {
                                    this.onAdapterFailure(error.error);
                                },
                            );
                    },
                    res => {
                        this.templateErrorMessage = res.error;
                        this.startAdapter(adapterElementId);
                    },
                );
        });
    }
}
