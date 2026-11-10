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
    AfterViewInit,
    Directive,
    ElementRef,
    HostListener,
    inject,
    Input,
    NgZone,
    OnChanges,
    OnDestroy,
} from '@angular/core';

/**
 * Adds range selection to explicitly marked Material checkboxes, including
 * checkboxes rendered by child components. Native activation keeps change
 * handlers, forms and accessibility behavior identical to individual clicks.
 */
@Directive({
    selector: '[spRangeSelection]',
    host: { 'data-sp-range-selection': '' },
})
export class SpRangeSelectionDirective
    implements AfterViewInit, OnChanges, OnDestroy
{
    @Input() spRangeSelection: unknown;

    private readonly host =
        inject<ElementRef<HTMLElement>>(ElementRef).nativeElement;
    private readonly ngZone = inject(NgZone);
    private inputs: HTMLInputElement[] = [];
    private anchor?: HTMLInputElement;
    private anchorChecked = false;
    private pointed?: HTMLInputElement;
    private applyingRange = false;
    private destroyed = false;
    private observer?: MutationObserver;

    ngAfterViewInit(): void {
        this.host.addEventListener('click', this.onClick, true);
        this.ngZone.runOutsideAngular(() => {
            this.observer = new MutationObserver(() => this.syncInputs());
            this.observer.observe(this.host, {
                childList: true,
                subtree: true,
            });
        });
        this.syncInputs();
    }

    ngOnChanges(): void {
        this.resetAnchor();
    }

    ngOnDestroy(): void {
        this.destroyed = true;
        this.observer?.disconnect();
        this.host.removeEventListener('click', this.onClick, true);
    }

    @HostListener('mouseover', ['$event'])
    onMouseOver(event: MouseEvent): void {
        this.syncInputs();
        this.pointed = this.inputAt(event.target);
        if (event.shiftKey && this.pointed) {
            this.applyRange(this.pointed);
        }
    }

    @HostListener('mouseleave')
    @HostListener('window:blur')
    clearPointer(): void {
        this.pointed = undefined;
    }

    @HostListener('document:keydown', ['$event'])
    onShift(event: KeyboardEvent): void {
        if (event.key === 'Shift' && !event.repeat && this.pointed) {
            this.applyRange(this.pointed);
        }
    }

    @HostListener('keydown', ['$event'])
    onKeyDown(event: KeyboardEvent): void {
        this.syncInputs();
        const input = this.inputAt(event.target);
        if (
            !input ||
            event.target !== input ||
            input.disabled ||
            event.altKey ||
            event.ctrlKey ||
            event.metaKey
        ) {
            return;
        }

        if (event.key === ' ') {
            event.preventDefault();
            event.stopPropagation();
            if (!event.repeat) {
                if (event.shiftKey && this.anchor) {
                    this.applyRange(input);
                } else {
                    input.click();
                }
            }
            return;
        }

        const direction =
            event.key === 'ArrowDown' || event.key === 'ArrowRight'
                ? 1
                : event.key === 'ArrowUp' || event.key === 'ArrowLeft'
                  ? -1
                  : 0;
        if (!direction) {
            return;
        }

        event.preventDefault();
        event.stopPropagation();
        const enabledInputs = this.inputs.filter(item => !item.disabled);
        const nextInput =
            enabledInputs[enabledInputs.indexOf(input) + direction];
        if (nextInput) {
            nextInput.focus();
            if (event.shiftKey && this.anchor) {
                this.applyRange(nextInput);
            }
        }
    }

    @HostListener('keyup', ['$event'])
    onKeyUp(event: KeyboardEvent): void {
        const input = this.inputAt(event.target);
        if (event.key === ' ' && input && event.target === input) {
            // Space is handled on keydown; suppress native keyup activation.
            event.preventDefault();
            event.stopPropagation();
        }
    }

    private readonly onClick = (event: MouseEvent): void => {
        const target = event.target as Element;
        if (
            this.applyingRange ||
            target.closest('[data-sp-range-selection]') !== this.host
        ) {
            return;
        }
        this.syncInputs();
        const input = this.inputAt(event.target);
        if (!input) {
            if (target.closest('button, mat-checkbox')) {
                this.resetAnchor();
            }
            return;
        }
        if (input.disabled) {
            return;
        }
        if (event.shiftKey && this.anchor) {
            event.preventDefault();
            event.stopImmediatePropagation();
            const anchor = this.anchor;
            setTimeout(() => {
                if (!this.destroyed && this.anchor === anchor) {
                    this.applyRange(input);
                }
            }, 0);
        } else {
            this.anchor = input;
            this.anchorChecked =
                event.target === input ? input.checked : !input.checked;
        }
    };

    private inputAt(target: EventTarget | null): HTMLInputElement | undefined {
        const checkbox = (target as Element | null)?.closest?.(
            'mat-checkbox[spRangeSelectionItem]',
        );
        const input = checkbox?.querySelector<HTMLInputElement>(
            'input[type="checkbox"]',
        );
        return input && this.inputs.includes(input) ? input : undefined;
    }

    private syncInputs(): void {
        const inputs = Array.from(
            this.host.querySelectorAll<HTMLInputElement>(
                'mat-checkbox[spRangeSelectionItem] input[type="checkbox"]',
            ),
        ).filter(
            input => input.closest('[data-sp-range-selection]') === this.host,
        );
        if (
            inputs.length !== this.inputs.length ||
            inputs.some((input, index) => input !== this.inputs[index])
        ) {
            this.inputs = inputs;
            this.resetAnchor();
            this.pointed = undefined;
        }
    }

    private resetAnchor(): void {
        this.anchor = undefined;
    }

    private applyRange(target: HTMLInputElement): void {
        this.syncInputs();
        const start = this.inputs.indexOf(this.anchor);
        const end = this.inputs.indexOf(target);
        if (start < 0 || end < 0 || target.disabled) {
            return;
        }
        this.applyingRange = true;
        try {
            for (const input of this.inputs.slice(
                Math.min(start, end),
                Math.max(start, end) + 1,
            )) {
                if (!input.disabled && input.checked !== this.anchorChecked) {
                    input.click();
                }
            }
        } finally {
            this.applyingRange = false;
        }
    }
}
