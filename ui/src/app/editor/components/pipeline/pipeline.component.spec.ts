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
import { DialogService, KeyboardShortcutService } from '@streampipes/shared-ui';
import { DataProcessorInvocation } from '@streampipes/platform-services';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PipelineComponent } from './pipeline.component';
import { PipelineEditorService } from '../../services/pipeline-editor.service';
import { JsplumbService } from '../../services/jsplumb.service';
import { JsplumbFactoryService } from '../../services/jsplumb-factory.service';
import { ObjectProvider } from '../../services/object-provider.service';
import { EditorService } from '../../services/editor.service';
import { PipelineStyleService } from '../../services/pipeline-style.service';
import { PipelineValidationService } from '../../services/pipeline-validation.service';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';

describe('Pipeline palette drops', () => {
    const create = vi.fn();
    beforeEach(() => {
        create.mockReset().mockReturnValue({
            type: 'stream',
            payload: { dom: 'new-stream' },
        });
        TestBed.configureTestingModule({
            providers: [
                ...[
                    ObjectProvider,
                    PipelineStyleService,
                    PipelineValidationService,
                    DialogService,
                    MatDialog,
                    KeyboardShortcutService,
                ].map(provide => ({ provide, useValue: {} })),
                {
                    provide: JsplumbService,
                    useValue: { createNewPipelineElementConfig: create },
                },
                {
                    provide: JsplumbFactoryService,
                    useValue: { destroy: vi.fn() },
                },
                {
                    provide: EditorService,
                    useValue: { makePipelineAssemblyEmpty: vi.fn() },
                },
                {
                    provide: IdGeneratorService,
                    useValue: { generate: () => 'unique' },
                },
            ],
        }).overrideComponent(PipelineComponent, {
            set: { template: '', imports: [] },
        });
    });

    function setup() {
        const fixture = TestBed.createComponent(PipelineComponent);
        const component = fixture.componentInstance;
        const element = Object.assign(new DataProcessorInvocation(), {
            elementId: 'processor',
        });
        component.allElements = [element];
        component.rawPipelineModel = [];
        component.JsplumbBridge = {
            repaintEverything: vi.fn(),
        } as unknown as PipelineComponent['JsplumbBridge'];
        vi.spyOn(component, 'checkTopicModel').mockImplementation(() => {});
        vi.spyOn(component, 'validatePipeline').mockImplementation(() => {});
        const canvas = fixture.nativeElement as HTMLElement;
        Object.defineProperty(canvas, 'offsetWidth', { value: 1000 });
        vi.spyOn(canvas, 'getBoundingClientRect').mockReturnValue(
            new DOMRect(100, 100, 500, 500),
        );
        vi.spyOn(canvas.parentElement, 'getBoundingClientRect').mockReturnValue(
            new DOMRect(100, 100, 400, 400),
        );
        component.initAssembly();
        const service = TestBed.inject(PipelineEditorService);
        return { fixture, component, element, service };
    }

    it('adds an element at canvas coordinates and updates the cache', () => {
        const { component, element, service } = setup();
        const cacheUpdate = vi.spyOn(
            component.triggerPipelineCacheUpdateEmitter,
            'emit',
        );
        service.paletteDrop$.next({
            element,
            bounds: new DOMRect(200, 250, 70, 70),
        });
        expect(create).toHaveBeenCalledWith(
            element,
            { x: 200, y: 300 },
            false,
            false,
            'processor:unique',
        );
        expect(component.rawPipelineModel).toHaveLength(1);
        expect(cacheUpdate).toHaveBeenCalledOnce();
    });

    it('ignores drops outside the visible canvas', () => {
        const { component, element, service } = setup();
        service.paletteDrop$.next({
            element,
            bounds: new DOMRect(450, 200, 70, 70),
        });
        expect(component.rawPipelineModel).toHaveLength(0);
        expect(create).not.toHaveBeenCalled();
    });

    it('ignores drops in read-only mode', () => {
        const { component, element, service } = setup();
        component.readonly = true;
        service.paletteDrop$.next({
            element,
            bounds: new DOMRect(200, 200, 70, 70),
        });
        expect(create).not.toHaveBeenCalled();
    });

    it('unsubscribes when the editor is destroyed', () => {
        const { fixture, element, service } = setup();
        fixture.destroy();
        service.paletteDrop$.next({
            element,
            bounds: new DOMRect(200, 200, 70, 70),
        });
        expect(create).not.toHaveBeenCalled();
    });
});
