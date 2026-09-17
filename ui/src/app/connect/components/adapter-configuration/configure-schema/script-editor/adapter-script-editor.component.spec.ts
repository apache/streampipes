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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { TranslateModule } from '@ngx-translate/core';
import { OverlayContainer } from '@angular/cdk/overlay';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AdapterScriptEditorComponent } from './adapter-script-editor.component';
import { EditorAutocompletionService } from '../../../../../services/editor-autocompletion.service';

describe('Script editor fullscreen', () => {
    let fixture: ComponentFixture<AdapterScriptEditorComponent>;
    let container: OverlayContainer;
    let originalParent: HTMLElement;
    let trigger: HTMLButtonElement;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AdapterScriptEditorComponent, TranslateModule.forRoot()],
            providers: [
                provideNoopAnimations(),
                { provide: EditorAutocompletionService, useValue: {} },
            ],
        })
            .overrideComponent(AdapterScriptEditorComponent, {
                set: {
                    template: '<textarea aria-label="Script"></textarea>',
                    imports: [],
                },
            })
            .compileComponents();
        fixture = TestBed.createComponent(AdapterScriptEditorComponent);
        container = TestBed.inject(OverlayContainer);
        fixture.detectChanges();
        originalParent = fixture.nativeElement.parentElement;
        trigger = document.createElement('button');
        document.body.appendChild(trigger);
        trigger.focus();
    });

    afterEach(() => {
        fixture.destroy();
        trigger.remove();
        container.ngOnDestroy();
    });

    it('moves the same editor into an overlay and restores it on Escape', () => {
        const editor = fixture.nativeElement.querySelector('textarea');
        editor.value = 'unsaved script';
        fixture.componentInstance.toggleFullscreen();
        expect(container.getContainerElement().contains(editor)).toBe(true);
        expect(fixture.componentInstance.fullscreen()).toBe(true);
        fixture.componentInstance.onEscape(
            new KeyboardEvent('keydown', { key: 'Escape', cancelable: true }),
        );
        expect(fixture.nativeElement.parentElement).toBe(originalParent);
        expect(fixture.nativeElement.querySelector('textarea')).toBe(editor);
        expect(editor.value).toBe('unsaved script');
        expect(document.activeElement).toBe(trigger);
    });

    it('keeps deactivated code visible and read-only without replacing its host', () => {
        const host = fixture.nativeElement;
        fixture.componentRef.setInput('script', 'unsaved script');
        fixture.componentRef.setInput('scriptActive', false);
        fixture.detectChanges();
        expect(fixture.nativeElement).toBe(host);
        expect(fixture.componentInstance.script()).toBe('unsaved script');
        expect(
            fixture.componentInstance.effectiveEditorOptions().readOnly,
        ).toBe(true);
        expect(
            fixture.componentInstance.effectiveEditorOptions().domReadOnly,
        ).toBe(true);
        fixture.componentRef.setInput('scriptActive', true);
        fixture.detectChanges();
        expect(
            fixture.componentInstance.effectiveEditorOptions().readOnly,
        ).toBeFalsy();
    });

    it('does not open fullscreen when transformation is disabled', () => {
        fixture.componentRef.setInput('scriptActive', false);
        fixture.detectChanges();
        fixture.componentInstance.toggleFullscreen();
        expect(fixture.componentInstance.fullscreen()).toBe(false);
        expect(
            container.getContainerElement().querySelector('.cdk-overlay-pane'),
        ).toBeNull();
    });

    it('uses the shared dialog header above the existing editor', () => {
        fixture.componentInstance.toggleFullscreen();
        const header = container
            .getContainerElement()
            .querySelector('.dialog-panel-header');
        expect(header?.textContent).toContain('Script editor');
        expect(header?.querySelector('button')).not.toBeNull();
    });

    it('allows Monaco to consume Escape before closing fullscreen', () => {
        fixture.componentInstance.toggleFullscreen();
        const event = new KeyboardEvent('keydown', {
            key: 'Escape',
            cancelable: true,
        });
        event.preventDefault();
        fixture.componentInstance.onEscape(event);
        expect(fixture.componentInstance.fullscreen()).toBe(true);
    });

    it('removes the overlay and scroll lock when destroyed while fullscreen', () => {
        fixture.componentInstance.toggleFullscreen();
        fixture.destroy();
        expect(
            container.getContainerElement().querySelector('.cdk-overlay-pane'),
        ).toBeNull();
        expect(
            document.documentElement.classList.contains(
                'cdk-global-scrollblock',
            ),
        ).toBe(false);
    });
});
