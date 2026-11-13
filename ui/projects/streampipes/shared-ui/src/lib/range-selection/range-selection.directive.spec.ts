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

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { SpRangeSelectionDirective } from './range-selection.directive';

@Component({
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [FormsModule, MatCheckbox, SpRangeSelectionDirective],
    template: `
        <div [spRangeSelection]="context">
            <input class="search" />
            <mat-checkbox class="header">Select all</mat-checkbox>
            @for (row of rows; track row.id) {
                <mat-checkbox
                    spRangeSelectionItem
                    [(ngModel)]="row.selected"
                    [disabled]="row.disabled"
                    (change)="changes.push(row.id)"
                    (click)="$event.stopPropagation()"
                    >{{ row.id }}</mat-checkbox
                >
            }
            <button (click)="clear()">Clear</button>
            <div spRangeSelection>
                <mat-checkbox spRangeSelectionItem>Nested group</mat-checkbox>
            </div>
        </div>
    `,
})
class RangeSelectionTestHostComponent {
    context = '';
    changes: number[] = [];
    rows = Array.from({ length: 8 }, (_, id) => ({
        id,
        selected: false,
        disabled: false,
    }));
    clear() {
        this.rows.forEach(row => (row.selected = false));
    }
}

describe('SpRangeSelectionDirective', () => {
    let fixture: ComponentFixture<RangeSelectionTestHostComponent>;
    let host: RangeSelectionTestHostComponent;
    const inputs = (): HTMLInputElement[] =>
        Array.from(
            fixture.nativeElement.querySelectorAll(
                'mat-checkbox[spRangeSelectionItem] input',
            ),
        );
    const selected = () =>
        host.rows.filter(row => row.selected).map(row => row.id);

    async function click(index: number, shiftKey = false) {
        inputs()[index].dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                shiftKey,
            }),
        );
        await fixture.whenStable();
        fixture.detectChanges();
    }

    function hover(index: number, shiftKey = true) {
        inputs()[index].dispatchEvent(
            new MouseEvent('mouseover', {
                bubbles: true,
                shiftKey,
            }),
        );
        fixture.detectChanges();
    }

    function pressKey(
        index: number,
        key: string,
        shiftKey = false,
        repeat = false,
    ) {
        const event = new KeyboardEvent('keydown', {
            key,
            shiftKey,
            repeat,
            bubbles: true,
            cancelable: true,
        });
        inputs()[index].dispatchEvent(event);
        fixture.detectChanges();
        return event;
    }

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [RangeSelectionTestHostComponent],
        }).compileComponents();
        fixture = TestBed.createComponent(RangeSelectionTestHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
        await fixture.whenStable();
    });

    afterEach(() => TestBed.resetTestingModule());

    it('selects an inclusive range and preserves selections outside it', async () => {
        await click(0);
        await click(2);
        await click(6, true);
        expect(selected()).toEqual([0, 2, 3, 4, 5, 6]);
        expect(host.changes).toEqual([0, 2, 3, 4, 5, 6]);
    });

    it('keeps the anchor fixed across repeated Shift clicks in either direction', async () => {
        await click(3);
        await click(5, true);
        await click(1, true);
        expect(selected()).toEqual([1, 2, 3, 4, 5]);
    });

    it('selects on Shift hover without toggling the endpoint on a subsequent click', async () => {
        await click(1);
        hover(7);
        expect(selected()).toEqual([1, 2, 3, 4, 5, 6, 7]);
        await click(7, true);
        expect(selected()).toEqual([1, 2, 3, 4, 5, 6, 7]);
    });

    it('selects when Shift is pressed while already pointing at a checkbox', async () => {
        await click(1);
        hover(5, false);
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Shift' }));
        fixture.detectChanges();
        expect(selected()).toEqual([1, 2, 3, 4, 5]);
    });

    it('supports Shift clicking checkbox labels without duplicate activation', async () => {
        await click(1);
        const label = inputs()[5]
            .closest('mat-checkbox')
            .querySelector('label');
        label.dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                shiftKey: true,
            }),
        );
        await fixture.whenStable();
        fixture.detectChanges();
        expect(selected()).toEqual([1, 2, 3, 4, 5]);
        expect(host.changes).toEqual([1, 2, 3, 4, 5]);
    });

    it('sets an anchor when activating the larger Material touch target', async () => {
        const target = inputs()[1]
            .closest('mat-checkbox')
            .querySelector<HTMLElement>('.mat-mdc-checkbox-touch-target');
        target.click();
        await fixture.whenStable();
        fixture.detectChanges();
        await click(5, true);
        expect(selected()).toEqual([1, 2, 3, 4, 5]);
    });

    it('supports Shift Space on a focused checkbox', async () => {
        await click(1);
        inputs()[5].dispatchEvent(
            new KeyboardEvent('keydown', {
                key: ' ',
                shiftKey: true,
                bubbles: true,
                cancelable: true,
            }),
        );
        fixture.detectChanges();
        expect(selected()).toEqual([1, 2, 3, 4, 5]);
    });

    it('toggles with Space, sets the anchor and suppresses a second keyup activation', () => {
        inputs()[1].focus();
        expect(pressKey(1, ' ').defaultPrevented).toBe(true);
        expect(selected()).toEqual([1]);

        const keyUp = new KeyboardEvent('keyup', {
            key: ' ',
            bubbles: true,
            cancelable: true,
        });
        inputs()[1].dispatchEvent(keyUp);
        expect(keyUp.defaultPrevented).toBe(true);
        expect(host.changes).toEqual([1]);

        pressKey(1, 'ArrowDown');
        pressKey(2, 'ArrowDown', true);
        expect(document.activeElement).toBe(inputs()[3]);
        expect(selected()).toEqual([1, 2, 3]);
    });

    it('moves focus with all four arrow keys without changing selection', () => {
        inputs()[2].focus();
        for (const [index, key, target] of [
            [2, 'ArrowDown', 3],
            [3, 'ArrowRight', 4],
            [4, 'ArrowUp', 3],
            [3, 'ArrowLeft', 2],
        ] as const) {
            expect(pressKey(index, key).defaultPrevented).toBe(true);
            expect(document.activeElement).toBe(inputs()[target]);
        }
        expect(selected()).toEqual([]);
    });

    it('skips disabled checkboxes and stops at the current group boundaries', () => {
        host.rows[2].disabled = true;
        fixture.detectChanges();
        inputs()[1].focus();
        pressKey(1, 'ArrowDown');
        expect(document.activeElement).toBe(inputs()[3]);
        pressKey(3, 'ArrowUp');
        expect(document.activeElement).toBe(inputs()[1]);

        inputs()[0].focus();
        pressKey(0, 'ArrowUp');
        expect(document.activeElement).toBe(inputs()[0]);
        inputs()[7].focus();
        pressKey(7, 'ArrowDown');
        expect(document.activeElement).toBe(inputs()[7]);
    });

    it('keeps the anchor fixed when extending a range with Shift and arrows', () => {
        inputs()[3].focus();
        pressKey(3, ' ');
        pressKey(3, 'ArrowDown', true);
        pressKey(4, 'ArrowRight', true);
        pressKey(5, 'ArrowUp', true);
        pressKey(4, 'ArrowLeft', true);
        pressKey(3, 'ArrowUp', true);
        expect(document.activeElement).toBe(inputs()[2]);
        expect(selected()).toEqual([2, 3, 4, 5]);
    });

    it('deselects a keyboard range from an unchecked anchor', () => {
        inputs()[1].focus();
        pressKey(1, ' ');
        pressKey(1, 'ArrowDown', true);
        pressKey(2, 'ArrowDown', true);
        expect(selected()).toEqual([1, 2, 3]);
        pressKey(3, ' ');
        pressKey(3, 'ArrowUp', true);
        pressKey(2, 'ArrowUp', true);
        expect(selected()).toEqual([]);
    });

    it('moves focus without selecting when Shift arrows have no anchor', () => {
        inputs()[1].focus();
        pressKey(1, 'ArrowDown', true);
        expect(document.activeElement).toBe(inputs()[2]);
        expect(selected()).toEqual([]);
        pressKey(2, ' ', true);
        expect(selected()).toEqual([2]);
    });

    it('ignores repeated Space keydowns', () => {
        inputs()[1].focus();
        pressKey(1, ' ');
        pressKey(1, ' ', false, true);
        expect(selected()).toEqual([1]);
        expect(host.changes).toEqual([1]);
    });

    it('leaves keyboard behavior of search fields and header checkboxes unchanged', () => {
        for (const element of [
            fixture.nativeElement.querySelector('.search'),
            fixture.nativeElement.querySelector('.header input'),
        ] as HTMLInputElement[]) {
            element.focus();
            for (const key of ['ArrowDown', ' ']) {
                const event = new KeyboardEvent('keydown', {
                    key,
                    bubbles: true,
                    cancelable: true,
                });
                element.dispatchEvent(event);
                expect(event.defaultPrevented).toBe(false);
                expect(document.activeElement).toBe(element);
            }
        }
    });

    it('deselects a range using an unchecked anchor and skips disabled items', async () => {
        host.rows.forEach(row => (row.selected = true));
        host.rows[4].disabled = true;
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();
        await click(6);
        await click(2, true);
        expect(selected()).toEqual([0, 1, 4, 7]);
    });

    it('treats Shift click without an anchor as an ordinary click', async () => {
        await click(3, true);
        expect(selected()).toEqual([3]);
    });

    it('resets the anchor when filtering removes rows', async () => {
        await click(1);
        host.rows = host.rows.slice(1);
        fixture.detectChanges();
        await click(4, true);
        expect(selected()).toEqual([1, 5]);
    });

    it('resets the anchor when sorting reorders the same checkbox instances', async () => {
        await click(1);
        host.rows.reverse();
        fixture.detectChanges();
        await click(0, true);
        expect(selected()).toEqual([7, 1]);
    });

    it('resets the anchor when paging replaces the visible rows', async () => {
        await click(1);
        host.rows = host.rows.map(row => ({
            ...row,
            id: row.id + 10,
            selected: false,
        }));
        fixture.detectChanges();
        await click(5, true);
        expect(selected()).toEqual([15]);
    });

    it('resets the anchor when the list context changes without DOM changes', async () => {
        await click(1);
        host.context = 'new search';
        fixture.detectChanges();
        await click(5, true);
        expect(selected()).toEqual([1, 5]);
    });

    it('resets the anchor after bulk clearing', async () => {
        await click(1);
        (
            fixture.nativeElement.querySelector('button') as HTMLButtonElement
        ).click();
        fixture.detectChanges();
        await click(5, true);
        expect(selected()).toEqual([5]);
    });

    it('ignores header checkboxes and checkboxes in nested groups', async () => {
        await click(1);
        await click(8, true);
        expect(selected()).toEqual([1]);
        expect(inputs()[8].checked).toBe(true);
        (
            fixture.nativeElement.querySelector(
                '.header input',
            ) as HTMLInputElement
        ).click();
        await click(5, true);
        expect(selected()).toEqual([1, 5]);
    });
});
