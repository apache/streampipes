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

import { Injectable, NgZone, OnDestroy, inject } from '@angular/core';
import { Subject } from 'rxjs';
import { DialogService } from '../dialog/base-dialog/base-dialog.service';

export interface ShortcutAction {
    key: string;
    ctrl?: boolean;
    shift?: boolean;
    alt?: boolean;
    action: (event: KeyboardEvent) => void;
    preventDefault?: boolean;
    allowInDialog?: boolean;
}

export interface SequenceAction {
    sequence: [string, string]; // e.g. ['g', 'p']
    action: (event: KeyboardEvent) => void;
    preventDefault?: boolean;
}

export type ShortcutRegistration = { unregister: () => void };

@Injectable({ providedIn: 'root' })
export class KeyboardShortcutService implements OnDestroy {
    private keydown$ = new Subject<KeyboardEvent>();
    private registrations: Map<string, ShortcutAction[]> = new Map();
    private sequenceRegistrations: Map<string, SequenceAction[]> = new Map();
    private pendingSequenceKey: string | null = null;
    private pendingSequenceTimeout: any = null;
    private readonly SEQUENCE_TIMEOUT_MS = 1000;

    private listener = (e: KeyboardEvent) => {
        const isInput = this.isInputFocused(e);
        const ctrl = e.ctrlKey || e.metaKey;

        if (!isInput || ctrl || e.key === 'Escape') {
            this.keydown$.next(e);
        }
    };

    private dialogService = inject(DialogService);

    constructor(private ngZone: NgZone) {
        this.ngZone.runOutsideAngular(() =>
            document.addEventListener('keydown', this.listener, true),
        );
        this.keydown$.subscribe(e =>
            this.ngZone.run(() => this.handleEvent(e)),
        );
    }

    ngOnDestroy(): void {
        document.removeEventListener('keydown', this.listener, true);
        this.keydown$.complete();
        this.clearPendingSequence();
    }

    register(id: string, actions: ShortcutAction[]): ShortcutRegistration {
        this.registrations.set(id, actions);
        return { unregister: () => this.unregister(id) };
    }

    registerSequences(
        id: string,
        actions: SequenceAction[],
    ): ShortcutRegistration {
        this.sequenceRegistrations.set(id, actions);
        return { unregister: () => this.unregister(id) };
    }

    unregister(id: string): void {
        this.registrations.delete(id);
        this.sequenceRegistrations.delete(id);
    }

    private handleEvent(event: KeyboardEvent): void {
        const key = event.key.toLowerCase();
        const ctrl = event.ctrlKey || event.metaKey;
        const shift = event.shiftKey;
        const alt = event.altKey;

        // 1. If a sequence is pending, THIS keystroke completes or cancels it —
        //    it never falls through to single-key matching.
        if (this.pendingSequenceKey) {
            const firstKey = this.pendingSequenceKey;
            this.clearPendingSequence();

            const seqMatch = Array.from(this.sequenceRegistrations.values())
                .flat()
                .find(a => a.sequence[0] === firstKey && a.sequence[1] === key);

            if (seqMatch) {
                if (seqMatch.preventDefault !== false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                if (!this.dialogService.hasOpenDialogs) {
                    seqMatch.action(event);
                }
                return;
            }
            // no match: fall through and evaluate this key normally (don't return)
        }

        // 2. Does this key start a known sequence (no modifiers held)?
        const startsSequence =
            !ctrl &&
            !shift &&
            !alt &&
            Array.from(this.sequenceRegistrations.values())
                .flat()
                .some(a => a.sequence[0] === key);

        if (startsSequence) {
            this.pendingSequenceKey = key;
            this.pendingSequenceTimeout = setTimeout(
                () => this.clearPendingSequence(),
                this.SEQUENCE_TIMEOUT_MS,
            );
            return; // don't evaluate single-key matches for the first key either
        }

        // 3. Existing single-key/combo matching (unchanged, but add alt check)
        const match = Array.from(this.registrations.values())
            .flat()
            .find(
                a =>
                    a.key.toLowerCase() === key &&
                    !!a.ctrl === ctrl &&
                    !!a.shift === shift &&
                    !!a.alt === alt,
            );

        if (match) {
            if (match.preventDefault !== false) {
                event.preventDefault();
                event.stopPropagation();
            }
            if (!this.dialogService.hasOpenDialogs || match.allowInDialog) {
                match.action(event);
            }
        }
    }

    private clearPendingSequence(): void {
        if (this.pendingSequenceTimeout) {
            clearTimeout(this.pendingSequenceTimeout);
        }
        this.pendingSequenceKey = null;
        this.pendingSequenceTimeout = null;
    }

    private isInputFocused = (event: KeyboardEvent): boolean => {
        const tag = (event.target as HTMLElement)?.tagName;
        return (
            ['INPUT', 'TEXTAREA', 'SELECT'].includes(tag) ||
            (event.target as HTMLElement)?.isContentEditable
        );
    };
}
