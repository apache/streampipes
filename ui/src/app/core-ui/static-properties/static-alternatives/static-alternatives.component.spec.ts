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

import '@angular/compiler';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideTranslateService } from '@ngx-translate/core';
import {
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
} from '@streampipes/platform-services';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { StaticAlternativesComponent } from './static-alternatives.component';

function group(name: string) {
    const property = new StaticPropertyGroup();
    property.internalName = name;
    property.label = name;
    property.staticProperties = [];
    return property;
}

function alternative(
    name: string,
    property: StaticPropertyGroup | null,
    selected: boolean,
) {
    const alternative = new StaticPropertyAlternative();
    alternative.internalName = name;
    alternative.elementId = name;
    alternative.label = name;
    alternative.staticProperty = property;
    alternative.selected = selected;
    return alternative;
}

function alternatives(...items: StaticPropertyAlternative[]) {
    const property = new StaticPropertyAlternatives();
    property.internalName = 'alternatives';
    property.label = 'alternatives';
    property.alternatives = items;
    return property;
}

describe('StaticAlternativesComponent completion', () => {
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [StaticAlternativesComponent],
            providers: [provideHttpClient(), provideTranslateService()],
        })
            .overrideComponent(StaticAlternativesComponent, {
                set: { template: '' },
            })
            .compileComponents();
    });

    afterEach(() => {
        vi.restoreAllMocks();
        TestBed.resetTestingModule();
    });

    function create(property: StaticPropertyAlternatives) {
        const fixture = TestBed.createComponent(StaticAlternativesComponent);
        const emitted: boolean[] = [];
        fixture.componentInstance.staticProperty = property;
        fixture.componentInstance.completedConfigurationsEmitter.subscribe(ev =>
            emitted.push(ev.configured),
        );
        fixture.componentInstance.ngOnInit();
        return { component: fixture.componentInstance, emitted };
    }

    it('does not let a completed inactive alternative make the selected one valid', () => {
        const property = alternatives(
            alternative('first', group('first_group'), true),
            alternative('second', group('second_group'), false),
        );
        const { component } = create(property);
        component.handleConfigurationUpdate({
            staticPropertyInternalName: 'first_group',
            configured: true,
        });
        expect(component.alternativeCompleted()).toBe(true);

        property.alternatives[0].selected = false;
        property.alternatives[1].selected = true;
        expect(component.alternativeCompleted()).toBe(false);
    });

    it('reports an unconfigured alternative as incomplete after switching from one without a property', () => {
        const property = alternatives(
            alternative('none', null, true),
            alternative('details', group('details_group'), false),
        );
        const { component, emitted } = create(property);
        expect(component.alternativeCompleted()).toBe(true);

        component.radioSelectionChange({ value: property.alternatives[1] });
        expect(emitted[emitted.length - 1]).toBe(false);

        component.handleConfigurationUpdate({
            staticPropertyInternalName: 'details_group',
            configured: true,
        });
        expect(emitted[emitted.length - 1]).toBe(true);

        component.radioSelectionChange({ value: property.alternatives[0] });
        expect(emitted[emitted.length - 1]).toBe(true);
    });
});
