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
import { UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideTranslateService } from '@ngx-translate/core';
import {
    FreeTextStaticProperty,
    RuntimeResolvableTreeInputStaticProperty,
    StaticPropertyAlternative,
    StaticPropertyAlternatives,
    StaticPropertyGroup,
} from '@streampipes/platform-services';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { StaticPropertyComponent } from './static-property.component';
import { StaticGroupComponent } from './static-group/static-group.component';
import { StaticAlternativesComponent } from './static-alternatives/static-alternatives.component';
import { StaticRuntimeResolvableTreeInputComponent } from './static-runtime-resolvable-tree-input/static-runtime-resolvable-tree-input.component';

function text(name: string, value: string) {
    const property = new FreeTextStaticProperty();
    property.internalName = name;
    property.label = name;
    property.value = value;
    property.requiredDatatype = 'http://www.w3.org/2001/XMLSchema#string';
    return property;
}

function group(name: string, children = []) {
    const property = new StaticPropertyGroup();
    property.internalName = name;
    property.label = name;
    property.staticProperties = children;
    return property;
}

function alternative(
    name: string,
    property: StaticPropertyGroup,
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

describe('Nested runtime configuration dependencies', () => {
    let loadOptions;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [StaticPropertyComponent],
            providers: [provideHttpClient(), provideTranslateService()],
        })
            .overrideComponent(StaticRuntimeResolvableTreeInputComponent, {
                set: { template: '' },
            })
            .compileComponents();
        loadOptions = vi
            .spyOn(
                StaticRuntimeResolvableTreeInputComponent.prototype,
                'loadOptionsFromRestApi',
            )
            .mockImplementation(() => {});
    });

    afterEach(() => {
        vi.restoreAllMocks();
        TestBed.resetTestingModule();
    });

    it('loads a tree inside an alternative and group only when parent and sibling dependencies are ready', () => {
        const tree = new RuntimeResolvableTreeInputStaticProperty();
        tree.internalName = 'browse_results';
        tree.label = 'Available tags';
        tree.dependsOn = ['plc_ip', 'browse_query'];
        tree.nodes = [];
        tree.selectedNodesInternalNames = [];
        const browse = group('browse_group', [text('browse_query', '*'), tree]);
        const tags = new StaticPropertyAlternatives();
        tags.internalName = 'tag_input';
        tags.label = 'Tag selection';
        tags.alternatives = [alternative('browse', browse, true)];
        const fixture = TestBed.createComponent(StaticPropertyComponent);
        const parent = {
            staticPropertyInternalName: 'plc_ip',
            configured: false,
            revision: 0,
        };
        fixture.componentRef.setInput('staticProperty', tags);
        fixture.componentRef.setInput('staticProperties', [tags]);
        fixture.componentRef.setInput('parentForm', new UntypedFormGroup({}));
        fixture.componentRef.setInput('fieldName', 'tags');
        fixture.componentRef.setInput('completedConfigurations', [parent]);
        fixture.detectChanges();
        expect(loadOptions).not.toHaveBeenCalled();

        fixture.componentRef.setInput('completedConfigurations', [
            { ...parent, configured: true, revision: 1 },
        ]);
        fixture.detectChanges();
        expect(loadOptions).toHaveBeenCalledTimes(1);

        // An unrelated container revision must not cause another request.
        fixture.componentRef.setInput('completedConfigurations', [
            { ...parent, configured: true, revision: 1 },
            {
                staticPropertyInternalName: 'tag_input',
                configured: false,
                revision: 1,
            },
        ]);
        fixture.detectChanges();
        expect(loadOptions).toHaveBeenCalledTimes(1);

        const groupComponent = fixture.debugElement.query(
            By.directive(StaticGroupComponent),
        ).componentInstance;
        groupComponent.handleConfigurationUpdate({
            staticPropertyInternalName: 'browse_query',
            configured: true,
        });
        fixture.detectChanges();
        expect(loadOptions).toHaveBeenCalledTimes(2);
        expect(parent.configured).toBe(false);
    });

    it('marks an empty required metadata group as configured', () => {
        const fixture = TestBed.createComponent(StaticGroupComponent);
        fixture.componentRef.setInput(
            'staticProperty',
            group('required_metadata'),
        );
        const completed = vi.fn();
        fixture.componentInstance.completedConfigurationsEmitter.subscribe(
            completed,
        );
        fixture.detectChanges();
        expect(completed).toHaveBeenCalledWith({
            staticPropertyInternalName: 'required_metadata',
            configured: true,
        });
    });

    it('does not let a completed inactive alternative make the selected one valid', () => {
        const fixture = TestBed.createComponent(StaticAlternativesComponent);
        const property = new StaticPropertyAlternatives();
        property.internalName = 'tag_input';
        property.alternatives = [
            alternative('manual', group('manual_group'), true),
            alternative('browse', group('browse_group'), false),
        ];
        fixture.componentInstance.staticProperty = property;
        fixture.componentInstance.ngOnInit();
        fixture.componentInstance.handleConfigurationUpdate({
            staticPropertyInternalName: 'manual_group',
            configured: true,
        });
        expect(fixture.componentInstance.alternativeCompleted()).toBe(true);
        property.alternatives[0].selected = false;
        property.alternatives[1].selected = true;
        expect(fixture.componentInstance.alternativeCompleted()).toBe(false);
    });
});
