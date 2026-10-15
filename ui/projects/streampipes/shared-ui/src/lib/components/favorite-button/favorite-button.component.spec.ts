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

import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { SpFavoriteButtonComponent } from './favorite-button.component';

describe('SpFavoriteButtonComponent', () => {
    beforeEach(() =>
        TestBed.configureTestingModule({
            imports: [SpFavoriteButtonComponent],
        }),
    );

    it('exposes its state and accessible label and stops row navigation', () => {
        const fixture = TestBed.createComponent(SpFavoriteButtonComponent);
        fixture.componentRef.setInput('label', 'Remove from favorites');
        fixture.componentRef.setInput('selected', true);
        fixture.detectChanges();
        const button: HTMLButtonElement =
            fixture.nativeElement.querySelector('button');
        expect(button.getAttribute('aria-pressed')).toBe('true');
        expect(button.getAttribute('aria-label')).toBe('Remove from favorites');
        let toggles = 0;
        let rowClicks = 0;
        fixture.componentInstance.toggled.subscribe(() => toggles++);
        fixture.nativeElement.addEventListener('click', () => rowClicks++);
        button.click();
        expect(toggles).toBe(1);
        expect(rowClicks).toBe(0);
    });

    it('does not toggle while a request is pending', () => {
        const fixture = TestBed.createComponent(SpFavoriteButtonComponent);
        fixture.componentRef.setInput('label', 'Add to favorites');
        fixture.componentRef.setInput('disabled', true);
        fixture.detectChanges();
        let toggles = 0;
        fixture.componentInstance.toggled.subscribe(() => toggles++);
        const button: HTMLButtonElement =
            fixture.nativeElement.querySelector('button');
        button.click();
        expect(button.disabled).toBe(true);
        expect(toggles).toBe(0);
    });
});
