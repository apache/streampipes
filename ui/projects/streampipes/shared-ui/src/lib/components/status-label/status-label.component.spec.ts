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
import { beforeEach, describe, expect, it } from 'vitest';
import { SpStatusLabelComponent, SpStatusTone } from './status-label.component';

describe('SpStatusLabelComponent', () => {
    let fixture: ComponentFixture<SpStatusLabelComponent>;

    function render(tone: SpStatusTone, showLabel: boolean): HTMLElement {
        fixture.componentRef.setInput('tone', tone);
        fixture.componentRef.setInput('label', 'Running');
        fixture.componentRef.setInput('showLabel', showLabel);
        fixture.detectChanges();
        return fixture.nativeElement;
    }

    beforeEach(() => {
        fixture = TestBed.createComponent(SpStatusLabelComponent);
    });

    it.each([
        ['success', 'light-green'],
        ['warning', 'light-yellow'],
        ['error', 'light-red'],
        ['neutral', 'light-neutral'],
    ] as [SpStatusTone, string][])(
        'shows the %s tone as %s',
        (tone, lightClass) => {
            const symbol = render(tone, false).querySelector('.light');

            expect(symbol.classList.contains(lightClass)).toBe(true);
        },
    );

    it('shows a coloured dot with the text', () => {
        const host = render('success', true);
        const text = host.querySelector('.status-text');

        expect(text.textContent.trim()).toBe('Running');
        expect(text.classList.contains('status-text--success')).toBe(true);
        expect(text.querySelector('.status-dot')).not.toBeNull();
        expect(host.querySelector('.light')).toBeNull();
        expect(host.querySelector('[role="img"]')).toBeNull();
    });

    it('names the symbol when the text is not shown', () => {
        const host = render('success', false);
        const symbol = host.querySelector('.light');

        expect(host.querySelector('.status-text')).toBeNull();
        expect(symbol.getAttribute('role')).toBe('img');
        expect(symbol.getAttribute('aria-label')).toBe('Running');
    });
});
