/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { inject, Injectable, signal } from '@angular/core';
import {
    AdapterDescription,
    ConnectScriptLanguagesService,
    EventSchema,
    ScriptMetadata,
    SpLogMessage,
} from '@streampipes/platform-services';
import { AdapterConfigurationState } from './AdapterConfigurationState';
import { HttpErrorResponse } from '@angular/common/http';
import { RestService } from '../../../services/rest.service';
import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmDialogComponent } from '@streampipes/shared-ui';

@Injectable({
    providedIn: 'root',
})
export class AdapterConfigurationStateService {
    private dialog = inject(MatDialog);
    private translateService = inject(TranslateService);
    private restService = inject(RestService);
    private scriptLanguagesService = inject(ConnectScriptLanguagesService);

    private initialState: AdapterConfigurationState = {
        adapterSettingsChanged: false,
        adapterSettingsString: '',

        scriptActive: false,
        availableScriptMetadata: null,
        loadingAvailableScriptsError: null,
        isLoadingAvailableScripts: false,

        selectedScriptMetadata: null,

        currentScript: '',
        initialScript: null,

        transformationConfigurationChanged: false,
        transformationConfigurationString: '',

        adapterDescription: null,
        isGettingSample: false,
        sampleError: null,
        isRunningScript: false,
        scriptError: null,

        autoLoadSchema: true,
        isGettingEventSchema: false,
        getEventSchemaError: null,
        isPreviewLoading: false,
        resultPreview: {},
    };

    private _state = signal<AdapterConfigurationState>(this.initialState);

    public state = this._state.asReadonly();

    public updateState(newState: Partial<AdapterConfigurationState>): void {
        this._state.update(current => ({ ...current, ...newState }));
    }

    public initializeCreateMode(adapter: AdapterDescription): void {
        this.updateState({ adapterDescription: adapter });
    }

    public initializeEditMode(adapter: AdapterDescription): void {
        this.updateState({
            adapterDescription: adapter,
            autoLoadSchema: false,
        });
    }

    public updateAdapter(adapter: AdapterDescription): void {
        // Cloning is required to trigger all computed signals
        const clonedAdapter = this.cloneAdapter(adapter);

        const adapterSettingsChanged =
            this.checkIfAdapterSettingsChanged(clonedAdapter);
        const transformationConfigurationChanged =
            this.checkIfTransformationConfigurationChanged(clonedAdapter);

        this.updateState({
            adapterDescription: { ...clonedAdapter },
            adapterSettingsChanged: adapterSettingsChanged,
            transformationConfigurationChanged:
                transformationConfigurationChanged,
        });
    }

    private checkIfAdapterSettingsChanged(
        current: AdapterDescription,
    ): boolean {
        const lastSynced = this.state().adapterSettingsString;
        if (!lastSynced) return false;

        const currentConfigStr = JSON.stringify(current.config);
        return currentConfigStr !== lastSynced;
    }

    public checkIfTransformationConfigurationChanged(
        current: AdapterDescription,
    ): boolean {
        const lastSynced = this.state().transformationConfigurationString;
        if (!lastSynced || lastSynced === '') return false;
        const currentConfigStr = JSON.stringify(current.transformationConfig);
        return currentConfigStr !== lastSynced;
    }

    public loadAndInitializeScript(
        adapterDescription: AdapterDescription,
    ): void {
        this.loadAvailableScripts(adapterDescription).subscribe({
            next: scriptMetadata => {
                this.updateState({
                    availableScriptMetadata: scriptMetadata,
                    isLoadingAvailableScripts: false,
                });
                this.initializeScriptState(adapterDescription);
            },
            error: (error: HttpErrorResponse) => {
                this.updateState({
                    loadingAvailableScriptsError: error.error as SpLogMessage,
                    isLoadingAvailableScripts: false,
                });
            },
        });
    }

    private loadAvailableScripts(
        adapter: AdapterDescription,
    ): Observable<ScriptMetadata[]> {
        this.updateState({ isLoadingAvailableScripts: true });
        return this.scriptLanguagesService.getAll(adapter);
    }

    private initializeScriptState(adapter: AdapterDescription) {
        const scripts = this.state().availableScriptMetadata;

        if (!scripts || scripts.length === 0) return;

        const existingScript = adapter.transformationConfig?.script;
        const existingLanguage = adapter.transformationConfig?.language;

        let activeScript: string;
        let activeScriptMetadata: ScriptMetadata;

        if (existingScript) {
            activeScript = existingScript;
            activeScriptMetadata =
                scripts.find(s => s.language === existingLanguage) ||
                scripts.find(s => s.language === 'javascript') ||
                scripts[0];
        } else {
            activeScriptMetadata =
                scripts.find(s => s.language === 'javascript') || scripts[0];
            activeScript = activeScriptMetadata.template;
        }

        this.updateState({
            selectedScriptMetadata: activeScriptMetadata,
            currentScript: activeScript,
            initialScript: {
                scriptMetadata: activeScriptMetadata,
                script: activeScript,
            },
        });
    }

    resetScriptToInitial(): void {
        const initial = this.state().initialScript;

        if (initial) {
            this.updateState({
                currentScript: initial.script,
                selectedScriptMetadata: initial.scriptMetadata,
            });
        }
    }

