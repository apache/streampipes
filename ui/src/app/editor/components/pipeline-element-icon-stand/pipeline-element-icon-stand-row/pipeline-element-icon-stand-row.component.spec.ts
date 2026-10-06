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

import { Component, Input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { CdkDropList } from '@angular/cdk/drag-drop';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import {
    PipelineElementComponent,
    SpAssetBrowserService,
    SpTableAssetContextService,
} from '@streampipes/shared-ui';
import { DataProcessorInvocation } from '@streampipes/platform-services';
import { PipelineElementIconStandRowComponent } from './pipeline-element-icon-stand-row.component';
import { PipelineEditorService } from '../../../services/pipeline-editor.service';
import { EditorService } from '../../../services/editor.service';

@Component({ selector: 'sp-pipeline-element', template: '' })
class IconStubComponent {
    @Input() pipelineElement: unknown;
    @Input() iconStandSize: boolean;
}

@Component({
    imports: [CdkDropList, PipelineElementIconStandRowComponent],
    template: `<div cdkDropList [cdkDropListSortingDisabled]="true">
        <sp-pe-icon-stand-row [element]="element" />
    </div>`,
})
class PaletteHostComponent {
    element = Object.assign(new DataProcessorInvocation(), {
        name: 'Processor',
        elementId: 'processor',
        appId: 'processor',
    });
}

describe('Pipeline palette dragging without jQuery', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [PaletteHostComponent],
            providers: [
                EditorService,
                SpAssetBrowserService,
                SpTableAssetContextService,
            ].map(provide => ({ provide, useValue: {} })),
        }).overrideComponent(PipelineElementIconStandRowComponent, {
            remove: { imports: [PipelineElementComponent] },
            add: { imports: [IconStubComponent] },
        });
    });

    it('emits the icon bounds on release and keeps the palette item reusable', async () => {
        const fixture = TestBed.createComponent(PaletteHostComponent);
        fixture.detectChanges();
        await fixture.whenStable();
        const service = TestBed.inject(PipelineEditorService);
        const dropped = vi.fn();
        service.paletteDrop$.subscribe(dropped);
        const row = fixture.nativeElement.querySelector(
            '.draggable-pipeline-element',
        ) as HTMLElement;
        vi.spyOn(row, 'getBoundingClientRect').mockReturnValue(
            new DOMRect(0, 0, 200, 40),
        );
        for (let count = 1; count <= 2; count++) {
            row.dispatchEvent(
                new MouseEvent('mousedown', {
                    detail: 1,
                    bubbles: true,
                    button: 0,
                    buttons: 1,
                    clientX: 10,
                    clientY: 10,
                }),
            );
            document.dispatchEvent(
                new MouseEvent('mousemove', {
                    bubbles: true,
                    buttons: 1,
                    clientX: 30,
                    clientY: 30,
                }),
            );
            document.dispatchEvent(
                new MouseEvent('mousemove', {
                    bubbles: true,
                    buttons: 1,
                    clientX: 300,
                    clientY: 200,
                }),
            );
            const preview = document.querySelector(
                '.pipeline-element-drag-preview',
            );
            expect(preview).not.toBeNull();
            expect(service.dragging()).toBe(true);
            const bounds = new DOMRect(300, 200, 70, 70);
            vi.spyOn(preview, 'getBoundingClientRect').mockReturnValue(bounds);
            document.dispatchEvent(
                new MouseEvent('mouseup', {
                    bubbles: true,
                    clientX: 300,
                    clientY: 200,
                }),
            );
            await fixture.whenStable();
            expect(dropped).toHaveBeenCalledTimes(count);
            expect(dropped).toHaveBeenLastCalledWith({
                element: fixture.componentInstance.element,
                bounds,
            });
            expect(service.dragging()).toBe(false);
            expect(
                document.querySelector('.pipeline-element-drag-preview'),
            ).toBeNull();
            expect(fixture.nativeElement.contains(row)).toBe(true);
        }
    });
});
