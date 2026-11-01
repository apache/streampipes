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

import { Component, viewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
    MatCellDef,
    MatColumnDef,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { provideTranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { SpTableComponent } from './sp-table.component';
import { SpAssetBrowserService } from '../asset-browser/asset-browser.service';
import { AssetBrowserData } from '../asset-browser/asset-browser.model';
import { FeatureCardService } from '../feature-card-host/feature-card.service';
import { SpTableAssetContextConfig } from './sp-table.model';

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
