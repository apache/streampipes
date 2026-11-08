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

import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import {
    SpOverviewCardFactComponent,
    SpOverviewCardFactsComponent,
} from './overview-card-facts.component';

@Component({
    imports: [SpOverviewCardFactComponent, SpOverviewCardFactsComponent],
    template: `
        <sp-overview-card-facts>
            <div spOverviewCardFact label="Status">Running</div>
            <div spOverviewCardFact label="Created">Today</div>
        </sp-overview-card-facts>
    `,
})
class FactsHostComponent {}

describe('SpOverviewCardFactsComponent', () => {
    it('renders the facts as a description list', () => {
        const fixture = TestBed.createComponent(FactsHostComponent);
        fixture.detectChanges();
        const groups = (fixture.nativeElement as HTMLElement).querySelectorAll(
            'dl > div',
        );

        expect(groups.length).toBe(2);
        expect(groups[0].querySelector('dt').textContent).toBe('Status');
        expect(groups[0].querySelector('dd').textContent).toBe('Running');
    });
});
