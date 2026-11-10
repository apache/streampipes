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
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { SpTableComponent } from './sp-table.component';
import { SpAssetBrowserService } from '../asset-browser/asset-browser.service';
import { FeatureCardService } from '../feature-card-host/feature-card.service';

interface TestRow {
    _id: string;
    name: string;
}

@Component({
    selector: 'sp-table-shift-host',
    imports: [SpTableComponent, MatColumnDef, MatHeaderCellDef, MatCellDef],
    template: `
        <sp-table [dataSource]="dataSource" [columns]="columns">
            <ng-container matColumnDef="name">
                <th mat-header-cell *matHeaderCellDef>Name</th>
                <td mat-cell *matCellDef="let row">{{ row.name }}</td>
            </ng-container>
        </sp-table>
    `,
})
class SpTableShiftHostComponent {
    readonly table = viewChild.required(SpTableComponent<TestRow>);

    dataSource = new MatTableDataSource<TestRow>([
        { _id: 'r1', name: 'Alpha' },
        { _id: 'r2', name: 'Beta' },
        { _id: 'r3', name: 'Gamma' },
    ]);
    columns = ['name'];
}

describe('SpTableComponent shift selection', () => {
    let fixture: ComponentFixture<SpTableShiftHostComponent>;
    let table: SpTableComponent<TestRow>;
    let rows: TestRow[];

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

        await TestBed.configureTestingModule({
            imports: [SpTableShiftHostComponent],
            providers: [
                provideNoopAnimations(),
                provideTranslateService(),
                {
                    provide: SpAssetBrowserService,
                    useValue: {
                        assetData$: new BehaviorSubject(undefined),
                    },
                },
                {
                    provide: FeatureCardService,
                    useValue: { openFeatureCard: () => {} },
                },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(SpTableShiftHostComponent);
        fixture.detectChanges();
        table = fixture.componentInstance.table();
        rows = table.dataSource.data;
        table.visiblePageRows = rows;
    });

    afterEach(() => {
        fixture.destroy();
        TestBed.resetTestingModule();
    });

    function click(shiftKey: boolean) {
        table.onRowSelectionClick({
            shiftKey,
            stopPropagation: () => {},
        } as MouseEvent);
    }

    it('selects every row between the anchor and a shift-clicked row', () => {
        click(false);
        table.toggleRowSelection(rows[0], true);
        click(true);
        table.toggleRowSelection(rows[2], true);

        expect(table.selection.selected).toEqual(rows);
    });

    it('deselects the range when the shift-clicked row is selected', () => {
        click(false);
        table.toggleRowSelection(rows[0], true);
        click(true);
        table.toggleRowSelection(rows[2], true);
        click(true);
        table.toggleRowSelection(rows[2], false);

        expect(table.selection.selected).toEqual([]);
    });

    it('falls back to a single row without an anchor', () => {
        click(true);
        table.toggleRowSelection(rows[1], true);

        expect(table.selection.selected).toEqual([rows[1]]);
    });
});
