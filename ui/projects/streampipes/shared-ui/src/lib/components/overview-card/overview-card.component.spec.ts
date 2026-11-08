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
import { SpOverviewCardGridComponent } from './overview-card-grid.component';
import { SpOverviewCardComponent } from './overview-card.component';

@Component({
    imports: [SpOverviewCardComponent, SpOverviewCardGridComponent],
    template: `
        <sp-overview-card-grid>
            <sp-overview-card id="full" title="Pump" description="Main hall">
                <input cardSelect type="checkbox" />
                <img cardIcon alt="" />
                <p id="content">Content</p>
                <span id="assets" cardAssetContext>Assets</span>
                <a cardFooter href="#">Details</a>
                <button cardFooterActions>Settings</button>
            </sp-overview-card>
            <sp-overview-card id="minimal" title="Valve" />
        </sp-overview-card-grid>
    `,
})
class OverviewCardHostComponent {}

describe('SpOverviewCardComponent', () => {
    function render(): HTMLElement {
        const fixture = TestBed.createComponent(OverviewCardHostComponent);
        fixture.detectChanges();
        return fixture.nativeElement;
    }

    it('labels the card and renders its identity', () => {
        const card = render().querySelector('#full article');

        expect(card.getAttribute('aria-label')).toBe('Pump');
        expect(card.querySelector('[role="heading"]').textContent).toContain(
            'Pump',
        );
        expect(card.querySelector('.card-description').textContent.trim()).toBe(
            'Main hall',
        );
    });

    it('projects content into the header, body and footer slots', () => {
        const card = render().querySelector('#full');

        expect(
            card.querySelector('.card-header .card-icon img'),
        ).not.toBeNull();
        expect(
            card.querySelector('.card-header .card-select input'),
        ).not.toBeNull();
        expect(card.querySelector('.card-body #content')).not.toBeNull();
        expect(card.querySelector('.card-context #assets')).not.toBeNull();
        expect(card.querySelector('.card-footer-start a').textContent).toBe(
            'Details',
        );
        expect(card.querySelector('.card-footer-end button').textContent).toBe(
            'Settings',
        );
    });

    it('places the selection before the title', () => {
        const header = render().querySelector('#full .card-header');

        expect(header.firstElementChild.classList.contains('card-select')).toBe(
            true,
        );
    });

    it('places the icon after the title', () => {
        const header = render().querySelector('#full .card-header');

        expect(header.lastElementChild.classList.contains('card-icon')).toBe(
            true,
        );
    });

    it('renders a plain title unless it is clickable', () => {
        expect(render().querySelector('.card-title-link')).toBeNull();
    });

    it('emits the title click when the title is clickable', () => {
        const fixture = TestBed.createComponent(SpOverviewCardComponent);
        fixture.componentRef.setInput('title', 'Pump');
        fixture.componentRef.setInput('titleClickable', true);
        let clicks = 0;
        fixture.componentInstance.titleClick.subscribe(() => clicks++);
        fixture.detectChanges();

        (fixture.nativeElement as HTMLElement)
            .querySelector<HTMLElement>('.card-title-link')
            .click();

        expect(clicks).toBe(1);
    });

    it('reserves the description lines unless turned off', () => {
        const fixture = TestBed.createComponent(SpOverviewCardComponent);
        fixture.componentRef.setInput('title', 'Pump');
        fixture.detectChanges();
        const header = (fixture.nativeElement as HTMLElement).querySelector(
            '.card-header',
        );

        expect(header.classList.contains('card-header--compact')).toBe(false);

        fixture.componentRef.setInput('reserveDescription', false);
        fixture.detectChanges();

        expect(header.classList.contains('card-header--compact')).toBe(true);
    });

    it('applies the accent colour', () => {
        const fixture = TestBed.createComponent(SpOverviewCardComponent);
        fixture.componentRef.setInput('title', 'Pump');
        fixture.componentRef.setInput('accentColor', 'rgb(1, 2, 3)');
        fixture.detectChanges();
        const card = (
            fixture.nativeElement as HTMLElement
        ).querySelector<HTMLElement>('article');

        expect(card.style.getPropertyValue('--card-accent')).toBe(
            'rgb(1, 2, 3)',
        );
    });

    it('leaves unused slots empty so they collapse', () => {
        const card = render().querySelector('#minimal');

        expect(card.querySelector('.card-description')).toBeNull();
        [
            '.card-icon',
            '.card-select',
            '.card-body',
            '.card-context',
            '.card-footer-start',
            '.card-footer-end',
        ].forEach(slot =>
            expect(card.querySelector(slot).matches(':empty'), slot).toBe(true),
        );
    });
});
