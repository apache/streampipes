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

import {
    Component,
    Input,
    OnInit,
    EventEmitter,
    Output,
    inject,
} from '@angular/core';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import {
    AdapterDescription,
    AdapterService,
    SpAssetTreeNode,
    CompactPipeline,
    CompactPipelineElement,
    DatalakeRestService,
    ErrorMessage,
    Message,
    PipelineOperationStatus,
    PipelineTemplateService,
    PipelineUpdateInfo,
    SpLogMessage,
    LinkageData,
    CompactPipelineService,
} from '@streampipes/platform-services';
import { AssetSaveService, DialogRef } from '@streampipes/shared-ui';

import { TranslateService } from '@ngx-translate/core';
import { firstValueFrom, lastValueFrom } from 'rxjs';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-started-dialog.component.html',
    standalone: false,
})
export class AdapterStartedDialog implements OnInit {
    translateService = inject(TranslateService);
    public dialogRef = inject(DialogRef<AdapterStartedDialog>);
    private adapterService = inject(AdapterService);
    private shepherdService = inject(ShepherdService);
    private pipelineTemplateService = inject(PipelineTemplateService);
    private compactPipelineService = inject(CompactPipelineService);
    private assetSaveService = inject(AssetSaveService);
    private dataLakeService = inject(DatalakeRestService);

    adapterInstalled = false;

    public pipelineOperationStatus: PipelineOperationStatus;

    /**
     * AdapterDescription that should be persisted and started
     */
    @Input() adapter: AdapterDescription;

    /**
     * Assets selectedAsset to link the adapter tp
     */
    @Input() selectedAssets: SpAssetTreeNode[];
    @Input() deselectedAssets: SpAssetTreeNode[];
    @Input() originalAssets: SpAssetTreeNode[];

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

    @Input()
    allResourcesAlias = this.translateService.instant('Resources');

    @Output() linkageDataEmitter: EventEmitter<LinkageData[]> =
        new EventEmitter<LinkageData[]>();

    templateErrorMessage: ErrorMessage;
    adapterUpdatePreflight = false;
    adapterPipelineUpdateInfos: PipelineUpdateInfo[];
    loading = false;
    loadingText = '';
    showPreview = false;
    adapterInstallationSuccessMessage = '';
    adapterElementId = '';
    adapterErrorMessage: SpLogMessage;
    addToAssetText = '';
    deletedFromAssetText = '';

    ngOnInit() {
        if (this.editMode) {
            this.initAdapterUpdatePreflight();
        } else {
            this.addAdapter();
        }
    }

    initAdapterUpdatePreflight(): void {
        this.loadingText = this.translateService.instant(
            'Checking migrations for adapter {{adapterName}}',
            {
                adapterName: this.adapter.name,
            },
        );
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
        this.loadingText = this.translateService.instant(
            'Updating adapter {{adapterName}}',
            {
                adapterName: this.adapter.name,
            },
        );

        this.loadingText = this.translateService.instant(
            'Updating adapter {{adapterName}}',
            {
                adapterName: this.adapter.name,
            },
        );

        this.loading = true;
        this.adapterService.updateAdapter(this.adapter).subscribe({
            next: status => {
                if (status.success) {
                    this.onAdapterReady(
                        `Adapter ${this.adapter.name} was successfully updated and is available in the pipeline editor.`,
                    );
                } else {
                    const errorLogMessage = this.getErrorLogMessage(status);

                    this.onAdapterFailure(errorLogMessage);
                }

                this.addToAsset();
            },
            error: error => {
                this.onAdapterFailure(error.error);
            },
        });
    }

    addAdapter() {
        this.loadingText = this.translateService.instant(
            'Creating adapter {{adapterName}}',
            {
                adapterName: this.adapter.name,
            },
        );
        this.loadingText = this.translateService.instant(
            'Creating adapter {{adapterName}}',
            {
                adapterName: this.adapter.name,
            },
        );
        this.loading = true;
        this.adapterService.addAdapter(this.adapter).subscribe(
            status => {
                if (status.success) {
                    const adapterElementId = status.notifications[0].title;
                    this.adapterElementId = adapterElementId;
                    this.adapterElementId = adapterElementId;
                    if (this.saveInDataLake) {
                        this.startSaveInDataLakePipeline(adapterElementId);
                    } else {
                        this.startAdapter(adapterElementId, true);
                        this.addToAsset();
                    }
                } else {
                    const errorMsg: SpLogMessage =
                        this.getErrorLogMessage(status);

                    this.onAdapterFailure(errorMsg);
                }
            },
            error => {
                this.onAdapterFailure(error.error);
            },
        );
    }

    private getErrorLogMessage(status: Message): SpLogMessage {
        const notification = status.notifications[0] || {
            title: 'Unknown Error',
            description: '',
        };
        return {
            cause: notification.title,
            detail: '',
            fullStackTrace: notification.description,
            level: 'ERROR',
            title: 'Unknown Error',
        };
    }

