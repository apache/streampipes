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

import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { MatSort } from '@angular/material/sort';
import { provideTranslateService } from '@ngx-translate/core';
import {
    FeatureCardService,
    LocalStorageService,
    SpAssetBrowserService,
    SpBreadcrumbService,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { FavoriteSummary } from '@streampipes/platform-services';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { FavoritesComponent } from './favorites.component';
import { FavoritesStore } from './favorites.store';

describe('Favorites overview', () => {
    const favorites: FavoriteSummary[] = [
        {
            resourceId: 'p1',
            resourceType: 'PIPELINE',
            name: 'Pipeline one',
            available: true,
        },
        {
            resourceId: 'c1',
            resourceType: 'CHART',
            name: 'Chart one',
            available: true,
        },
        {
            resourceId: 'p2',
            resourceType: 'PIPELINE',
            name: 'Pipeline two',
            available: true,
        },
    ];
    const breadcrumb = {
        getRootLink: vi.fn(item => [item]),
        updateBreadcrumb: vi.fn(),
    };

    beforeEach(async () => {
        vi.clearAllMocks();
        await TestBed.configureTestingModule({
            imports: [FavoritesComponent],
            providers: [
                provideRouter([]),
                provideTranslateService(),
                { provide: SpBreadcrumbService, useValue: breadcrumb },
                { provide: FeatureCardService, useValue: {} },
                {
                    provide: SpAssetBrowserService,
                    useValue: { assetData$: of(undefined) },
                },
                {
                    provide: LocalStorageService,
                    useValue: { signalFor: () => signal(10), set: vi.fn() },
                },
                {
                    provide: FavoritesStore,
                    useValue: {
                        favorites: signal(favorites),
                        loading: signal(false),
                        loaded: signal(true),
                        error: signal(false),
                        pending: signal(new Set()),
                        selected: () => true,
                        key: (type: string, id: string) => type + id,
                        load: vi.fn(),
                        toggle: vi.fn(),
                    },
                },
            ],
        }).compileComponents();
    });

    it('switches between grouped and list views without rebuilding the data source', async () => {
        const fixture = TestBed.createComponent(FavoritesComponent);
        fixture.detectChanges();
        await fixture.whenStable();
        const table = fixture.debugElement.query(By.directive(SpTableComponent))
            .componentInstance as SpTableComponent<FavoriteSummary>;
        const source = table.dataSource;
        for (let index = 0; index < 3; index++) {
            table.setViewMode('grouped');
            fixture.detectChanges();
            await fixture.whenStable();
            expect(
                table.groupedSections.map(group => [group.title, group.count]),
            ).toEqual([
                ['Chart', 1],
                ['Pipeline', 2],
            ]);
            expect(
                fixture.nativeElement.querySelectorAll(
                    '.grouped-table-row--header',
                ).length,
            ).toBe(2);
            table.setViewMode('list');
            fixture.detectChanges();
            await fixture.whenStable();
            expect(table.dataSource).toBe(source);
        }
        expect(breadcrumb.updateBreadcrumb).toHaveBeenCalledWith([
            { label: 'Favorites', link: ['favorites'] },
        ]);
    });

    it('keeps grouping, search and paging working together', async () => {
        const fixture = TestBed.createComponent(FavoritesComponent);
        fixture.detectChanges();
        await fixture.whenStable();
        const table = fixture.debugElement.query(By.directive(SpTableComponent))
            .componentInstance as SpTableComponent<FavoriteSummary>;
        table.setViewMode('grouped');
        table.onNameSearchInput('Pipeline');
        await new Promise(resolve => setTimeout(resolve, 200));
        fixture.detectChanges();
        await fixture.whenStable();
        expect(table.groupedSections.map(group => group.title)).toEqual([
            'Pipeline',
        ]);
        expect(table.visiblePageRows.length).toBe(2);
        table.paginator.pageSize = 1;
        table.paginator.pageIndex = 1;
        table.onPage({ pageIndex: 1, pageSize: 1, length: 2 });
        fixture.detectChanges();
        expect(table.visiblePageRows.map(row => row.resourceId)).toEqual([
            'p2',
        ]);
    });

    it('preserves row priority and column sorting when refreshing grouped pages', async () => {
        const fixture = TestBed.createComponent(FavoritesComponent);
        fixture.detectChanges();
        await fixture.whenStable();
        const table = fixture.debugElement.query(By.directive(SpTableComponent))
            .componentInstance as SpTableComponent<FavoriteSummary>;
        const sort = TestBed.runInInjectionContext(() => new MatSort());
        sort.active = 'name';
        sort.direction = 'desc';
        sort.ngOnInit();
        const sortData = table.dataSource.sortData.bind(table.dataSource);
        table.dataSource.sortData = (rows, activeSort) => {
            const sorted = sortData(rows, activeSort);
            return [
                ...sorted.filter(row => row.resourceId === 'c1'),
                ...sorted.filter(row => row.resourceId !== 'c1'),
            ];
        };
        table.dataSource.sort = sort;
        table.paginator.pageSize = 2;
        table.setViewMode('grouped');
        fixture.detectChanges();
        expect(table.visiblePageRows.map(row => row.resourceId)).toEqual([
            'c1',
            'p2',
        ]);
        expect(table.groupedSections.map(group => group.title)).toEqual([
            'Chart',
            'Pipeline',
        ]);
        table.paginator.pageIndex = 1;
        table.onPage({ pageIndex: 1, pageSize: 2, length: 3 });
        expect(table.visiblePageRows.map(row => row.resourceId)).toEqual([
            'p1',
        ]);
    });
});
