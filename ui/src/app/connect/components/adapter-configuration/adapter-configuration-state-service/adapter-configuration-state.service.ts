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
    EventSchema,
    SpLogMessage,
} from '@streampipes/platform-services';
import { AdapterConfigurationState } from './AdapterConfigurationState';
import { HttpErrorResponse } from '@angular/common/http';
import { RestService } from '../../../services/rest.service';

@Injectable({
    providedIn: 'root',
})
export class AdapterConfigurationStateService {
    private restService = inject(RestService);

    private initialState: AdapterConfigurationState = {
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

    public initializeOrUpdateAdapter(adapter: AdapterDescription): void {
        this.updateState({ adapterDescription: adapter });
    }

    public updateAdapter(adapter: AdapterDescription): void {
        // Cloning is required to trigger all computed signals
        const clonedAdapter = this.cloneAdapter(adapter);
        this.updateState({
            adapterDescription: { ...clonedAdapter },
        });
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
                // 1. Mutate the data
                const updatedAdapter = { ...adapter };
                updatedAdapter.schemaTransformationConfig.inputs = [
                    sampleData.samples[0],
                ];

                // 2. Update state with new data AND metadata (success/idle)
                this.updateState({
                    adapterDescription: updatedAdapter,
                    isGettingSample: false,
                });

                // 3. Automatically run the script after getting the sample
                const currentScript =
                    updatedAdapter.schemaTransformationConfig.script;
                this.runScript(updatedAdapter, currentScript);
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

    public runScript(adapter: AdapterDescription, script: string): void {
        // 1. Prepare state for loading
        this.updateState({
            isRunningScript: true,
            scriptError: null,
        });

        // 2. Update the local adapter object with the latest script from the UI
        const updatedAdapter = { ...adapter };
        updatedAdapter.schemaTransformationConfig.script = script;
        updatedAdapter.schemaTransformationConfig.language = 'javascript';

        // 3. Execute the API call
        this.restService.sampleTransform(updatedAdapter).subscribe({
            next: response => {
                // Update the outputs in the adapter object based on server results
                updatedAdapter.schemaTransformationConfig.outputs =
                    response.schemaTransformationConfig.outputs;

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
