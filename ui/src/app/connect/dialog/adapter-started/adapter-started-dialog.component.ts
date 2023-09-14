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
import { RestService } from '../../services/rest.service';
import {
    AdapterDescription,
    ErrorMessage,
    Message,
    PipelineOperationStatus,
    PipelineTemplateService,
    SpDataStream,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';
import { PipelineInvocationBuilder } from '../../../core-services/template/PipelineInvocationBuilder';
import { AdapterService } from '../../../../../projects/streampipes/platform-services/src/lib/apis/adapter.service';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-started-dialog.component.html',
    styleUrls: ['./adapter-started-dialog.component.scss'],
})
export class AdapterStartedDialog implements OnInit {
    adapterInstalled = false;
    public adapterStatus: Message;
    public streamDescription: SpDataStream;
    pollingActive = false;
    public pipelineOperationStatus: PipelineOperationStatus;

    adapterSuccessfullyEdited = false;

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

    constructor(
        public dialogRef: DialogRef<AdapterStartedDialog>,
        private adapterService: AdapterService,
        private restService: RestService,
        private shepherdService: ShepherdService,
        private pipelineTemplateService: PipelineTemplateService,
    ) {}

    ngOnInit() {
        if (this.editMode) {
            this.editAdapter();
        } else {
            this.addAdapter();
        }
    }

    editAdapter() {
        this.adapterService.updateAdapter(this.adapter).subscribe(status => {
            this.adapterStatus = status;
            this.adapterInstalled = true;
        });
    }

    addAdapter() {
        this.adapterService.addAdapter(this.adapter).subscribe(status => {
            this.adapterStatus = status;

            if (status.success) {
                const adapterElementId = status.notifications[0].title;

                if (this.saveInDataLake) {
                    this.startSaveInDataLakePipeline(adapterElementId, status);
                } else {
                    this.startAdapter(status, adapterElementId);
                }
            }
        });
    }

    startAdapter(status: Message, adapterElementId: string) {
        if (this.startAdapterNow) {
            this.adapterService
                .startAdapterByElementId(adapterElementId)
                .subscribe(startStatus => {
                    this.showAdapterPreview(startStatus, adapterElementId);
                });
        } else {
            this.showAdapterPreview(status, adapterElementId);
        }
    }

    showAdapterPreview(status: Message, adapterElementId: string) {
        // Start preview on streams and message for sets
        if (status.success) {
            this.getLiveViewPreview(adapterElementId);
            this.adapterInstalled = true;
        }
    }

    onCloseConfirm() {
        this.pollingActive = false;
        this.dialogRef.close('Confirm');
        this.shepherdService.trigger('confirm_adapter_started_button');
    }

    private getLiveViewPreview(adapterElementId: string) {
        this.adapterService.getAdapter(adapterElementId).subscribe(adapter => {
            this.restService
                .getSourceDetails(adapter.correspondingDataStreamElementId)
                .subscribe(st => {
                    this.streamDescription = st;
                    this.pollingActive = true;
                });
        });
    }

    private startSaveInDataLakePipeline(
        adapterElementId: string,
        message: Message,
    ) {
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
                                .build();

                        this.pipelineTemplateService
                            .createPipelineTemplateInvocation(
                                pipelineInvocation,
                            )
                            .subscribe(pipelineOperationStatus => {
                                this.pipelineOperationStatus =
                                    pipelineOperationStatus;
                                this.startAdapter(message, adapterElementId);
                            });
                    },
                    res => {
                        this.templateErrorMessage = res.error;
                        this.startAdapter(message, adapterElementId);
                    },
                );
        });
    }
}
