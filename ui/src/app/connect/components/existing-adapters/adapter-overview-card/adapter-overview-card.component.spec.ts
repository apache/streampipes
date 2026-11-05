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

import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import {
    AdapterSummaryDto,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AdapterOverviewCardComponent } from './adapter-overview-card.component';

const LAST_MESSAGE = new Date(2026, 10, 4, 20, 14).getTime();

@Component({
    imports: [AdapterOverviewCardComponent],
    template: `
        <sp-adapter-overview-card
            [adapter]="adapter()"
            [metrics]="metrics()"
            [iconUrl]="iconUrl()"
            [operationInProgress]="operationInProgress()"
            [currentTime]="currentTime()"
            (startAdapter)="startAdapter()"
            (stopAdapter)="stopAdapter()"
            (showDetails)="showDetails()"
            (editAdapter)="editAdapter()"
        >
            <button id="preview" cardHeaderActions>Preview</button>
            <span id="asset" cardAssetContext>Hall 3</span>
        </sp-adapter-overview-card>
    `,
})
class AdapterOverviewCardHostComponent {
    adapter = signal({
        elementId: 'a1',
        name: 'Hall 3 OPC UA',
        description: 'Line 1 machine signals',
        running: true,
        createdAt: new Date(2026, 1, 2, 8, 30).getTime(),
    } as AdapterSummaryDto);
    metrics = signal<SpMetricsEntry | undefined>({
        messagesOut: { counter: 12480 },
        lastTimestamp: LAST_MESSAGE,
    } as SpMetricsEntry);
    iconUrl = signal<string | undefined>('/icon.png');
    currentTime = signal(LAST_MESSAGE + 2 * 60 * 1000);
    operationInProgress = signal(false);
    startAdapter = vi.fn();
    stopAdapter = vi.fn();
    showDetails = vi.fn();
    editAdapter = vi.fn();
}

describe('AdapterOverviewCardComponent', () => {
    let fixture: ComponentFixture<AdapterOverviewCardHostComponent>;
    let host: AdapterOverviewCardHostComponent;

    const byCy = (id: string): HTMLElement =>
        fixture.nativeElement.querySelector(`[data-cy="${id}"]`);
    const text = (id: string) => byCy(id)?.textContent.trim();

    beforeEach(async () => {
        window.matchMedia ??= query =>
            ({
                matches: false,
                media: query,
                addListener: () => {},
                removeListener: () => {},
                addEventListener: () => {},
                removeEventListener: () => {},
            }) as unknown as MediaQueryList;
        await TestBed.configureTestingModule({
            imports: [AdapterOverviewCardHostComponent],
            providers: [provideTranslateService()],
        }).compileComponents();
        fixture = TestBed.createComponent(AdapterOverviewCardHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('shows identity, status and metrics', () => {
        const card = fixture.nativeElement as HTMLElement;

        expect(card.querySelector('[role="heading"]').textContent).toContain(
            'Hall 3 OPC UA',
        );
        expect(card.querySelector('.card-leading img')).not.toBeNull();
        expect(text('adapter-card-status')).toBe('Running');
        expect(text('adapter-card-messages')).toBe('12,480');
        expect(text('adapter-card-last-message')).toBe('2 minutes ago');
    });

    it('shows older messages with their exact time', async () => {
        host.currentTime.set(LAST_MESSAGE + 2 * 24 * 60 * 60 * 1000);
        await fixture.whenStable();
        fixture.detectChanges();

        expect(text('adapter-card-last-message')).toContain('2026');
    });

    it('formats the message count in the current language', async () => {
        TestBed.inject(TranslateService).use('de');
        host.metrics.update(metrics => ({ ...metrics }));
        await fixture.whenStable();
        fixture.detectChanges();

        expect(text('adapter-card-messages')).toBe('12.480');
    });

    it('falls back when there are no metrics or icon', async () => {
        host.metrics.set(undefined);
        host.iconUrl.set(undefined);
        await fixture.whenStable();
        fixture.detectChanges();
        const card = fixture.nativeElement as HTMLElement;

        expect(text('adapter-card-messages')).toBe('0');
        expect(text('adapter-card-last-message')).toBe('n/a');
        expect(card.querySelector('.card-leading').matches(':empty')).toBe(
            true,
        );
    });

    it('projects the table actions and asset context', () => {
        const card = fixture.nativeElement as HTMLElement;

        expect(
            card.querySelector('.card-header-actions #preview'),
        ).not.toBeNull();
        expect(card.querySelector('.card-context #asset')).not.toBeNull();
    });

    it('offers stop for a running adapter', () => {
        expect(byCy('adapter-card-start')).toBeNull();

        byCy('adapter-card-stop').click();

        expect(host.stopAdapter).toHaveBeenCalledOnce();
    });

    it('offers start for a stopped adapter', async () => {
        host.adapter.update(adapter => ({ ...adapter, running: false }));
        await fixture.whenStable();
        fixture.detectChanges();

        expect(text('adapter-card-status')).toBe('Stopped');
        expect(byCy('adapter-card-stop')).toBeNull();

        byCy('adapter-card-start').click();

        expect(host.startAdapter).toHaveBeenCalledOnce();
    });

    it('opens the details from the title and edits from the footer', () => {
        (fixture.nativeElement as HTMLElement)
            .querySelector<HTMLElement>('.card-title-link')
            .click();
        byCy('adapter-card-edit').click();

        expect(host.showDetails).toHaveBeenCalledOnce();
        expect(host.editAdapter).toHaveBeenCalledOnce();
    });

    it('replaces start and stop while an operation is in progress', async () => {
        host.operationInProgress.set(true);
        await fixture.whenStable();
        fixture.detectChanges();

        expect(byCy('adapter-operation-in-progress-spinner')).not.toBeNull();
        expect(byCy('adapter-card-stop')).toBeNull();
        expect(byCy('adapter-card-edit')).not.toBeNull();
    });
});
