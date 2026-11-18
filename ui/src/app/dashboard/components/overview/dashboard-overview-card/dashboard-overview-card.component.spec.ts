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
import { DashboardSummaryDto } from '@streampipes/platform-services';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { DashboardOverviewCardComponent } from './dashboard-overview-card.component';

@Component({
    imports: [DashboardOverviewCardComponent],
    template: `
        <sp-dashboard-overview-card
            [dashboard]="dashboard()"
            icon="dashboard"
            [canEdit]="canEdit()"
            (showDashboard)="showDashboard()"
            (editDashboard)="editDashboard()"
        >
            <button id="preview" cardFooterActions>Preview</button>
            <span id="asset" cardAssetContext>Hall 3</span>
        </sp-dashboard-overview-card>
    `,
})
class DashboardOverviewCardHostComponent {
    dashboard = signal<DashboardSummaryDto>({
        elementId: 'd1',
        name: 'Line 1 overview',
        description: 'Throughput and pressure of line 1',
        createdAtEpochMs: new Date(2026, 9, 1, 8, 0).getTime(),
        lastModifiedEpochMs: new Date(2026, 10, 4, 9, 30).getTime(),
    });
    canEdit = signal(true);
    showDashboard = vi.fn();
    editDashboard = vi.fn();
}

describe('DashboardOverviewCardComponent', () => {
    let fixture: ComponentFixture<DashboardOverviewCardHostComponent>;
    let host: DashboardOverviewCardHostComponent;

    const card = () => fixture.nativeElement as HTMLElement;
    const byCy = (id: string) =>
        card().querySelector<HTMLElement>(`[data-cy="${id}"]`);
    const text = (id: string) => byCy(id)?.textContent.trim();

    async function refresh() {
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
            imports: [DashboardOverviewCardHostComponent],
            providers: [provideTranslateService()],
        }).compileComponents();
        fixture = TestBed.createComponent(DashboardOverviewCardHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('shows identity, description and dates', () => {
        expect(card().querySelector('.card-title-link').textContent).toContain(
            'Line 1 overview',
        );
        expect(card().querySelector('.card-description').textContent).toContain(
            'Throughput and pressure of line 1',
        );
        expect(card().querySelector('.card-header--compact')).toBeNull();
        expect(card().querySelector('.card-icon mat-icon').textContent).toBe(
            'dashboard',
        );
        expect(text('dashboard-card-last-modified')).toContain('2026');
        expect(text('dashboard-card-created')).toContain('2026');
    });

    it('shows a dash for unknown dates', async () => {
        host.dashboard.update(dashboard => ({
            ...dashboard,
            createdAtEpochMs: null,
            lastModifiedEpochMs: null,
        }));
        await refresh();

        expect(text('dashboard-card-last-modified')).toBe('–');
        expect(text('dashboard-card-created')).toBe('–');
    });

    it('shows the dashboard from the title and the footer', () => {
        card().querySelector<HTMLElement>('.card-title-link').click();
        byCy('dashboard-card-show').click();
        byCy('dashboard-card-edit').click();

        expect(host.showDashboard).toHaveBeenCalledTimes(2);
        expect(host.editDashboard).toHaveBeenCalledOnce();
    });

    it('offers editing only to users who may edit dashboards', async () => {
        host.canEdit.set(false);
        await refresh();

        expect(byCy('dashboard-card-edit')).toBeNull();
        expect(byCy('dashboard-card-show')).not.toBeNull();
    });

    it('projects the table actions and asset context', () => {
        expect(
            card().querySelector('.card-footer-end #preview'),
        ).not.toBeNull();
        expect(card().querySelector('.card-context #asset')).not.toBeNull();
    });
});
