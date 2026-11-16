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
import { ChartOverviewRow } from '../chart-overview-row';
import { ChartOverviewCardComponent } from './chart-overview-card.component';

@Component({
    imports: [ChartOverviewCardComponent],
    template: `
        <sp-chart-overview-card
            [chart]="chart()"
            accentColor="rgb(1, 2, 3)"
            [canEdit]="canEdit()"
            (openChart)="openChart()"
            (showChart)="showChart()"
            (editChart)="editChart()"
        >
            <button id="preview" cardFooterActions>Preview</button>
            <span id="asset" cardAssetContext>Hall 3</span>
        </sp-chart-overview-card>
    `,
})
class ChartOverviewCardHostComponent {
    chart = signal({
        elementId: 'c1',
        name: 'Pump pressure',
        datasetName: 'pump_flow',
        createdAtEpochMs: 1,
        lastModifiedEpochMs: 2,
        multiSourceChart: false,
        widgetType: 'line-chart',
        healthStatus: 'OK',
        chartTypeIcon: 'show_chart',
        chartTypeName: 'Line chart',
        createdAtLabel: '01.11.2026',
        lastModifiedLabel: '04.11.2026',
        showLegacyWarning: false,
        showRequiresAttentionWarning: false,
    } as ChartOverviewRow);
    canEdit = signal(true);
    openChart = vi.fn();
    showChart = vi.fn();
    editChart = vi.fn();
}

describe('ChartOverviewCardComponent', () => {
    let fixture: ComponentFixture<ChartOverviewCardHostComponent>;
    let host: ChartOverviewCardHostComponent;

    const card = () => fixture.nativeElement as HTMLElement;
    const byCy = (id: string) =>
        card().querySelector<HTMLElement>(`[data-cy="${id}"]`);
    const text = (id: string) => byCy(id)?.textContent.trim();

    async function update(changes: Partial<ChartOverviewRow>) {
        host.chart.update(chart => ({ ...chart, ...changes }));
        await fixture.whenStable();
        fixture.detectChanges();
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
            imports: [ChartOverviewCardHostComponent],
            providers: [provideTranslateService()],
        }).compileComponents();
        fixture = TestBed.createComponent(ChartOverviewCardHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('shows the chart type, dataset and last change', () => {
        expect(card().querySelector('.card-title-link').textContent).toContain(
            'Pump pressure',
        );
        expect(card().querySelector('.card-header--compact')).not.toBeNull();
        expect(card().querySelector('.card-icon mat-icon').textContent).toBe(
            'show_chart',
        );
        expect(text('chart-card-type')).toBe('Line chart');
        expect(text('chart-card-dataset')).toBe('pump_flow');
        expect(text('chart-card-last-modified')).toBe('04.11.2026');
    });

    it('falls back when the chart has no dataset', async () => {
        await update({ datasetName: null });

        expect(text('chart-card-dataset')).toBe('n/a');
    });

    it('shows no warning for a healthy chart', () => {
        expect(byCy('chart-card-legacy-warning')).toBeNull();
        expect(byCy('chart-card-attention-warning')).toBeNull();
    });

    it('warns about outdated charts and schema changes', async () => {
        await update({
            showLegacyWarning: true,
            showRequiresAttentionWarning: true,
        });

        expect(byCy('chart-card-legacy-warning')).not.toBeNull();
        expect(byCy('chart-card-attention-warning')).not.toBeNull();
    });

    it('opens the chart from the title and the footer', () => {
        card().querySelector<HTMLElement>('.card-title-link').click();
        byCy('chart-card-show').click();
        byCy('chart-card-edit').click();

        expect(host.openChart).toHaveBeenCalledOnce();
        expect(host.showChart).toHaveBeenCalledOnce();
        expect(host.editChart).toHaveBeenCalledOnce();
    });

    it('offers editing only to users who may edit charts', async () => {
        host.canEdit.set(false);
        await fixture.whenStable();
        fixture.detectChanges();

        expect(byCy('chart-card-edit')).toBeNull();
        expect(byCy('chart-card-show')).not.toBeNull();
    });

    it('projects the table actions and asset context', () => {
        expect(
            card().querySelector('.card-footer-end #preview'),
        ).not.toBeNull();
        expect(card().querySelector('.card-context #asset')).not.toBeNull();
    });
});
