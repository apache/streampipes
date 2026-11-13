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

import { NgTemplateOutlet } from '@angular/common';
import { Component, viewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
    MatCellDef,
    MatColumnDef,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { SpTableComponent } from './sp-table.component';
import { SpTableCardDirective } from './sp-actions/sp-table-card.directive';
import { SpTableActionsDirective } from './sp-actions/sp-table-actions.directive';
import { SpAssetBrowserService } from '../asset-browser/asset-browser.service';
import { AssetBrowserData } from '../asset-browser/asset-browser.model';
import { FeatureCardService } from '../feature-card-host/feature-card.service';
import {
    SpTableAssetContextConfig,
    SpTableResolvedAssetContext,
} from './sp-table.model';

interface TestRow {
    _id: string;
    name: string;
}

@Component({
    selector: 'sp-table-host',
    imports: [SpTableComponent, MatColumnDef, MatHeaderCellDef, MatCellDef],
    template: `
        <sp-table
            [dataSource]="dataSource"
            [columns]="columns"
            [assetContextConfig]="assetContextConfig"
            resourceIdKey="_id"
            [rememberViewMode]="true"
        >
            <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Name</th>
                <td mat-cell *matCellDef="let row">{{ row.name }}</td>
            </ng-container>
        </sp-table>
    `,
})
class SpTableHostComponent {
    readonly table = viewChild.required(SpTableComponent<TestRow>);

    dataSource = new MatTableDataSource<TestRow>([
        { _id: 'r1', name: 'Alpha' },
        { _id: 'r2', name: 'Beta' },
        { _id: 'r3', name: 'Gamma' },
    ]);
    columns = ['name', 'assetContext'];
    assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'pipeline',
        resourceIdKey: '_id',
        hideBelowWidth: 0,
    };
}

const assetLink = (resourceId: string) => ({
    resourceId,
    linkType: 'pipeline',
    linkLabel: resourceId,
    editingDisabled: false,
    navigationActive: true,
    queryHint: '',
});

const assetData = {
    assets: [
        {
            assetId: 'asset-1',
            assetName: 'Press',
            assetLinks: [assetLink('r1'), assetLink('r2')],
            assets: [],
            labelIds: [],
        },
    ],
    assetLinks: [],
    sites: [],
    labels: [],
} as unknown as AssetBrowserData;