    startAdapter(adapterElementId: string, showPreview = false) {
        const successMessage = this.translateService.instant(
            'Your new data stream is now available in the pipeline editor.',
        );
        if (this.startAdapterNow) {
            this.adapterElementId = adapterElementId;
            this.loadingText = this.translateService.instant(
                'Starting adapter {{adapterName}}',
                {
                    adapterName: this.adapter.name,
                },
            );
            this.loadingText = this.translateService.instant(
                'Starting adapter {{adapterName}}',
                {
                    adapterName: this.adapter.name,
                },
            );
            this.adapterService
                .startAdapterByElementId(adapterElementId)
                .subscribe(
                    () => {
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
        this.dialogRef.close('Confirm');
        this.shepherdService.trigger('confirm_adapter_started_button');
    }

    async addToAsset(): Promise<void> {
        let linkageData: LinkageData[];
        try {
            if (!this.editMode) {
                const adapter = await this.getAdapter();
                linkageData = this.createLinkageData(adapter);

                if (this.saveInDataLake) {
                    await this.addDataLakeLinkageData(adapter, linkageData);
                }
            } else {
                linkageData = this.createLinkageData(this.adapter);
            }

            await this.saveAssets(linkageData);

            this.setSuccessMessage();
        } catch (err) {
            console.error('Error in addToAsset:', err);
        }
    }

    private async getAdapter(): Promise<AdapterDescription> {
        return await firstValueFrom(
            this.adapterService.getAdapter(this.adapterElementId),
        );
    }

    private createLinkageData(adapter: AdapterDescription): LinkageData[] {
        return [
            {
                type: 'adapter',
                id:
                    this.adapterElementId !== ''
                        ? this.adapterElementId
                        : adapter.elementId,
                name: adapter.name,
            },
            {
                type: 'data-source',
                id: adapter.correspondingDataStreamElementId,
                name: adapter.name,
            },
        ];
    }

    private async addDataLakeLinkageData(
        adapter: AdapterDescription,
        linkageData: LinkageData[],
    ): Promise<void> {
        const pipelineId = `persist-${this.adapter.name.replaceAll(' ', '-')}`;
        linkageData.push({
            type: 'pipeline',
            id: pipelineId,
            name: pipelineId,
        });

        const res = await lastValueFrom(
            this.dataLakeService.getMeasurementByName(adapter.name),
        );

        linkageData.push({
            type: 'measurement',
            id: res.elementId,
            name: adapter.name,
        });
    }

    private async saveAssets(linkageData: LinkageData[]): Promise<void> {
        await this.assetSaveService.saveSelectedAssets(
            this.selectedAssets,
            linkageData,
            this.deselectedAssets,
            this.originalAssets,
        );
    }

    private setSuccessMessage(): void {
        if (this.selectedAssets.length > 0) {
            this.addToAssetText = this.translateService.instant(
                'Your Assets were successfully added.',
            );
        }
        if (this.deselectedAssets && this.deselectedAssets.length > 0) {
            this.deletedFromAssetText = this.translateService.instant(
                'Your Assets were successfully deleted.',
            );
        }
    }

    private formatWithAnd(list: string[]): string {
        if (list.length === 1) return list[0];
        const lastItem = list.pop();
        return `${list.join(', ')}, and ${lastItem}`;
    }

    private startSaveInDataLakePipeline(adapterElementId: string) {
        this.loadingText = this.translateService.instant(
            'Creating pipeline to persist data stream',
        );
        this.adapterService.getAdapter(adapterElementId).subscribe(adapter => {
            this.pipelineTemplateService
                .findById('sp-internal-persist')
                .subscribe(
                    template => {
                        const pipeline: CompactPipeline = {
                            id:
                                'persist-' +
                                this.adapter.name.replaceAll(' ', '-'),
                            name: 'Persist ' + this.adapter.name,
                            description: '',
                            pipelineElements: this.makeTemplateConfigs(
                                template.pipeline,
                                adapter,
                            ),
                            createOptions: {
                                persist: false,
                                start: true,
                            },
                        };
                        this.compactPipelineService.create(pipeline).subscribe(
                            pipelineOperationStatus => {
                                this.pipelineOperationStatus =
                                    pipelineOperationStatus;
                                this.startAdapter(adapterElementId, true);
                                this.addToAsset();
                            },
                            error => {
                                this.onAdapterFailure(error.error);
                            },
                        );
                    },
                    error => {
                        this.templateErrorMessage = error.error;
                        this.startAdapter(adapterElementId);
                    },
                );
        });
    }

    makeTemplateConfigs(
        template: CompactPipelineElement[],
        adapter: AdapterDescription,
    ): CompactPipelineElement[] {
        template[0].configuration.push(
            {
                db_measurement: this.adapter.name,
            },
            {
                timestamp_mapping: 's0::' + this.dataLakeTimestampField,
            },
            {
                dimensions_selection: adapter.eventSchema.eventProperties
                    .filter(ep => ep.propertyScope === 'DIMENSION_PROPERTY')
                    .map(ep => ep.runtimeName),
            },
        );
        template.push({
            type: 'stream',
            ref: 'stream1',
            configuration: undefined,
            id: adapter.correspondingDataStreamElementId,
            connectedTo: undefined,
            output: undefined,
        });
        return template;
    }
}
