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

import { Injectable, OnDestroy, computed, signal } from '@angular/core';
import {
    DataProcessorInvocation,
    EventPropertyUnion,
    PipelinePreviewModel,
    SemanticType,
    SpDataStream,
} from '@streampipes/platform-services';
import { PipelineElementConfig } from '../model/editor.model';

export type ElementPreviewStatus = 'live' | 'waiting' | 'error';

export interface PreviewField {
    runtimeName: string;
    timestamp: boolean;
}

export interface PreviewElement {
    /** Element id used in the editor (key of the preview's element id mappings). */
    elementId: string;
    name: string;
    type: PipelineElementConfig['type'];
    /** Output fields in schema order; empty for sinks. */
    fields: PreviewField[];
    /** Element ids of the elements connected to this element's input. */
    inputIds: string[];
    /** Key of this element in the preview result stream; undefined for sinks. */
    previewElementId?: string;
    error?: string;
}

export type PreviewEvent = Record<string, unknown>;

export interface PreviewHistoryEntry {
    receivedAt: number;
    event: PreviewEvent;
}

export interface ElementPreviewData {
    latest?: PreviewEvent;
    lastEventAt?: number;
    /** Most recent first. */
    history: PreviewHistoryEntry[];
}

export const PREVIEW_HISTORY_SIZE = 20;

/**
 * State of a running live preview: the previewed elements, the events received per element and
 * the selection shared by the canvas badges and the preview panel. Provided per drawing area.
 */
@Injectable()
export class LivePreviewStore implements OnDestroy {
    readonly elements = signal<PreviewElement[]>([]);
    readonly data = signal<Record<string, ElementPreviewData>>({});
    readonly selectedElementId = signal<string | undefined>(undefined);
    readonly paused = signal(false);
    readonly active = signal(false);
    /** Wall clock, updated every second while the preview is active. */
    readonly now = signal(Date.now());
    /** When the backend stops the preview at the latest; undefined if it reports no limit. */
    readonly expiresAt = signal<number | undefined>(undefined);
    /** Whole seconds until the preview stops; undefined without a limit. */
    readonly secondsLeft = computed(() => {
        const expiresAt = this.expiresAt();
        return expiresAt === undefined
            ? undefined
            : Math.max(0, Math.ceil((expiresAt - this.now()) / 1000));
    });

    readonly selectedElement = computed(() =>
        this.elements().find(e => e.elementId === this.selectedElementId()),
    );

    private clock: ReturnType<typeof setInterval>;

    start(
        preview: PipelinePreviewModel,
        rawPipelineModel: PipelineElementConfig[],
    ): void {
        const elements = toPreviewElements(preview, rawPipelineModel);
        const previousSelection = this.selectedElementId();
        this.elements.set(elements);
        this.data.set({});
        this.paused.set(false);
        this.active.set(true);
        this.expiresAt.set(
            preview.maxDurationMillis > 0
                ? Date.now() + preview.maxDurationMillis
                : undefined,
        );
        this.selectedElementId.set(
            elements.some(e => e.elementId === previousSelection)
                ? previousSelection
                : elements[0]?.elementId,
        );
        clearInterval(this.clock);
        this.now.set(Date.now());
        this.clock = setInterval(() => this.now.set(Date.now()), 1000);
    }

    stop(): void {
        clearInterval(this.clock);
        this.active.set(false);
        this.expiresAt.set(undefined);
        this.elements.set([]);
        this.data.set({});
        this.paused.set(false);
    }

    /** Applies one snapshot of the preview stream: the latest event per preview element id. */
    ingest(
        snapshot: Record<string, PreviewEvent>,
        receivedAt = Date.now(),
    ): void {
        if (!this.active() || this.paused() || !snapshot) {
            return;
        }
        const current = this.data();
        let next: Record<string, ElementPreviewData> | undefined;
        for (const element of this.elements()) {
            const event = element.previewElementId
                ? snapshot[element.previewElementId]
                : undefined;
            const previous = current[element.elementId];
            if (!event || sameEvent(previous?.latest, event)) {
                continue;
            }
            next ??= { ...current };
            next[element.elementId] = {
                latest: event,
                lastEventAt: receivedAt,
                history: [
                    { receivedAt, event },
                    ...(previous?.history ?? []),
                ].slice(0, PREVIEW_HISTORY_SIZE),
            };
        }
        if (next) {
            this.data.set(next);
        }
    }

