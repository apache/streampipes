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

import {
    Component,
    ElementRef,
    computed,
    effect,
    inject,
    input,
    output,
    signal,
} from '@angular/core';
import { Clipboard } from '@angular/cdk/clipboard';
import { MatIconButton } from '@angular/material/button';
import {
    MatButtonToggle,
    MatButtonToggleGroup,
} from '@angular/material/button-toggle';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { SpSpinnerComponent } from '@streampipes/shared-ui';
import {
    LivePreviewStore,
    PreviewElement,
    PreviewEvent,
    PreviewField,
} from '../../services/live-preview.store';
import {
    formatPreviewValue,
    formatTimestamp,
    orderFields,
} from './live-preview-format';

export interface PreviewDetailRow {
    field: string;
    inputs: string[];
    output: string;
    changed: boolean;
}

export interface PreviewDetail {
    element: PreviewElement;
    inputs: PreviewElement[];
    rows: PreviewDetailRow[];
    showOutput: boolean;
    hasOutput: boolean;
    /** Whether the input column shows the very event the output was computed from. */
    inputMatchesOutput: boolean;
    /**
     * Whether the latest input arrived well after the latest output, e.g. because a filter
     * dropped it. The input column then shows the latest input, not the one behind the output.
     */
    outputBehind: boolean;
    /** Seconds since the shown output was emitted, when the output is behind. */
    outputAgeSeconds?: number;
}

const HEIGHT_STORAGE_KEY = 'sp-live-preview-panel-height';
const DEFAULT_HEIGHT = 288;
const MIN_HEIGHT = 160;
const KEYBOARD_RESIZE_STEP = 24;
/**
 * How much older than the latest input the latest output may be and still count as its result.
 * Covers processing time and the preview's sampling interval of 500 ms.
 */
const OUTPUT_BEHIND_MS = 1500;

@Component({
    selector: 'sp-live-preview-panel',
    templateUrl: './live-preview-panel.component.html',
    styleUrls: ['./live-preview-panel.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        LayoutGapDirective,
        MatButtonToggle,
        MatButtonToggleGroup,
        MatIcon,
        MatIconButton,
        MatTooltip,
        SpSpinnerComponent,
        TranslatePipe,
    ],
})
export class LivePreviewPanelComponent {
    readonly store = inject(LivePreviewStore);
    private host = inject<ElementRef<HTMLElement>>(ElementRef);
    private clipboard = inject(Clipboard);
    private snackBar = inject(MatSnackBar);
    private translateService = inject(TranslateService);

    starting = input(false);
    closePanel = output<void>();

    view = signal<'latest' | 'history'>('latest');
    height = signal(readStoredHeight());

    detail = computed(() => this.buildDetail());
    history = computed(() => this.buildHistory());

    constructor() {
        // a different element shows its latest event first
        effect(() => {
            this.store.selectedElementId();
            this.view.set('latest');
        });
    }

    rowSummary(element: PreviewElement): string {
        if (!element.previewElementId) {
            return this.translateService.instant('Shows its input');
        }
        switch (this.store.status(element.elementId)) {
            case 'error':
                return this.translateService.instant("Couldn't start");
            case 'waiting':
                return this.translateService.instant('No events yet');
            default:
                return this.translateService.instant(
                    'Last event {{seconds}} s ago',
                    { seconds: this.secondsSinceLastEvent(element) },
                );
        }
    }

    dotStatus(element: PreviewElement): string {
        return this.store.status(element.elementId) ?? 'input';
    }

    typeLabel(element: PreviewElement): string {
        switch (element.type) {
            case 'stream':
                return this.translateService.instant('Data stream');
            case 'sepa':
                return this.translateService.instant('Data processor');
            default:
                return this.translateService.instant('Data sink');
        }
    }

    togglePaused(): void {
        this.store.paused.update(paused => !paused);
    }

    copyAsJson(): void {
        const detail = this.detail();
        if (!detail) {
            return;
        }
        const data = this.store.data();
        const event = detail.showOutput
            ? data[detail.element.elementId]?.latest
            : data[detail.inputs[0]?.elementId]?.latest;
        if (event && this.clipboard.copy(JSON.stringify(event, null, 2))) {
            this.snackBar.open(
                this.translateService.instant('Event copied as JSON'),
                undefined,
                { duration: 2000 },
            );
        }
    }

    startResize(event: PointerEvent): void {
        const handle = event.currentTarget as HTMLElement;
        const startY = event.clientY;
        const startHeight = this.height();
        handle.setPointerCapture(event.pointerId);
        const move = (e: PointerEvent) =>
            this.setHeight(startHeight + (startY - e.clientY));
        const end = () => {
            handle.removeEventListener('pointermove', move);
            handle.removeEventListener('pointerup', end);
            handle.removeEventListener('pointercancel', end);
            storeHeight(this.height());
        };
        handle.addEventListener('pointermove', move);
        handle.addEventListener('pointerup', end);
        handle.addEventListener('pointercancel', end);
        event.preventDefault();
    }

