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

import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { KeyboardShortcutService, DialogService } from '@streampipes/shared-ui';

describe('KeyboardShortcutService', () => {
    let service: KeyboardShortcutService;
    let mockDialogService: any;

    beforeEach(() => {
        mockDialogService = {
            hasOpenDialogs: false,
        };

        TestBed.configureTestingModule({
            providers: [
                KeyboardShortcutService,
                { provide: DialogService, useValue: mockDialogService },
            ],
        });

        service = TestBed.inject(KeyboardShortcutService);
    });

    afterEach(() => {
        service.ngOnDestroy();
    });

    it('should fire single-key registered shortcut', () => {
        let fired = false;
        service.register('test', [
            {
                key: 'a',
                action: () => {
                    fired = true;
                },
            },
        ]);

        const event = new KeyboardEvent('keydown', { key: 'a' });
        document.dispatchEvent(event);

        expect(fired).toBe(true);
    });

    it('should match Alt combos', () => {
        let fired = false;
        service.register('test', [
            {
                key: 'b',
                alt: true,
                action: () => {
                    fired = true;
                },
            },
        ]);

        // Dispatch keydown b without alt
        document.dispatchEvent(
            new KeyboardEvent('keydown', { key: 'b', altKey: false }),
        );
        expect(fired).toBe(false);

        // Dispatch keydown b with alt
        document.dispatchEvent(
            new KeyboardEvent('keydown', { key: 'b', altKey: true }),
        );
        expect(fired).toBe(true);
    });

    it('should fire sequence action within timeout', fakeAsync(() => {
        let fired = false;
        service.registerSequences('test', [
            {
                sequence: ['g', 'p'],
                action: () => {
                    fired = true;
                },
            },
        ]);

        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'g' }));
        tick(500); // 500ms elapsed
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'p' }));

        expect(fired).toBe(true);
    }));

    it('should NOT fire sequence action if timeout elapsed', fakeAsync(() => {
        let fired = false;
        service.registerSequences('test', [
            {
                sequence: ['g', 'p'],
                action: () => {
                    fired = true;
                },
            },
        ]);

        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'g' }));
        tick(1001); // timeout elapsed
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'p' }));

        expect(fired).toBe(false);
    }));

    it('should give sequence precedence over single-key contextual shortcut', fakeAsync(() => {
        let seqFired = false;
        let singleFired = false;

        service.registerSequences('global', [
            {
                sequence: ['g', 'e'],
                action: () => {
                    seqFired = true;
                },
            },
        ]);

        service.register('contextual', [
            {
                key: 'e',
                action: () => {
                    singleFired = true;
                },
            },
        ]);

        // Press 'g'
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'g' }));
        tick(100);
        // Press 'e' within sequence timeout
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'e' }));

        expect(seqFired).toBe(true);
        expect(singleFired).toBe(false);
    }));

    it('should ignore shortcut events when input field is focused', () => {
        let fired = false;
        service.register('test', [
            {
                key: 's',
                action: () => {
                    fired = true;
                },
            },
        ]);

        // Create an input element and focus it
        const input = document.createElement('input');
        document.body.appendChild(input);
        input.focus();

        const event = new KeyboardEvent('keydown', {
            key: 's',
            bubbles: true,
        });
        Object.defineProperty(event, 'target', {
            value: input,
            enumerable: true,
        });

        input.dispatchEvent(event);

        expect(fired).toBe(false);

        // Cleanup
        document.body.removeChild(input);
    });

    it('should show shortcut hints after holding Shift', fakeAsync(() => {
        document.dispatchEvent(
            new KeyboardEvent('keydown', {
                key: 'Shift',
                shiftKey: true,
            }),
        );

        tick(199);
        expect(service.shortcutHintsVisible()).toBe(false);

        tick(1);
        expect(service.shortcutHintsVisible()).toBe(true);

        document.dispatchEvent(
            new KeyboardEvent('keyup', {
                key: 'Shift',
            }),
        );
        expect(service.shortcutHintsVisible()).toBe(false);
    }));

    it('should not show shortcut hints in editable fields', fakeAsync(() => {
        let fired = false;
        service.register('test', [
            {
                key: 'p',
                shift: true,
                action: () => {
                    fired = true;
                },
            },
        ]);

        const input = document.createElement('input');
        document.body.appendChild(input);
        input.focus();

        input.dispatchEvent(
            new KeyboardEvent('keydown', {
                key: 'Shift',
                shiftKey: true,
                bubbles: true,
            }),
        );
        input.dispatchEvent(
            new KeyboardEvent('keydown', {
                key: 'P',
                shiftKey: true,
                bubbles: true,
            }),
        );
        tick(200);

        expect(service.shortcutHintsVisible()).toBe(false);
        expect(fired).toBe(false);

        document.body.removeChild(input);
    }));

    it('should ignore shortcuts when a Monaco editor is focused', () => {
        let fired = false;
        service.register('test', [
            {
                key: 'a',
                shift: true,
                action: () => {
                    fired = true;
                },
            },
        ]);

        const monacoEditor = document.createElement('div');
        monacoEditor.classList.add('monaco-editor');
        const editorSurface = document.createElement('div');
        monacoEditor.appendChild(editorSurface);
        document.body.appendChild(monacoEditor);

        const event = new KeyboardEvent('keydown', {
            key: 'A',
            shiftKey: true,
            bubbles: true,
        });
        Object.defineProperty(event, 'target', {
            value: editorSurface,
            enumerable: true,
        });

        editorSurface.dispatchEvent(event);

        expect(fired).toBe(false);

        document.body.removeChild(monacoEditor);
    });

    it('should not show shortcut hints when a Monaco editor is focused', fakeAsync(() => {
        const monacoEditor = document.createElement('div');
        monacoEditor.classList.add('monaco-editor');
        const editorSurface = document.createElement('div');
        monacoEditor.appendChild(editorSurface);
        document.body.appendChild(monacoEditor);

        const shiftEvent = new KeyboardEvent('keydown', {
            key: 'Shift',
            shiftKey: true,
            bubbles: true,
        });
        Object.defineProperty(shiftEvent, 'target', {
            value: editorSurface,
            enumerable: true,
        });

        editorSurface.dispatchEvent(shiftEvent);
        tick(200);

        expect(service.shortcutHintsVisible()).toBe(false);

        document.body.removeChild(monacoEditor);
    }));

    it('should fire Shift shortcuts before hints appear', fakeAsync(() => {
        let fired = false;
        service.register('test', [
            {
                key: 'p',
                shift: true,
                action: () => {
                    fired = true;
                },
            },
        ]);

        document.dispatchEvent(
            new KeyboardEvent('keydown', {
                key: 'Shift',
                shiftKey: true,
            }),
        );
        tick(50);
        document.dispatchEvent(
            new KeyboardEvent('keydown', {
                key: 'P',
                shiftKey: true,
            }),
        );

        expect(fired).toBe(true);
        expect(service.shortcutHintsVisible()).toBe(false);
    }));
});
