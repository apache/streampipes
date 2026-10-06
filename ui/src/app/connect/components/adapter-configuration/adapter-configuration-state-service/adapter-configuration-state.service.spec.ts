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

import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import {
    AdapterDescription,
    ConnectScriptLanguagesService,
    ScriptMetadata,
} from '@streampipes/platform-services';
import { Subject } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AdapterConfigurationStateService } from './adapter-configuration-state.service';
import { RestService } from '../../../services/rest.service';
import { EventSchemaDiffService } from '../../../services/event-schema-diff.service';

describe('Schema preview freshness', () => {
    let service: AdapterConfigurationStateService;
    let response: Subject<AdapterDescription>;
    let adapter: AdapterDescription;
    let sampleTransform: ReturnType<typeof vi.fn>;

    beforeEach(() => {
        response = new Subject<AdapterDescription>();
        sampleTransform = vi.fn(() => response);
        TestBed.configureTestingModule({
            providers: [
                AdapterConfigurationStateService,
                { provide: MatDialog, useValue: {} },
                { provide: TranslateService, useValue: {} },
                { provide: RestService, useValue: { sampleTransform } },
                { provide: ConnectScriptLanguagesService, useValue: {} },
                { provide: EventSchemaDiffService, useValue: {} },
            ],
        });
        service = TestBed.inject(AdapterConfigurationStateService);
        adapter = {
            transformationConfig: {
                scriptActive: true,
                script: 'original',
                language: 'javascript',
                inputs: [{ temperature: 43.747 }],
                outputs: [],
            },
        } as unknown as AdapterDescription;
        service.initializeCreateMode(adapter);
        service.updateState({
            currentScript: 'round',
            selectedScriptMetadata: {
                language: 'javascript',
                name: 'JavaScript',
            } as ScriptMetadata,
        });
    });

    function completeRun() {
        response.next({
            transformationConfig: { outputs: [{ temperature: 43.7 }] },
        } as unknown as AdapterDescription);
    }

    it('requires a successful run and becomes stale when code or language changes', () => {
        expect(service.previewOutdated()).toBe(true);
        service.runScript(adapter);
        completeRun();
        expect(service.previewOutdated()).toBe(false);
        service.updateCurrentScript('edited');
        expect(service.previewOutdated()).toBe(true);
        service.updateCurrentScript('round');
        expect(service.previewOutdated()).toBe(false);
        service.updateState({
            selectedScriptMetadata: { language: 'python' } as ScriptMetadata,
        });
        expect(service.previewOutdated()).toBe(true);
    });

    it('does not mark edits during an in-flight request as previewed', () => {
        service.runScript(adapter);
        service.updateCurrentScript('newer code');
        completeRun();
        expect(service.state().currentScript).toBe('newer code');
        expect(service.previewOutdated()).toBe(true);
    });

    it('preserves a newer input sample when an older response arrives', () => {
        service.runScript(adapter);
        const newer = {
            ...adapter,
            transformationConfig: {
                ...adapter.transformationConfig,
                inputs: [{ temperature: 99 }],
            },
        } as unknown as AdapterDescription;
        service.updateState({ adapterDescription: newer });
        completeRun();
        expect(
            service.state().adapterDescription.transformationConfig.inputs,
        ).toEqual([{ temperature: 99 }]);
        expect(service.previewOutdated()).toBe(true);
    });

    it('keeps failed runs stale and allows retry', () => {
        service.runScript(adapter);
        response.error({ error: { cause: 'Invalid script' } });
        expect(service.hasScriptPreview()).toBe(false);
        expect(service.state().isRunningScript).toBe(false);
        expect(service.state().scriptError).toEqual({
            cause: 'Invalid script',
        });
    });

    it('ignores duplicate runs and cancels pending work when the wizard closes', () => {
        service.runScript(adapter);
        service.runScript(adapter);
        expect(sampleTransform).toHaveBeenCalledTimes(1);
        service.reset();
        completeRun();
        expect(service.state().adapterDescription).toBeNull();
        expect(service.hasScriptPreview()).toBe(false);
    });

    it('does not replace pass-through output if transformation is disabled during a run', () => {
        service.runScript(adapter);
        service.updateState({
            adapterDescription: {
                ...adapter,
                transformationConfig: {
                    ...adapter.transformationConfig,
                    scriptActive: false,
                    outputs: adapter.transformationConfig.inputs,
                },
            },
        });
        completeRun();
        expect(
            service.state().adapterDescription.transformationConfig.outputs,
        ).toEqual(adapter.transformationConfig.inputs);
        expect(service.state().isRunningScript).toBe(false);
    });
});