    resizeWithKeyboard(event: KeyboardEvent): void {
        const step =
            event.key === 'ArrowUp'
                ? KEYBOARD_RESIZE_STEP
                : event.key === 'ArrowDown'
                  ? -KEYBOARD_RESIZE_STEP
                  : 0;
        if (step) {
            event.preventDefault();
            this.setHeight(this.height() + step);
            storeHeight(this.height());
        }
    }

    formatTimeLeft(seconds: number): string {
        return `${Math.floor(seconds / 60)}:${String(seconds % 60).padStart(2, '0')}`;
    }

    formatReceivedAt(receivedAt: number): string {
        return formatTimestamp(receivedAt);
    }

    private setHeight(height: number): void {
        const available =
            this.host.nativeElement.parentElement?.clientHeight ?? 0;
        const max = available > 0 ? available * 0.75 : height;
        this.height.set(
            Math.round(Math.max(MIN_HEIGHT, Math.min(max, height))),
        );
    }

    private secondsSinceLastEvent(element: PreviewElement): number {
        const lastEventAt = this.store.data()[element.elementId]?.lastEventAt;
        return lastEventAt
            ? Math.max(0, Math.round((this.store.now() - lastEventAt) / 1000))
            : 0;
    }

    private buildDetail(): PreviewDetail | undefined {
        const element = this.store.selectedElement();
        if (!element) {
            return undefined;
        }
        const data = this.store.data();
        const inputs = element.inputIds
            .map(id => this.store.element(id))
            .filter((e): e is PreviewElement => e !== undefined);
        const showOutput = element.type !== 'action';
        const outputData = showOutput ? data[element.elementId] : undefined;
        const output = outputData?.latest;
        const latestInputAt = Math.max(
            0,
            ...inputs.map(input => data[input.elementId]?.lastEventAt ?? 0),
        );
        const outputBehind =
            output !== undefined &&
            latestInputAt - outputData.lastEventAt > OUTPUT_BEHIND_MS;

        // While the output keeps up, prefer the input event it was computed from, identified by
        // its timestamp. Each side uses its own timestamp field, so a renamed timestamp matches.
        const outputTimestamp = element.fields.find(f => f.timestamp);
        const inputTimestamp = inputs[0]?.fields.find(f => f.timestamp);
        const matchedInput =
            !outputBehind &&
            inputs.length === 1 &&
            output &&
            outputTimestamp &&
            inputTimestamp
                ? data[inputs[0].elementId]?.history.find(
                      h =>
                          h.event[inputTimestamp.runtimeName] ===
                          output[outputTimestamp.runtimeName],
                  )?.event
                : undefined;
        const inputEvents: (PreviewEvent | undefined)[] = inputs.map(
            (input, i) =>
                i === 0 && matchedInput
                    ? matchedInput
                    : data[input.elementId]?.latest,
        );

        const schemaFields = mergeFields(element.fields, inputs);
        const fields = orderFields(schemaFields, [output, ...inputEvents]);
        const compare = showOutput && matchedInput !== undefined;
        const rows = fields.map(field => {
            const inputValues = inputEvents.map(e =>
                formatPreviewValue(e?.[field.runtimeName], field.timestamp),
            );
            const outputValue = formatPreviewValue(
                output?.[field.runtimeName],
                field.timestamp,
            );
            return {
                field: field.runtimeName,
                inputs: inputValues,
                output: outputValue,
                changed: compare && inputValues[0] !== outputValue,
            };
        });
        return {
            element,
            inputs,
            rows,
            showOutput,
            hasOutput: output !== undefined,
            inputMatchesOutput: matchedInput !== undefined,
            outputBehind,
            outputAgeSeconds: outputBehind
                ? Math.max(
                      0,
                      Math.round(
                          (this.store.now() - outputData.lastEventAt) / 1000,
                      ),
                  )
                : undefined,
        };
    }

    private buildHistory(): {
        fields: PreviewField[];
        rows: { receivedAt: number; values: string[] }[];
    } {
        const element = this.store.selectedElement();
        const entries = element
            ? (this.store.data()[element.elementId]?.history ?? [])
            : [];
        const fields = orderFields(
            element?.fields ?? [],
            entries.map(e => e.event),
        );
        return {
            fields,
            rows: entries.map(entry => ({
                receivedAt: entry.receivedAt,
                values: fields.map(f =>
                    formatPreviewValue(entry.event[f.runtimeName], f.timestamp),
                ),
            })),
        };
    }
}

function mergeFields(
    outputFields: PreviewField[],
    inputs: PreviewElement[],
): PreviewField[] {
    const merged = [...outputFields];
    const known = new Set(merged.map(f => f.runtimeName));
    inputs
        .flatMap(i => i.fields)
        .forEach(f => {
            if (!known.has(f.runtimeName)) {
                known.add(f.runtimeName);
                merged.push(f);
            }
        });
    return merged;
}

function readStoredHeight(): number {
    try {
        const stored = Number(localStorage.getItem(HEIGHT_STORAGE_KEY));
        return stored >= MIN_HEIGHT ? stored : DEFAULT_HEIGHT;
    } catch {
        return DEFAULT_HEIGHT;
    }
}

function storeHeight(height: number): void {
    try {
        localStorage.setItem(HEIGHT_STORAGE_KEY, String(height));
    } catch {
        // the panel works without a remembered height
    }
}