    public updateCurrentScript(script: string): void {
        this.updateState({ currentScript: script });
    }

    private cloneAdapter(adapter: AdapterDescription): AdapterDescription {
        return {
            ...adapter,
            dataStream: {
                ...adapter.dataStream,
                eventSchema: {
                    ...adapter.dataStream.eventSchema,
                },
            },
        };
    }

    // New action method focusing on state transitions
    public getSampleEvent(adapter: AdapterDescription): void {
        this.updateState({
            isGettingSample: true,
            sampleError: null,
            adapterDescription: adapter,
        });

        this.restService.getSampleEvents(adapter).subscribe({
            next: sampleData => {
                const updatedAdapter = { ...adapter };
                updatedAdapter.transformationConfig.inputs = [
                    sampleData.samples[0],
                ];

                const scriptActive =
                    updatedAdapter.transformationConfig.scriptActive;

                if (!scriptActive) {
                    updatedAdapter.transformationConfig.outputs =
                        updatedAdapter.transformationConfig.inputs;
                }

                const transformationConfigurationChanged =
                    this.checkIfTransformationConfigurationChanged(
                        updatedAdapter,
                    );

                this.updateState({
                    adapterDescription: updatedAdapter,
                    isGettingSample: false,
                    adapterSettingsChanged: false, // Reset the warning
                    adapterSettingsString: JSON.stringify(
                        updatedAdapter.config,
                    ),
                    transformationConfigurationChanged:
                        transformationConfigurationChanged,
                });

                if (scriptActive) {
                    this.runScript(updatedAdapter);
                }
            },
            error: (error: HttpErrorResponse) => {
                // Update state with error AND metadata (error/idle)
                this.updateState({
                    isGettingSample: false,
                    sampleError: error.error as SpLogMessage, // Assuming error.error is the SpLogMessage
                });
            },
        });
    }

    public runScript(adapter: AdapterDescription): void {
        // 1. Prepare state for loading
        this.updateState({
            isRunningScript: true,
            scriptError: null,
        });

        // 2. Update the local adapter object with the latest script from the UI
        const updatedAdapter = { ...adapter };
        updatedAdapter.transformationConfig.script = this.state().currentScript;
        updatedAdapter.transformationConfig.language =
            this.state().selectedScriptMetadata.language;

        // 3. Execute the API call
        this.restService.sampleTransform(updatedAdapter).subscribe({
            next: response => {
                // Update the outputs in the adapter object based on server results
                updatedAdapter.transformationConfig.outputs =
                    response.transformationConfig.outputs;

                this.updateState({
                    adapterDescription: updatedAdapter,
                    isRunningScript: false,
                });
            },
            error: (error: HttpErrorResponse) => {
                this.updateState({
                    isRunningScript: false,
                    scriptError: error.error as SpLogMessage,
                });
            },
        });
    }

    public openTransformationConfigurationChangedDialog(): void {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            disableClose: true,
            hasBackdrop: true,
            data: {
                title: this.translateService.instant(
                    'Event Transformation Configuration has changed',
                ),
                subtitle: this.translateService.instant(
                    'You changed the transformation for the events, therefore it might be necessary to reload the fields.' +
                        'Please only change nothing if you are certain that your changes do not affect the event schema.',
                ),
                cancelTitle: this.translateService.instant('Nothing changed'),
                okTitle: this.translateService.instant('Refresh Fields'),
                confirmAndCancel: true,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.getEventSchema(this.state().adapterDescription);
            } else {
                this.acknowledgeNoSchemaRefresh();
            }
        });
    }

    private acknowledgeNoSchemaRefresh() {
        this.updateState({
            adapterDescription: this.state().adapterDescription,
            transformationConfigurationChanged: false,
            transformationConfigurationString: JSON.stringify(
                this.state().adapterDescription.config,
            ),
        });
    }

    public getEventSchema(adapter: AdapterDescription): void {
        this.updateState({
            isGettingEventSchema: true,
            getEventSchemaError: null,
        });

        this.restService.getEventSchema(adapter).subscribe({
            next: schema => {
                this.sortEventPropertiesAlphabetically(schema);

                const updatedAdapter = { ...adapter };
                updatedAdapter.dataStream.eventSchema = schema;

                this.updateState({
                    adapterDescription: updatedAdapter,
                    isGettingEventSchema: false,
                    autoLoadSchema: false,
                    transformationConfigurationChanged: false,
                    transformationConfigurationString: JSON.stringify(
                        updatedAdapter.transformationConfig,
                    ),
                });

                this.updateEventPreview(updatedAdapter);
            },
            error: err =>
                this.updateState({
                    isGettingEventSchema: false,
                    getEventSchemaError: err.error,
                    autoLoadSchema: true,
                }),
        });
    }

    private sortEventPropertiesAlphabetically(eventSchema: EventSchema) {
        eventSchema.eventProperties.sort((a, b) => {
            return a.runtimeName < b.runtimeName ? -1 : 1;
        });
    }

    public updateEventPreview(adapter: AdapterDescription): void {
        this.updateState({ isPreviewLoading: true });
        this.restService.getAdapterEventPreview(adapter).subscribe(preview => {
            this.updateState({
                resultPreview: preview,
                isPreviewLoading: false,
            });
        });
    }

    public reset(): void {
        this._state.set({ ...this.initialState });
    }
}
