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

import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Pipeline, PermissionsService } from '@streampipes/platform-services';
import {
    AssetSaveService,
    DialogService,
    KeyboardShortcutService,
    SpSplitButtonComponent,
} from '@streampipes/shared-ui';
import { PipelineAssemblyComponent } from './pipeline-assembly.component';
import { PipelineAssemblyOptionsComponent } from './pipeline-assembly-options/pipeline-assembly-options.component';
import { PipelineAssemblyDrawingAreaComponent } from './pipeline-assembly-drawing-area/pipeline-assembly-drawing-area.component';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import { PipelinePositioningService } from '../../services/pipeline-positioning.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import { ObjectProvider } from '../../services/object-provider.service';
import { EditorService } from '../../services/editor.service';
import { JsplumbService } from '../../services/jsplumb.service';
import { PipelineOperationsService } from '../../../pipelines/services/pipeline-operations.service';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';

describe('Pipeline editor workspace header', () => {
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [PipelineAssemblyComponent, TranslateModule.forRoot()],
            providers: [
                provideRouter([]),
                ...[
                    PipelinePositioningService,
                    ObjectProvider,
                    EditorService,
                    DialogService,
                    JsplumbService,
                    PermissionsService,
                    AssetSaveService,
                    PipelineOperationsService,
                    IdGeneratorService,
                ].map(provide => ({ provide, useValue: {} })),
                {
                    provide: PipelineValidationService,
                    useValue: { pipelineValid: true },
                },
                {
                    provide: KeyboardShortcutService,
                    useValue: { register: () => ({ unregister: () => {} }) },
                },
                {
                    provide: JsplumbFactoryService,
                    useValue: { getJsplumbBridge: () => undefined },
                },
            ],
        })
            .overrideComponent(PipelineAssemblyComponent, {
                remove: {
                    imports: [
                        PipelineAssemblyOptionsComponent,
                        PipelineAssemblyDrawingAreaComponent,
                    ],
                },
                add: { schemas: [NO_ERRORS_SCHEMA] },
            })
            .compileComponents();
    });

    function render(pipeline?: Pipeline, cloneMode = false) {
        const fixture = TestBed.createComponent(PipelineAssemblyComponent);
        fixture.componentInstance.originalPipeline = pipeline;
        fixture.componentInstance.cloneMode = cloneMode;
        fixture.componentInstance.rawPipelineModel = [];
        fixture.detectChanges();
        return fixture;
    }

    it('shows a new pipeline header and groups the tools and canvas inside the workspace', () => {
        const root: HTMLElement = render().nativeElement;
        expect(root.querySelector('h1')?.textContent).toContain('New Pipeline');
        expect(root.querySelector('[data-cy="options-pipeline"]')).toBeNull();
        expect(
            root.querySelector('sp-page-header sp-split-button'),
        ).not.toBeNull();
        expect(
            root.querySelector(
                '.workspace-container__toolbar sp-pipeline-assembly-options',
            ),
        ).not.toBeNull();
        expect(
            root.querySelector(
                '.workspace-container__content #outerAssemblyArea',
            ),
        ).not.toBeNull();
    });

    it('identifies existing and cloned pipelines', () => {
        const fixture = render({ name: 'Packaging line' } as Pipeline, true);
        expect(fixture.nativeElement.querySelector('h1').textContent).toContain(
            'Packaging line',
        );
        expect(
            fixture.nativeElement.querySelector('[pageMeta]').textContent,
        ).toContain('Clone');
        expect(
            fixture.nativeElement.querySelector('[data-cy="options-pipeline"]'),
        ).not.toBeNull();
    });

    it('preserves store-and-start and store-only save options', () => {
        const fixture = render();
        const submit = vi
            .spyOn(fixture.componentInstance, 'submit')
            .mockImplementation(() => {});
        const save = fixture.debugElement.query(
            By.directive(SpSplitButtonComponent),
        ).componentInstance as SpSplitButtonComponent;
        fixture.nativeElement.querySelector('.split-button__main').click();
        expect(submit).toHaveBeenLastCalledWith({
            startPipelineAfterStorage: true,
        });
        save.actionSelected.emit({
            label: 'Store',
            action: 'store',
            icon: 'save',
        });
        expect(submit).toHaveBeenLastCalledWith({
            startPipelineAfterStorage: false,
        });
    });

    it('disables saving when pipeline validation fails', () => {
        TestBed.inject(PipelineValidationService).pipelineValid = false;
        const fixture = render();
        const submit = vi
            .spyOn(fixture.componentInstance, 'submit')
            .mockImplementation(() => {});
        const button = fixture.nativeElement.querySelector(
            '.split-button__main',
        ) as HTMLButtonElement;
        expect(button.disabled).toBe(true);
        button.click();
        expect(submit).not.toHaveBeenCalled();
    });
});
