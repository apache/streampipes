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
import { TranslateService, provideTranslateService } from '@ngx-translate/core';
import { PipelinePreviewModel } from '@streampipes/platform-services';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { PipelineElementConfig } from '../../model/editor.model';
import { LivePreviewStore } from '../../services/live-preview.store';
import { LivePreviewPanelComponent } from './live-preview-panel.component';

const TIMESTAMP = 'http://schema.org/DateTime';
const schema = (...names: string[]) => ({
    eventProperties: [
        { runtimeName: 'timestamp', semanticType: TIMESTAMP },
        ...names.map(runtimeName => ({ runtimeName })),
    ],
});

function config(
    type: PipelineElementConfig['type'],
    elementId: string,
    name: string,
    payload: Record<string, unknown>,
): PipelineElementConfig {
    return {
        type,
        settings: { disabled: false },
        payload: { elementId, dom: `dom-${elementId}`, name, ...payload },
    } as unknown as PipelineElementConfig;
}

const pipeline = [
    config('stream', 'stream', 'Flow sensor', {
        eventSchema: schema('flag', 'temperature'),
    }),
    config('sepa', 'inverter', 'Boolean inverter', {
        connectedTo: ['dom-stream'],
        outputStream: { eventSchema: schema('flag', 'temperature') },
    }),
    config('sepa', 'ratelimit', 'Rate limit', {
        connectedTo: ['dom-inverter'],
        outputStream: { eventSchema: schema('flag', 'temperature') },
    }),
    config('action', 'sink', 'Data lake', { connectedTo: ['dom-inverter'] }),
];

