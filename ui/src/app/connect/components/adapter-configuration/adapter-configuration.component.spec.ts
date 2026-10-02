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
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {
    AdapterDescription,
    ConnectScriptLanguagesService,
    FreeTextStaticProperty,
    PipelineElementAssetService,
    PipelineElementTemplateService,
} from '@streampipes/platform-services';
import { DialogService } from '@streampipes/shared-ui';
import { NEVER, of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { AdapterTemplateService } from '../../services/adapter-template.service';
import { EventSchemaDiffService } from '../../services/event-schema-diff.service';
import { RestService } from '../../services/rest.service';
import { AdapterConfigurationComponent } from './adapter-configuration.component';
import { AdapterConfigurationStateService } from './adapter-configuration-state-service/adapter-configuration-state.service';
import { AdapterSettingsComponent } from './adapter-settings/adapter-settings.component';

describe('Adapter template configuration', () => {
    let component: AdapterConfigurationComponent;
    let settings: AdapterSettingsComponent;
    let state: AdapterConfigurationStateService;
    let getSampleEvents: ReturnType<typeof vi.fn>;
    let original: AdapterDescription;

    function adapterWithInterval(value: string): AdapterDescription {
        const interval = new FreeTextStaticProperty();
        interval.internalName = 'PULLING_INTERVAL';
        interval.value = value;
        return {
            config: [interval],
            dataStream: { eventSchema: { eventProperties: [] } },
            transformationConfig: { inputs: [], outputs: [] },
        } as AdapterDescription;
    }

    beforeEach(() => {
        getSampleEvents = vi.fn(() => NEVER);
        TestBed.configureTestingModule({
            providers: [
                { provide: Router, useValue: {} },
                { provide: ShepherdService, useValue: { trigger: vi.fn() } },
                { provide: TranslateService, useValue: {} },
                { provide: DialogService, useValue: {} },
                { provide: MatDialog, useValue: {} },
                { provide: PipelineElementAssetService, useValue: {} },
                { provide: ConnectScriptLanguagesService, useValue: {} },
                { provide: EventSchemaDiffService, useValue: {} },
                { provide: RestService, useValue: { getSampleEvents } },
                { provide: AdapterTemplateService, useValue: {} },
                {
                    provide: PipelineElementTemplateService,
                    useValue: { getPipelineElementTemplates: () => of([]) },
                },
            ],
        });
        state = TestBed.inject(AdapterConfigurationStateService);
        component = TestBed.runInInjectionContext(
            () => new AdapterConfigurationComponent(),
        );
        settings = TestBed.runInInjectionContext(
            () => new AdapterSettingsComponent(),
        );
        vi.spyOn(component, 'goForward').mockImplementation(() => {});
        original = adapterWithInterval(null);
        component.adapterDescription = original;
        state.initializeCreateMode(original);
        settings.adapterDescription = original;
        settings.ngOnInit();
        settings.updateAdapterDescriptionEmitter.subscribe(adapter =>
            component.updateAdapterDescription(adapter),
        );
    });

    it('requests sample data with the configuration restored from a template', () => {
        settings.afterTemplateReceived(adapterWithInterval('1000'));

        component.nextAdapterSettings();

        expect(getSampleEvents).toHaveBeenCalledOnce();
        const requested = getSampleEvents.mock.calls[0][0];
        expect(requested.config[0].value).toBe('1000');
        expect(state.state().adapterDescription.config).toEqual(
            requested.config,
        );
        expect(original.config[0]).toHaveProperty('value', null);
    });

    it('synchronizes the restored configuration when clearing a template', () => {
        (original.config[0] as FreeTextStaticProperty).value = '2000';
        settings.afterTemplateReceived(adapterWithInterval('1000'));

        settings.loadTemplate({ value: false });
        component.nextAdapterSettings();

        expect(getSampleEvents.mock.calls[0][0].config[0].value).toBe('2000');
        expect(settings.selectedTemplate).toBe(false);
    });
});