    select(elementId: string): void {
        if (this.elements().some(e => e.elementId === elementId)) {
            this.selectedElementId.set(elementId);
        }
    }

    element(elementId: string): PreviewElement | undefined {
        return this.elements().find(e => e.elementId === elementId);
    }

    /** Undefined for elements without a preview of their own (sinks, elements not in the preview). */
    status(elementId: string): ElementPreviewStatus | undefined {
        const element = this.element(elementId);
        if (!element?.previewElementId) {
            return undefined;
        }
        if (element.error) {
            return 'error';
        }
        return this.data()[elementId]?.lastEventAt ? 'live' : 'waiting';
    }

    ngOnDestroy(): void {
        clearInterval(this.clock);
    }
}

function sameEvent(a: PreviewEvent | undefined, b: PreviewEvent): boolean {
    return a !== undefined && JSON.stringify(a) === JSON.stringify(b);
}

function toPreviewElements(
    preview: PipelinePreviewModel,
    rawPipelineModel: PipelineElementConfig[],
): PreviewElement[] {
    const configs = rawPipelineModel.filter(pe => !pe.settings.disabled);
    const elementIdByDom = new Map(
        configs.map(pe => [pe.payload.dom, pe.payload.elementId]),
    );
    const mappings = preview.elementIdMappings ?? {};
    const errors = preview.elementErrors ?? {};

    const elements = configs
        .map(pe => {
            const payload = pe.payload;
            const inputIds = (
                'connectedTo' in payload ? (payload.connectedTo ?? []) : []
            )
                .map(dom => elementIdByDom.get(dom))
                .filter((id): id is string => id !== undefined);
            return {
                elementId: payload.elementId,
                name: payload.name,
                type: pe.type,
                fields: outputFields(pe),
                inputIds,
                previewElementId: mappings[payload.elementId],
                error: errors[payload.elementId],
            } as PreviewElement;
        })
        // processors outside the preview (e.g. not connected) and unconnected sinks are left out
        .filter(e =>
            e.type === 'action'
                ? e.inputIds.length > 0
                : e.previewElementId !== undefined,
        );

    const depth = depthResolver(elements);
    return elements.sort(
        (a, b) => depth(a) - depth(b) || a.name.localeCompare(b.name),
    );
}

function outputFields(pe: PipelineElementConfig): PreviewField[] {
    let properties: EventPropertyUnion[] = [];
    if (pe.type === 'stream') {
        properties = (pe.payload as SpDataStream).eventSchema?.eventProperties;
    } else if (pe.type === 'sepa') {
        properties = (pe.payload as DataProcessorInvocation).outputStream
            ?.eventSchema?.eventProperties;
    }
    return (properties ?? []).map(p => ({
        runtimeName: p.runtimeName,
        timestamp: SemanticType.isTimestamp(p),
    }));
}

/** Distance from the pipeline's sources, so the list follows the data flow. */
function depthResolver(
    elements: PreviewElement[],
): (element: PreviewElement) => number {
    const byId = new Map(elements.map(e => [e.elementId, e]));
    const cache = new Map<string, number>();
    const resolve = (
        element: PreviewElement,
        visiting: Set<string>,
    ): number => {
        if (cache.has(element.elementId)) {
            return cache.get(element.elementId);
        }
        visiting.add(element.elementId);
        const inputs = element.inputIds
            .map(id => byId.get(id))
            .filter(e => e && !visiting.has(e.elementId));
        const value =
            inputs.length === 0
                ? 0
                : 1 + Math.max(...inputs.map(e => resolve(e, visiting)));
        visiting.delete(element.elementId);
        cache.set(element.elementId, value);
        return value;
    };
    return element => resolve(element, new Set());
}