describe('LivePreviewPanelComponent', () => {
    let fixture: ComponentFixture<LivePreviewPanelComponent>;
    let store: LivePreviewStore;

    const panel = () => fixture.nativeElement as HTMLElement;
    const text = () => panel().textContent.replace(/\s+/g, ' ');
    const detailRows = () =>
        Array.from(
            panel().querySelectorAll(
                '[data-cy="sp-live-preview-detail-table"] tbody tr',
            ),
        ).map(row =>
            Array.from(row.children).map(cell => cell.textContent.trim()),
        );

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [LivePreviewPanelComponent],
            providers: [provideTranslateService(), LivePreviewStore],
        });
        store = TestBed.inject(LivePreviewStore);
        store.start(
            Object.assign(new PipelinePreviewModel(), {
                previewId: 'p1',
                elementIdMappings: {
                    stream: 'stream',
                    inverter: 'inverter:a',
                    ratelimit: 'ratelimit:b',
                },
                elementErrors: { ratelimit: 'No extensions service available' },
                maxDurationMillis: 5 * 60 * 1000,
            }),
            pipeline,
        );
        fixture = TestBed.createComponent(LivePreviewPanelComponent);
        fixture.detectChanges();
    });

    afterEach(() => store.ngOnDestroy());

    function show(elementId: string) {
        store.select(elementId);
        fixture.detectChanges();
    }

    it('lists every previewed element with its status', () => {
        const rows = Array.from(
            panel().querySelectorAll('.live-preview-panel__row'),
        ).map(
            r =>
                r
                    .querySelector('.live-preview-panel__row-name')
                    .textContent.trim() +
                ' ' +
                r
                    .querySelector('.live-preview-panel__row-summary')
                    .textContent.trim(),
        );

        expect(rows).toEqual([
            'Flow sensor No events yet',
            'Boolean inverter No events yet',
            'Data lake Shows its input',
            "Rate limit Couldn't start",
        ]);
    });

    it('shows input and output of a processor and marks what it changed', () => {
        store.ingest({
            'stream': { timestamp: 1000, flag: true, temperature: 20.5 },
            'inverter:a': { timestamp: 1000, flag: false, temperature: 20.5 },
        });
        show('inverter');

        const rows = detailRows();
        expect(rows.map(r => r[0])).toEqual([
            'timestamp',
            'flag',
            'temperature',
        ]);
        expect(rows[1]).toEqual([
            'flag',
            'true',
            'falsechanged by this element',
        ]);
        expect(rows[2]).toEqual(['temperature', '20.5', '20.5']);
        const changed = panel().querySelectorAll(
            '.live-preview-panel__changed',
        );
        expect(Array.from(changed).map(c => c.textContent.trim())).toEqual([
            'false',
        ]);
        expect(text()).toContain(
            'Highlighted values were changed by this element.',
        );
    });

    it('matches input and output when the element renames the timestamp', () => {
        store.start(
            Object.assign(new PipelinePreviewModel(), {
                previewId: 'p2',
                elementIdMappings: { stream: 'stream', renamer: 'renamer:c' },
            }),
            [
                pipeline[0],
                config('sepa', 'renamer', 'Field renamer', {
                    connectedTo: ['dom-stream'],
                    outputStream: {
                        eventSchema: {
                            eventProperties: [
                                { runtimeName: 't', semanticType: TIMESTAMP },
                                { runtimeName: 'temperature' },
                            ],
                        },
                    },
                }),
            ],
        );
        store.ingest({
            'stream': { timestamp: 1000, flag: true, temperature: 20.5 },
            'renamer:c': { t: 1000, flag: true, temperature: 20.5 },
        });
        show('renamer');

        const changedFields = detailRows()
            .filter(row => row[2].endsWith('changed by this element'))
            .map(row => row[0]);
        expect(changedFields).toEqual(['t', 'timestamp']);
        expect(text()).toContain(
            'Highlighted values were changed by this element.',
        );
    });

    it('keeps showing the latest input when a filter drops events', () => {
        store.ingest(
            {
                'stream': { timestamp: 1000, flag: true, temperature: 20 },
                'inverter:a': { timestamp: 1000, flag: false, temperature: 20 },
            },
            1000,
        );
        store.ingest(
            { stream: { timestamp: 2000, flag: true, temperature: 21 } },
            2000,
        );
        store.ingest(
            { stream: { timestamp: 3000, flag: true, temperature: 22 } },
            3000,
        );
        store.now.set(4000);
        show('inverter');

        expect(detailRows()[2]).toEqual(['temperature', '22', '20']);
        expect(
            panel().querySelector('.live-preview-panel__changed'),
        ).toBeNull();
        expect(
            panel().querySelector('[data-cy="sp-live-preview-output-behind"]'),
        ).not.toBeNull();
    });

    it('still pairs input and output when the output arrives a moment later', () => {
        store.ingest(
            {
                'stream': { timestamp: 1000, flag: true, temperature: 20 },
                'inverter:a': { timestamp: 1000, flag: false, temperature: 20 },
            },
            1000,
        );
        store.ingest(
            { stream: { timestamp: 2000, flag: true, temperature: 21 } },
            2000,
        );
        store.ingest(
            {
                'stream': { timestamp: 2000, flag: true, temperature: 21 },
                'inverter:a': { timestamp: 2000, flag: false, temperature: 21 },
            },
            2500,
        );
        show('inverter');

        expect(detailRows()[2]).toEqual(['temperature', '21', '21']);
        expect(
            panel().querySelector('[data-cy="sp-live-preview-output-behind"]'),
        ).toBeNull();
        expect(text()).toContain(
            'Highlighted values were changed by this element.',
        );
    });

    it('does not compare input and output that belong to different events', () => {
        store.ingest({
            'stream': { timestamp: 2000, flag: true, temperature: 21 },
            'inverter:a': { timestamp: 1000, flag: false, temperature: 20.5 },
        });
        show('inverter');

        expect(
            panel().querySelector('.live-preview-panel__changed'),
        ).toBeNull();
        expect(text()).toContain('may not belong to the same event');
    });

    it('explains a failed element instead of showing a table', () => {
        show('ratelimit');

        const alert = panel().querySelector('[role="alert"]');
        expect(alert?.textContent).toContain('No extensions service available');
        expect(detailRows()).toEqual([]);
    });

    it('shows what a sink receives', () => {
        store.ingest({
            'inverter:a': { timestamp: 1000, flag: false, temperature: 20.5 },
        });
        show('sink');

        expect(text()).toContain('Sinks have no output');
        expect(detailRows()[1]).toEqual(['flag', 'false']);
    });

    it('shows how long the preview keeps running', () => {
        const translate = TestBed.inject(TranslateService);
        translate.setTranslation('en', {
            'Stops in {{time}}': 'Stops in {{time}}',
        });
        translate.use('en');
        fixture.detectChanges();

        expect(
            panel()
                .querySelector('[data-cy="sp-live-preview-time-left"]')
                ?.textContent.trim(),
        ).toBe('Stops in 5:00');
    });

    it('closes the preview from the panel', () => {
        const closed = vi.fn();
        fixture.componentInstance.closePanel.subscribe(closed);

        panel()
            .querySelector<HTMLButtonElement>(
                '[data-cy="sp-live-preview-close"]',
            )
            .click();

        expect(closed).toHaveBeenCalledOnce();
    });
});
