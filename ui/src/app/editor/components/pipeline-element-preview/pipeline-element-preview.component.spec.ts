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

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService } from '@ngx-translate/core';
import {
    LivePreviewService,
    PipelinePreviewModel,
} from '@streampipes/platform-services';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { PipelineElementPreviewComponent } from './pipeline-element-preview.component';

const ELEMENT_ID = 'org.example.processor:abc';
const PREVIEW_ELEMENT_ID = 'org.example.processor:xyz12';

function makePreview(elementErrors: Record<string, string> = {}) {
    return Object.assign(new PipelinePreviewModel(), {
        previewId: 'preview-1',
        elementIdMappings: { [ELEMENT_ID]: PREVIEW_ELEMENT_ID },
        elementErrors,
    });
}

describe('PipelineElementPreviewComponent', () => {
    let fixture: ComponentFixture<PipelineElementPreviewComponent>;
    let livePreviewService: LivePreviewService;

    const element = () => fixture.nativeElement as HTMLElement;
    const text = () => element().textContent.replace(/\s+/g, ' ').trim();
    const rows = () => Array.from(element().querySelectorAll('tr.preview-row'));

    function render(preview: PipelinePreviewModel) {
        fixture = TestBed.createComponent(PipelineElementPreviewComponent);
        fixture.componentRef.setInput('pipelinePreview', preview);
        fixture.componentRef.setInput('elementId', ELEMENT_ID);
        fixture.detectChanges();
    }

    beforeEach(() => {
        vi.useFakeTimers();
        TestBed.configureTestingModule({
            imports: [PipelineElementPreviewComponent],
            providers: [provideTranslateService()],
        });
        livePreviewService = TestBed.inject(LivePreviewService);
    });

    afterEach(() => {
        fixture?.destroy();
        vi.useRealTimers();
    });

    it('shows the start failure instead of waiting for data', () => {
        render(
            makePreview({
                [ELEMENT_ID]: 'No extensions service is available',
            }),
        );

        expect(element().querySelector('[role="alert"]')).not.toBeNull();
        expect(text()).toContain("This element couldn't be started");
        expect(text()).toContain('No extensions service is available');
        expect(text()).not.toContain('Waiting for live data');
    });

    it('renders the latest event of its own element only', () => {
        render(makePreview());

        livePreviewService.eventSub.next({
            [PREVIEW_ELEMENT_ID]: { temperature: 21.5, sensorId: 's1' },
            'other-element': { pressure: 3 },
        });
        fixture.detectChanges();

        expect(rows().map(r => r.textContent.replace(/\s+/g, ''))).toEqual([
            'sensorIds1',
            'temperature21.5',
        ]);
    });

    it('keeps the last values when an update does not contain its element', () => {
        render(makePreview());
        livePreviewService.eventSub.next({
            [PREVIEW_ELEMENT_ID]: { temperature: 21.5 },
        });
        fixture.detectChanges();

        livePreviewService.eventSub.next({ 'other-element': { pressure: 3 } });
        fixture.detectChanges();

        expect(text()).toContain('21.5');
    });

    it('reuses table rows across updates so values stay selectable', () => {
        render(makePreview());
        livePreviewService.eventSub.next({
            [PREVIEW_ELEMENT_ID]: { temperature: 21.5 },
        });
        fixture.detectChanges();
        const firstRow = rows()[0];

        livePreviewService.eventSub.next({
            [PREVIEW_ELEMENT_ID]: { temperature: 22 },
        });
        fixture.detectChanges();

        expect(rows()[0]).toBe(firstRow);
        expect(firstRow.textContent).toContain('22');
    });

    it('explains a missing output after a while', () => {
        render(makePreview());
        expect(text()).not.toContain('No events yet');

        vi.advanceTimersByTime(10000);
        fixture.detectChanges();

        expect(text()).toContain('No events yet');
    });
});
