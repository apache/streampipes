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

import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import {
    AdapterDescription,
    SpLogMessage,
} from '@streampipes/platform-services';
import { AdapterProcessingState } from './AdapterProcessingState';
import { HttpErrorResponse } from '@angular/common/http';
import { RestService } from '../../../services/rest.service';

@Injectable({
    providedIn: 'root',
})
export class AdapterConfigurationStateService {
    private restService = inject(RestService);

    private initialState: AdapterProcessingState = {
        adapterDescription: null,
        isSaving: false,
        saveError: null,
        isGettingSample: false,
        sampleError: null,
        isRunningScript: false,
        scriptError: null,
    };
    private stateSubject = new BehaviorSubject<AdapterProcessingState>(
        this.initialState,
    );

    state$: Observable<AdapterProcessingState> =
        this.stateSubject.asObservable();

    // Helper function to update the state subject
    private updateState(newState: Partial<AdapterProcessingState>): void {
        const currentState = this.stateSubject.getValue();
        this.stateSubject.next({ ...currentState, ...newState });
    }

    public initializeOrUpdateAdapter(adapter: AdapterDescription): void {
        this.updateState({ adapterDescription: adapter });
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

    // adapter-configuration-store.service.ts

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
}
