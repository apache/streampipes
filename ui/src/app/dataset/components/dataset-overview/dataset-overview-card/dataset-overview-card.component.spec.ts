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
import { provideTranslateService } from '@ngx-translate/core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { DatasetOverviewEntry } from '../dataset-overview-entry';
import { DatasetOverviewCardComponent } from './dataset-overview-card.component';

const LAST_EVENT = new Date(2026, 10, 4, 20, 14).getTime();

@Component({
    imports: [DatasetOverviewCardComponent],
    template: `
        <sp-dataset-overview-card
            [dataset]="dataset()"
            [lastEvent]="lastEvent()"
            [lastEventLoading]="lastEventLoading()"
            [currentTime]="currentTime"
            icon="dataset"
            [canConfigureRetention]="canConfigureRetention()"
            (showDetails)="showDetails()"
            (download)="download()"
            (configureRetention)="configureRetention()"
            (showRetentionLog)="showRetentionLog()"
        >
            <button id="preview" cardFooterActions>Preview</button>
            <span id="asset" cardAssetContext>Hall 3</span>
        </sp-dataset-overview-card>
    `,
})
class DatasetOverviewCardHostComponent {
    dataset = signal(
        Object.assign(new DatasetOverviewEntry(), {
            elementId: 'd1',
            name: 'pump_flow',
            pipelines: ['Persist pump flow', 'Archive pump flow'],
            retentionConfigured: true,
            lastExport: '2026-11-01T10:00:00Z',
            lastRetentionStatus: true,
        }),
    );
    lastEvent = signal<number | null>(LAST_EVENT);
    lastEventLoading = signal(false);
    currentTime = LAST_EVENT + 5 * 60 * 1000;
    canConfigureRetention = signal(true);
    showDetails = vi.fn();
    download = vi.fn();
    configureRetention = vi.fn();
    showRetentionLog = vi.fn();
}

describe('DatasetOverviewCardComponent', () => {
    let fixture: ComponentFixture<DatasetOverviewCardHostComponent>;
    let host: DatasetOverviewCardHostComponent;

    const card = () => fixture.nativeElement as HTMLElement;
    const byCy = (id: string) =>
        card().querySelector<HTMLElement>(`[data-cy="${id}"]`);
    const text = (id: string) =>
        byCy(id)?.textContent.replace(/\s+/g, ' ').trim();

    async function refresh() {
        await fixture.whenStable();
        fixture.detectChanges();
    }

    async function updateDataset(changes: Partial<DatasetOverviewEntry>) {
        host.dataset.update(dataset =>
            Object.assign(new DatasetOverviewEntry(), dataset, changes),
        );
        await refresh();
    }

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
            imports: [DatasetOverviewCardHostComponent],
            providers: [provideTranslateService()],
        }).compileComponents();
        fixture = TestBed.createComponent(DatasetOverviewCardHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('shows identity and facts without reserving a description', () => {
        expect(card().querySelector('.card-title-link').textContent).toContain(
            'pump_flow',
        );
        expect(card().querySelector('.card-header--compact')).not.toBeNull();
        expect(card().querySelector('.card-icon mat-icon').textContent).toBe(
            'dataset',
        );
        expect(text('dataset-card-last-event')).toBe('5 minutes ago');
        expect(text('dataset-card-pipelines')).toBe('Persist pump flow +1');
    });

    it('shows a spinner while the last event loads', async () => {
        host.lastEventLoading.set(true);
        await refresh();

        expect(byCy('dataset-card-last-event-loading')).not.toBeNull();
        expect(byCy('dataset-card-last-event')).toBeNull();
    });

    it('falls back when there is no event or pipeline', async () => {
        host.lastEvent.set(0);
        await updateDataset({ pipelines: [] });

        expect(text('dataset-card-last-event')).toBe('n/a');
        expect(text('dataset-card-no-pipelines')).toBe('None');
    });

    it('opens the retention log and flags a failed run', async () => {
        expect(byCy('dataset-card-retention-failed')).toBeNull();

        await updateDataset({ lastRetentionStatus: false });
        byCy('dataset-card-retention-log').click();

        expect(byCy('dataset-card-retention-failed')).not.toBeNull();
        expect(host.showRetentionLog).toHaveBeenCalledOnce();
    });

    it('shows retention as off when it is not configured', async () => {
        await updateDataset({ retentionConfigured: false });

        expect(byCy('dataset-card-retention-log')).toBeNull();
        expect(text('dataset-card-retention-off')).toBe('Off');
    });

    it('emits the card actions', () => {
        card().querySelector<HTMLElement>('.card-title-link').click();
        byCy('dataset-card-download').click();
        byCy('dataset-card-retention').click();

        expect(host.showDetails).toHaveBeenCalledOnce();
        expect(host.download).toHaveBeenCalledOnce();
        expect(host.configureRetention).toHaveBeenCalledOnce();
    });

    it('offers retention settings only to users who may change them', async () => {
        host.canConfigureRetention.set(false);
        await refresh();

        expect(byCy('dataset-card-retention')).toBeNull();
    });

    it('projects the table actions and asset context', () => {
        expect(
            card().querySelector('.card-footer-end #preview'),
        ).not.toBeNull();
        expect(card().querySelector('.card-context #asset')).not.toBeNull();
    });
});
