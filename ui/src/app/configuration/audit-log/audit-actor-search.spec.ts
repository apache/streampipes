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
import { SearchSelectComponent } from '@streampipes/shared-ui';
import { describe, expect, it } from 'vitest';

describe('Audit actor search-select behavior', () => {
    function create() {
        TestBed.overrideComponent(SearchSelectComponent, {
            set: { template: '', imports: [] },
        });
        return TestBed.createComponent(SearchSelectComponent);
    }

    it('keeps free text on close and supports clearing it', () => {
        const fixture = create();
        fixture.componentRef.setInput('freeTextValue', (text: string) => ({
            id: text,
            label: text,
        }));
        fixture.componentInstance.onInput('deleted-id');
        fixture.componentInstance.onOpened();
        expect(fixture.componentInstance.searchText()).toBe('deleted-id');
        fixture.componentInstance.onClosed();
        expect(fixture.componentInstance.value()).toEqual({
            id: 'deleted-id',
            label: 'deleted-id',
        });
        expect(fixture.componentInstance.inputValue()).toBe('deleted-id');
        fixture.componentInstance.clearValue();
        expect(fixture.componentInstance.value()).toBeUndefined();
    });

    it('supports custom searching by ID without changing the display label', () => {
        const fixture = create();
        fixture.componentRef.setInput('items', [
            { id: 'principal-1', label: 'operator' },
        ]);
        fixture.componentRef.setInput(
            'searchTextFor',
            (item: { id: string; label: string }) => `${item.label} ${item.id}`,
        );
        fixture.componentInstance.onInput('principal-1');
        expect(fixture.componentInstance.filteredItems()).toEqual([
            { id: 'principal-1', label: 'operator' },
        ]);
    });

    it('keeps existing selection-only behavior by default', () => {
        const fixture = create();
        fixture.componentInstance.onInput('unselected');
        fixture.componentInstance.onClosed();
        expect(fixture.componentInstance.value()).toBeUndefined();
        expect(fixture.componentInstance.inputValue()).toBe('');
    });
});