describe('SpTableComponent grouped view', () => {
    let fixture: ComponentFixture<SpTableHostComponent>;
    let host: SpTableHostComponent;
    let assetData$: BehaviorSubject<AssetBrowserData | undefined>;

    beforeEach(async () => {
        window.matchMedia ??= query => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: () => {},
            removeListener: () => {},
            addEventListener: () => {},
            removeEventListener: () => {},
            dispatchEvent: () => false,
        });
        localStorage.clear();
        assetData$ = new BehaviorSubject<AssetBrowserData | undefined>(
            assetData,
        );

        await TestBed.configureTestingModule({
            imports: [SpTableHostComponent],
            providers: [
                provideNoopAnimations(),
                provideTranslateService(),
                { provide: SpAssetBrowserService, useValue: { assetData$ } },
                {
                    provide: FeatureCardService,
                    useValue: { openFeatureCard: () => {} },
                },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(SpTableHostComponent);
        host = fixture.componentInstance;
        fixture.detectChanges();
    });

    afterEach(() => {
        fixture.destroy();
        TestBed.resetTestingModule();
    });

    function renderedRowTexts(): string[] {
        return Array.from(
            fixture.nativeElement.querySelectorAll('tr[mat-row]'),
        ).map(row =>
            (row as HTMLElement).textContent.replace(/\s+/g, ' ').trim(),
        );
    }

    function groupHeaderRows(): HTMLElement[] {
        return Array.from(
            fixture.nativeElement.querySelectorAll(
                'tr.grouped-table-row--header',
            ),
        );
    }

    it('renders the list view with asset context cells', () => {
        expect(groupHeaderRows().length).toBe(0);
        expect(renderedRowTexts()).toEqual([
            'Alpha Press',
            'Beta Press',
            'Gamma',
        ]);
    });

    it('renders rows under group headers in the grouped view', () => {
        host.table().setViewMode('grouped');
        fixture.detectChanges();

        expect(groupHeaderRows().length).toBe(2);
        expect(renderedRowTexts()).toEqual([
            'Press 2',
            'Alpha',
            'Beta',
            'Unassigned 1',
            'Gamma',
        ]);
    });

    it('keeps the grouped rows stable across change detection passes', () => {
        const connect = vi.spyOn(host.dataSource, 'connect');

        host.table().setViewMode('grouped');
        fixture.detectChanges();
        const connectCallsAfterSwitch = connect.mock.calls.length;
        const [firstHeaderRow] = groupHeaderRows();

        fixture.detectChanges();
        fixture.detectChanges();

        expect(connect.mock.calls.length).toBe(connectCallsAfterSwitch);
        expect(groupHeaderRows()[0]).toBe(firstHeaderRow);
        expect(renderedRowTexts()).toEqual([
            'Press 2',
            'Alpha',
            'Beta',
            'Unassigned 1',
            'Gamma',
        ]);
    });

    it('switches back to the list view', () => {
        host.table().setViewMode('grouped');
        fixture.detectChanges();
        host.table().setViewMode('list');
        fixture.detectChanges();

        expect(groupHeaderRows().length).toBe(0);
        expect(renderedRowTexts()).toEqual([
            'Alpha Press',
            'Beta Press',
            'Gamma',
        ]);
    });

    it('re-renders the grouped view when asset data changes', () => {
        host.table().setViewMode('grouped');
        fixture.detectChanges();
        assetData$.next(undefined);
        fixture.detectChanges();

        expect(renderedRowTexts()).toEqual([
            'Unassigned 3',
            'Alpha',
            'Beta',
            'Gamma',
        ]);
    });

    it('re-renders the grouped view when the table data changes', () => {
        host.table().setViewMode('grouped');
        fixture.detectChanges();
        host.dataSource.data = [
            { _id: 'r1', name: 'Alpha' },
            { _id: 'r4', name: 'Delta' },
        ];
        fixture.detectChanges();

        expect(renderedRowTexts()).toEqual([
            'Press 1',
            'Alpha',
            'Unassigned 1',
            'Delta',
        ]);
    });
});

@Component({
    selector: 'sp-table-card-host',
    imports: [
        SpTableComponent,
        SpTableCardDirective,
        SpTableActionsDirective,
        NgTemplateOutlet,
        MatColumnDef,
        MatHeaderCellDef,
        MatCellDef,
    ],
    template: `
        <sp-table
            [dataSource]="dataSource"
            [columns]="columns"
            [assetContextConfig]="assetContextConfig"
            [nameSearchConfig]="{ enabled: true }"
            [showActionsMenu]="true"
            [showSelectionCheckboxes]="true"
            featureCardId="adapter"
            resourceIdKey="_id"
            [rememberViewMode]="true"
        >
            <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Name</th>
                <td mat-cell *matCellDef="let row">{{ row.name }}</td>
            </ng-container>
            <ng-template
                spTableCard
                let-row
                let-actions="actions"
                let-assetContext="assetContext"
                let-select="selection"
            >
                <article class="test-card">
                    <ng-container
                        *ngTemplateOutlet="select; context: { $implicit: row }"
                    ></ng-container>
                    <span class="test-card-name">{{ row.name }}</span>
                    <ng-container
                        *ngTemplateOutlet="actions; context: { $implicit: row }"
                    ></ng-container>
                    <ng-container
                        *ngTemplateOutlet="
                            assetContext;
                            context: { $implicit: row }
                        "
                    ></ng-container>
                </article>
            </ng-template>
            <ng-template spTableActions></ng-template>
        </sp-table>
    `,
})
class SpTableCardHostComponent {
    readonly table = viewChild.required(SpTableComponent<TestRow>);

    dataSource = new MatTableDataSource<TestRow>([
        { _id: 'r1', name: 'Alpha' },
        { _id: 'r2', name: 'Beta' },
    ]);
    columns = ['name', 'actions'];
    assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'pipeline',
        resourceIdKey: '_id',
        hideBelowWidth: 0,
    };
}

