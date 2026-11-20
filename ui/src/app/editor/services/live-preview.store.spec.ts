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

import { PipelinePreviewModel } from '@streampipes/platform-services';
import { afterEach, describe, expect, it } from 'vitest';
import { PipelineElementConfig } from '../model/editor.model';
import { LivePreviewStore, PREVIEW_HISTORY_SIZE } from './live-preview.store';

const TIMESTAMP = 'http://schema.org/DateTime';

function config(
    type: PipelineElementConfig['type'],
    elementId: string,
    name: string,
    payload: Record<string, unknown> = {},
): PipelineElementConfig {
    return {
        type,
        settings: { disabled: false },
        payload: { elementId, dom: `dom-${elementId}`, name, ...payload },
    } as unknown as PipelineElementConfig;
}

const stream = config('stream', 'stream', 'Flow sensor', {
    eventSchema: {
        eventProperties: [
            { runtimeName: 'timestamp', semanticType: TIMESTAMP },
            { runtimeName: 'temperature' },
        ],
    },
});
const filter = config('sepa', 'filter', 'Numerical filter', {
    connectedTo: ['dom-stream'],
    outputStream: {
        eventSchema: { eventProperties: [{ runtimeName: 'temperature' }] },
    },
});
const sink = config('action', 'sink', 'Data lake', {
    connectedTo: ['dom-filter'],
});
const unconnectedProcessor = config('sepa', 'loose', 'Unconnected');

function preview(elementErrors: Record<string, string> = {}) {
    return Object.assign(new PipelinePreviewModel(), {
        previewId: 'p1',
        elementIdMappings: { stream: 'stream', filter: 'filter:x1' },
        elementErrors,
    });
}

describe('LivePreviewStore', () => {
    let store: LivePreviewStore;

    afterEach(() => store?.ngOnDestroy());

    function started(errors: Record<string, string> = {}) {
        store = new LivePreviewStore();
        store.start(preview(errors), [
            sink,
            unconnectedProcessor,
            filter,
            stream,
        ]);
        return store;
    }

    it('lists previewed elements in data-flow order and leaves out unconnected ones', () => {
        started();

        expect(store.elements().map(e => e.name)).toEqual([
            'Flow sensor',
            'Numerical filter',
            'Data lake',
        ]);
        expect(store.element('filter').inputIds).toEqual(['stream']);
        expect(store.element('stream').fields).toEqual([
            { runtimeName: 'timestamp', timestamp: true },
            { runtimeName: 'temperature', timestamp: false },
        ]);
        expect(store.selectedElementId()).toBe('stream');
    });

    it('derives status from start errors and received events', () => {
        started({ filter: 'No extensions service available' });

        expect(store.status('stream')).toBe('waiting');
        store.ingest({ stream: { temperature: 20 } });

        expect(store.status('stream')).toBe('live');
        expect(store.status('filter')).toBe('error');
        expect(store.status('sink')).toBeUndefined();
    });

    it('records only new events and caps the history', () => {
        started();
        store.ingest({ stream: { temperature: 1 } }, 1000);
        store.ingest({ stream: { temperature: 1 } }, 1500);

        expect(store.data()['stream'].history).toHaveLength(1);
        expect(store.data()['stream'].lastEventAt).toBe(1000);

        for (let i = 2; i < PREVIEW_HISTORY_SIZE + 5; i++) {
            store.ingest({ stream: { temperature: i } }, i * 1000);
        }
        const history = store.data()['stream'].history;
        expect(history).toHaveLength(PREVIEW_HISTORY_SIZE);
        expect(history[0].event).toEqual({
            temperature: PREVIEW_HISTORY_SIZE + 4,
        });
    });

    it('maps preview element ids back to editor element ids', () => {
        started();
        store.ingest({ 'filter:x1': { temperature: 42 } });

        expect(store.data()['filter'].latest).toEqual({ temperature: 42 });
    });

    it('ignores events while paused', () => {
        started();
        store.paused.set(true);
        store.ingest({ stream: { temperature: 1 } });

        expect(store.data()['stream']).toBeUndefined();
    });

    it('keeps the selection across a restart when the element still exists', () => {
        started();
        store.select('filter');
        store.start(preview(), [stream, filter, sink]);

        expect(store.selectedElementId()).toBe('filter');
    });

    it('counts down to the maximum duration the backend reports', () => {
        store = new LivePreviewStore();
        store.start(
            Object.assign(preview(), { maxDurationMillis: 5 * 60 * 1000 }),
            [stream],
        );

        expect(store.secondsLeft()).toBe(300);
        store.now.set(store.expiresAt() - 61_500);
        expect(store.secondsLeft()).toBe(62);
        store.now.set(store.expiresAt() + 1000);
        expect(store.secondsLeft()).toBe(0);
    });

    it('shows no countdown when the backend reports no maximum duration', () => {
        started();

        expect(store.secondsLeft()).toBeUndefined();
    });

    it('clears everything when stopped', () => {
        started();
        store.ingest({ stream: { temperature: 1 } });
        store.stop();

        expect(store.active()).toBe(false);
        expect(store.elements()).toEqual([]);
        expect(store.status('stream')).toBeUndefined();
    });
});