describe('SpTableComponent card view', () => {
    const openFeatureCard = vi.fn();

    const VIEW_MODE_STORAGE_KEY = 'sp-table-view-mode';

    async function setup<T>(component: new () => T, storedViewMode?: object) {
        localStorage.clear();
        if (storedViewMode) {
            localStorage.setItem(
                VIEW_MODE_STORAGE_KEY,
                JSON.stringify(storedViewMode),
            );
        }
        window.matchMedia ??= query => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: () => {},
            removeListener: () => {},
            addEventListener: () => {},
            removeEventListener: () => {},
            dispatchEvent: () => false,
        });
        openFeatureCard.mockClear();
        await TestBed.configureTestingModule({
            imports: [component],
            providers: [
                provideNoopAnimations(),
                provideTranslateService(),
                {
                    provide: SpAssetBrowserService,
                    useValue: {
                        assetData$: new BehaviorSubject<AssetBrowserData>(
                            assetData,
                        ),
                    },
                },
                { provide: FeatureCardService, useValue: { openFeatureCard } },
            ],
        }).compileComponents();
        const fixture = TestBed.createComponent(component);
        fixture.detectChanges();
        return fixture;
    }

    async function setupInCardMode() {
        const fixture = await setup(SpTableCardHostComponent);
        fixture.componentInstance.table().setViewMode('cards');
        fixture.detectChanges();
        return fixture;
    }

    const query = (
        fixture: ComponentFixture<unknown>,
        selector: string,
    ): HTMLElement[] =>
        Array.from(fixture.nativeElement.querySelectorAll(selector));

    afterEach(() => TestBed.resetTestingModule());

    it('offers the cards toggle only with a card template', async () => {
        const withoutTemplate = await setup(SpTableHostComponent);
        expect(
            query(withoutTemplate, '[data-cy="sp-table-view-cards"]').length,
        ).toBe(0);
        TestBed.resetTestingModule();

        const withTemplate = await setup(SpTableCardHostComponent);
        expect(
            query(withTemplate, '[data-cy="sp-table-view-cards"]').length,
        ).toBe(1);
        expect(query(withTemplate, '.test-card').length).toBe(0);
        expect(query(withTemplate, 'tr[mat-row]').length).toBe(2);
    });

    it('renders the current page rows as cards instead of table rows', async () => {
        const fixture = await setupInCardMode();

        expect(
            query(fixture, '.test-card-name').map(card => card.textContent),
        ).toEqual(['Alpha', 'Beta']);
        expect(query(fixture, 'tr[mat-row]').length).toBe(0);
        expect(
            query(fixture, 'table.sp-table')[0].classList.contains(
                'sp-table--hidden',
            ),
        ).toBe(true);
    });

    it('returns to the table rows when leaving the card view', async () => {
        const fixture = await setupInCardMode();

        fixture.componentInstance.table().setViewMode('list');
        fixture.detectChanges();

        expect(query(fixture, '.test-card').length).toBe(0);
        expect(query(fixture, 'tr[mat-row]').length).toBe(2);
    });

    it('remembers the chosen view mode and grouping', async () => {
        const fixture = await setupInCardMode();
        const stored = () =>
            JSON.parse(localStorage.getItem(VIEW_MODE_STORAGE_KEY));

        expect(stored()).toEqual({ viewMode: 'cards', groupBy: 'asset' });

        fixture.componentInstance.table().setViewMode('grouped');
        fixture.componentInstance.table().setGrouping('site');

        expect(stored()).toEqual({ viewMode: 'grouped', groupBy: 'site' });
    });

    it('restores the stored view mode on load', async () => {
        const fixture = await setup(SpTableCardHostComponent, {
            viewMode: 'cards',
            groupBy: 'label',
        });
        // The page rows arrive after the first render, as in list mode.
        fixture.detectChanges();

        expect(fixture.componentInstance.table().groupBy).toBe('label');
        expect(query(fixture, '.test-card').length).toBe(2);
        expect(query(fixture, 'tr[mat-row]').length).toBe(0);
    });

    it('falls back to the list when the stored mode is not offered', async () => {
        const withoutTemplate = await setup(SpTableHostComponent, {
            viewMode: 'cards',
            groupBy: 'asset',
        });

        expect(withoutTemplate.componentInstance.table().viewMode).toBe('list');
        expect(query(withoutTemplate, 'tr[mat-row]').length).toBe(3);
        expect(
            JSON.parse(localStorage.getItem(VIEW_MODE_STORAGE_KEY)).viewMode,
            'other overview pages still open in cards',
        ).toBe('cards');
        TestBed.resetTestingModule();

        const invalid = await setup(SpTableCardHostComponent, {
            viewMode: 'tiles',
            groupBy: 'owner',
        });

        expect(invalid.componentInstance.table().viewMode).toBe('list');
        expect(invalid.componentInstance.table().groupBy).toBe('asset');
    });

    it('opens the feature card of the row from the card actions', async () => {
        const fixture = await setupInCardMode();

        const previewButton = query(
            fixture,
            '.test-card:nth-of-type(2) button',
        )[0];
        previewButton.click();

        expect(openFeatureCard).toHaveBeenCalledWith('adapter', 'r2');
    });

    it('renders the asset context of the row on its card', async () => {
        const fixture = await setupInCardMode();

        expect(
            query(fixture, '.test-card')[0].querySelector('sp-label')
                .textContent,
        ).toContain('Press');
    });

    it('limits the card asset context and summarizes the rest', async () => {
        const fixture = await setup(SpTableCardHostComponent);
        const table = fixture.componentInstance.table();
        vi.spyOn(table, 'getAssetContext').mockReturnValue({
            sites: [{ id: 's1', label: 'Plant A' }],
            assets: [{ id: 'a1', label: 'Line 1' }],
            labels: [
                { _id: 'l1', label: 'Critical', color: '#f00' },
                { _id: 'l2', label: 'Demo', color: '#00f' },
            ],
            sortValue: '',
        } as SpTableResolvedAssetContext);

        table.setViewMode('cards');
        fixture.detectChanges();

        const card = query(fixture, '.test-card')[0];
        expect(
            Array.from(card.querySelectorAll('sp-label')).map(label =>
                label.textContent.trim(),
            ),
        ).toEqual(['Plant A', 'Line 1', '+2']);
        expect(
            card
                .querySelector('[data-cy="sp-table-card-asset-more"]')
                .getAttribute('aria-label'),
        ).toBe('Critical, Demo');
    });

    it('keeps the selection toolbar with search and view controls', async () => {
        const fixture = await setupInCardMode();

        expect(
            query(fixture, '[data-cy="sp-table-selection-toolbar"]').length,
        ).toBe(1);
        expect(query(fixture, '.table-search-field').length).toBe(1);
        expect(query(fixture, '.view-mode-toggle').length).toBe(1);
    });

    it('selects cards with their checkboxes, named after the row', async () => {
        const fixture = await setupInCardMode();
        const translate = TestBed.inject(TranslateService);
        translate.setTranslation('en', {
            'Select {{name}}': 'Select {{name}}',
        });
        translate.use('en');
        fixture.detectChanges();
        const table = fixture.componentInstance.table();
        const checkboxes = query(fixture, '[data-cy="sp-table-card-checkbox"]');

        expect(checkboxes.length).toBe(2);
        expect(
            checkboxes[0].querySelector('input').getAttribute('aria-label'),
        ).toBe('Select Alpha');

        checkboxes[1].querySelector('input').click();
        fixture.detectChanges();

        expect(table.selectedRows.map(row => row.name)).toEqual(['Beta']);
    });

    it('shares the selection between the card and list views', async () => {
        const fixture = await setupInCardMode();
        const table = fixture.componentInstance.table();

        query(fixture, '[data-cy="sp-table-select-visible"]')[0].click();
        fixture.detectChanges();
        const checked = (checkbox: string) =>
            query(fixture, `[data-cy="${checkbox}"] input`).filter(
                input => (input as HTMLInputElement).checked,
            ).length;

        expect(table.selectedRows.length).toBe(2);
        expect(checked('sp-table-card-checkbox')).toBe(2);

        table.setViewMode('list');
        fixture.detectChanges();

        expect(checked('sp-table-row-checkbox')).toBe(2);
    });

    it.each(['list', 'grouped', 'cards'] as const)(
        'selects ranges in rendered order in the %s view',
        async mode => {
            const fixture = await setup(SpTableCardHostComponent);
            const host = fixture.componentInstance;
            const table = host.table();
            host.dataSource.data = [
                { _id: 'r3', name: 'Gamma' },
                { _id: 'r1', name: 'Alpha' },
                { _id: 'r4', name: 'Delta' },
                { _id: 'r2', name: 'Beta' },
            ];
            table.setViewMode(mode);
            fixture.detectChanges();
            const checkbox =
                mode === 'cards'
                    ? 'sp-table-card-checkbox'
                    : 'sp-table-row-checkbox';
            const inputs = query(fixture, `[data-cy="${checkbox}"] input`);
            inputs[0].click();
            fixture.detectChanges();
            inputs[2].dispatchEvent(
                new MouseEvent('click', {
                    bubbles: true,
                    cancelable: true,
                    shiftKey: true,
                }),
            );
            await fixture.whenStable();
            fixture.detectChanges();

            expect(table.selectedRows.map(row => row._id)).toEqual(
                mode === 'grouped' ? ['r1', 'r2', 'r3'] : ['r3', 'r1', 'r4'],
            );
            expect((inputs[3] as HTMLInputElement).checked).toBe(false);
        },
    );

    it('starts a new range after searching and preserves hidden selections', async () => {
        const fixture = await setup(SpTableCardHostComponent);
        const host = fixture.componentInstance;
        const table = host.table();
        host.dataSource.data = Array.from({ length: 6 }, (_, index) => ({
            _id: `r${index}`,
            name: index === 0 ? 'Hidden' : `Visible ${index}`,
        }));
        fixture.detectChanges();
        query(fixture, '[data-cy="sp-table-row-checkbox"] input')[0].click();
        host.dataSource.filter = 'visible';
        fixture.detectChanges();
        query(
            fixture,
            '[data-cy="sp-table-row-checkbox"] input',
        )[3].dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                shiftKey: true,
            }),
        );
        await fixture.whenStable();
        fixture.detectChanges();

        expect(table.selectedRows.map(row => row._id)).toEqual(['r0', 'r4']);
    });

    it('limits ranges to the current page and resets the anchor on page changes', async () => {
        const fixture = await setup(SpTableCardHostComponent);
        const host = fixture.componentInstance;
        const table = host.table();
        host.dataSource.data = Array.from({ length: 20 }, (_, index) => ({
            _id: `r${index}`,
            name: `Row ${index}`,
        }));
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();
        query(fixture, '[data-cy="sp-table-row-checkbox"] input')[1].click();
        table.paginator.nextPage();
        fixture.detectChanges();
        const inputs = query(
            fixture,
            '[data-cy="sp-table-row-checkbox"] input',
        );
        inputs[4].dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                shiftKey: true,
            }),
        );
        await fixture.whenStable();
        fixture.detectChanges();
        expect(table.selectedRows.map(row => row._id)).toEqual(['r1', 'r14']);

        inputs[7].dispatchEvent(
            new MouseEvent('mouseover', {
                bubbles: true,
                shiftKey: true,
            }),
        );
        fixture.detectChanges();
        expect(table.selectedRows.map(row => row._id)).toEqual([
            'r1',
            'r14',
            'r15',
            'r16',
            'r17',
        ]);
    });

    it('shows the empty message when there are no rows', async () => {
        const fixture = await setupInCardMode();

        fixture.componentInstance.dataSource.data = [];
        fixture.detectChanges();

        expect(
            query(fixture, '.sp-table-cards [data-cy="no-table-entries"]')
                .length,
        ).toBe(1);
    });
});
