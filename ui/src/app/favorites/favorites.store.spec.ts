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

import { ChangeDetectorRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
    HttpTestingController,
    provideHttpClientTesting,
} from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { CurrentUserService } from '@streampipes/shared-ui';
import {
    FavoriteResourceType,
    FavoriteSummary,
    UserInfo,
} from '@streampipes/platform-services';
import { BehaviorSubject } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { FavoritesStore } from './favorites.store';

describe('FavoritesStore', () => {
    const url = '/streampipes-backend/api/v2/favorites';
    let store: FavoritesStore;
    let http: HttpTestingController;
    let user: BehaviorSubject<UserInfo>;
    const favorite: FavoriteSummary = {
        resourceType: 'ASSET',
        resourceId: 'asset:/one',
        name: 'Asset',
        available: true,
    };

    beforeEach(() => {
        user = new BehaviorSubject({ username: 'alice' } as UserInfo);
        TestBed.configureTestingModule({
            providers: [
                FavoritesStore,
                provideHttpClient(),
                provideHttpClientTesting(),
                { provide: CurrentUserService, useValue: { user$: user } },
                { provide: MatSnackBar, useValue: { open: vi.fn() } },
                {
                    provide: ChangeDetectorRef,
                    useValue: { markForCheck: vi.fn() },
                },
                {
                    provide: TranslateService,
                    useValue: { instant: (key: string) => key },
                },
            ],
        });
        store = TestBed.inject(FavoritesStore);
        http = TestBed.inject(HttpTestingController);
    });

    afterEach(() => http.verify());

    it('deduplicates loads and repeated clicks, updating immediately', () => {
        store.load();
        store.load();
        http.expectOne(url).flush([]);
        store.toggle('ASSET', favorite.resourceId);
        store.toggle('ASSET', favorite.resourceId);
        expect(store.selected('ASSET', favorite.resourceId)).toBe(true);
        const request = http.expectOne(req => req.method === 'PUT');
        expect(request.request.params.get('resourceId')).toBe(
            favorite.resourceId,
        );
        expect(request.request.params.has('userId')).toBe(false);
        request.flush(favorite);
        expect(store.selected('ASSET', favorite.resourceId)).toBe(true);
        expect(store.pending().size).toBe(0);
    });

    it('preserves a favorite on a failed removal and permits retry', () => {
        store.load();
        http.expectOne(url).flush([favorite]);
        store.toggle('ASSET', favorite.resourceId);
        expect(store.selected('ASSET', favorite.resourceId)).toBe(false);
        http.expectOne(req => req.method === 'DELETE').flush('Unavailable', {
            status: 503,
            statusText: 'Unavailable',
        });
        expect(store.selected('ASSET', favorite.resourceId)).toBe(true);
        expect(store.error()).toBe(true);
        expect(store.pending().size).toBe(0);
        store.toggle('ASSET', favorite.resourceId);
        http.expectOne(req => req.method === 'DELETE').flush(null);
        expect(store.favorites()).toEqual([]);
    });

    it('can remove unavailable resources', () => {
        store.load();
        http.expectOne(url).flush([
            { ...favorite, available: false, name: null },
        ]);
        store.toggle('ASSET', favorite.resourceId);
        http.expectOne(req => req.method === 'DELETE').flush(null);
        expect(store.favorites()).toEqual([]);
    });

    it('rolls back a failed addition without undoing another successful addition', () => {
        store.load();
        http.expectOne(url).flush([]);
        store.toggle('ASSET', favorite.resourceId);
        store.toggle('CHART', 'chart');
        expect(store.favorites()).toHaveLength(2);
        const chart: FavoriteSummary = {
            resourceType: 'CHART',
            resourceId: 'chart',
            name: 'Chart',
            available: true,
        };
        http.expectOne(req => req.params.get('resourceType') === 'CHART').flush(
            chart,
        );
        http.expectOne(req => req.params.get('resourceType') === 'ASSET').flush(
            null,
            {
                status: 500,
                statusText: 'Error',
            },
        );
        expect(store.favorites()).toEqual([chart]);
        expect(store.pending().size).toBe(0);
    });

    it('clears another users state and cancels their in-flight requests', () => {
        store.load();
        http.expectOne(url).flush([favorite]);
        store.toggle('ASSET', favorite.resourceId);
        const request = http.expectOne(req => req.method === 'DELETE');
        user.next({ username: 'bob' } as UserInfo);
        expect(request.cancelled).toBe(true);
        expect(store.favorites()).toEqual([]);
        expect(store.loaded()).toBe(false);
        expect(store.pending().size).toBe(0);
    });

    it('allows retry after a failed initial load', () => {
        store.load();
        http.expectOne(url).flush(null, { status: 500, statusText: 'Error' });
        expect(store.loaded()).toBe(false);
        expect(store.loading()).toBe(false);
        store.load();
        http.expectOne(url).flush([favorite]);
        expect(store.loaded()).toBe(true);
        expect(store.error()).toBe(false);
    });

    describe('favorite-first table sorting', () => {
        const rows = [
            { elementId: 'a', name: 'Alpha' },
            { elementId: 'b', name: 'Bravo' },
            { elementId: 'c', name: 'Charlie' },
            { elementId: 'd', name: 'Delta' },
        ];

        function createTable(type: FavoriteResourceType = 'ASSET') {
            const source = new MatTableDataSource([...rows]);
            const sort = TestBed.runInInjectionContext(() => new MatSort());
            sort.active = 'name';
            sort.direction = 'asc';
            sort.ngOnInit();
            source.sort = sort;
            TestBed.runInInjectionContext(() => {
                store.sortFavoritesFirst(source, type);
            });
            return { source, sort };
        }

        it.each<FavoriteResourceType>([
            'ASSET',
            'ADAPTER',
            'PIPELINE',
            'DASHBOARD',
            'CHART',
        ])('keeps %s favorites first in both sort directions', type => {
            const { source, sort } = createTable(type);
            store.favorites.set(
                ['b', 'd'].map(id => ({
                    ...favorite,
                    resourceType: type,
                    resourceId: id,
                })),
            );
            expect(
                source.sortData([...rows], sort).map(row => row.elementId),
            ).toEqual(['b', 'd', 'a', 'c']);
            sort.direction = 'desc';
            expect(
                source.sortData([...rows], sort).map(row => row.elementId),
            ).toEqual(['d', 'b', 'c', 'a']);
        });

        it('retains the default row order when sorting is cleared', () => {
            const { source, sort } = createTable();
            sort.direction = '';
            store.favorites.set([{ ...favorite, resourceId: 'd' }]);
            expect(
                source.sortData([...rows], sort).map(row => row.elementId),
            ).toEqual(['d', 'a', 'b', 'c']);
            store.favorites.set([]);
            expect(
                source.sortData([...rows], sort).map(row => row.elementId),
            ).toEqual(['a', 'b', 'c', 'd']);
            expect(source.data).toEqual(rows);
        });

        it('preserves custom column accessors and distinguishes resource types', () => {
            const { source, sort } = createTable();
            source.sortingDataAccessor = row => -row.name.length;
            store.favorites.set([
                { ...favorite, resourceType: 'CHART', resourceId: 'a' },
                { ...favorite, resourceId: 'd' },
            ]);
            expect(
                source.sortData([...rows], sort).map(row => row.elementId),
            ).toEqual(['d', 'c', 'a', 'b']);
        });

        it('updates visible rows on load, toggle, rollback and user changes', () => {
            const { source } = createTable();
            let rendered = [] as typeof rows;
            const subscription = source.connect().subscribe(value => {
                rendered = value;
            });
            const ids = () => rendered.map(row => row.elementId);
            store.load();
            http.expectOne(url).flush([{ ...favorite, resourceId: 'd' }]);
            TestBed.tick();
            expect(ids()).toEqual(['d', 'a', 'b', 'c']);

            store.toggle('ASSET', 'c');
            TestBed.tick();
            expect(ids()).toEqual(['c', 'd', 'a', 'b']);
            http.expectOne(req => req.method === 'PUT').flush(null, {
                status: 500,
                statusText: 'Error',
            });
            TestBed.tick();
            expect(ids()).toEqual(['d', 'a', 'b', 'c']);

            store.toggle('ASSET', 'd');
            TestBed.tick();
            expect(ids()).toEqual(['a', 'b', 'c', 'd']);
            http.expectOne(req => req.method === 'DELETE').flush(null);

            store.favorites.set([{ ...favorite, resourceId: 'b' }]);
            TestBed.tick();
            expect(ids()).toEqual(['b', 'a', 'c', 'd']);
            user.next({ username: 'bob' } as UserInfo);
            TestBed.tick();
            expect(ids()).toEqual(['a', 'b', 'c', 'd']);
            subscription.unsubscribe();
            source.disconnect();
        });

        it('ranks favorites before pagination without bypassing filtering', () => {
            const { source } = createTable();
            const paginator = TestBed.runInInjectionContext(
                () => new MatPaginator(),
            );
            paginator.pageSize = 2;
            paginator.ngOnInit();
            source.paginator = paginator;
            source.filterPredicate = row => row.elementId !== 'a';
            source.filter = 'filtered';
            store.favorites.set([{ ...favorite, resourceId: 'd' }]);
            let rendered = [] as typeof rows;
            const subscription = source.connect().subscribe(value => {
                rendered = value;
            });
            TestBed.tick();
            expect(rendered.map(row => row.elementId)).toEqual(['d', 'b']);
            expect(source.filteredData.map(row => row.elementId)).toEqual([
                'b',
                'c',
                'd',
            ]);
            subscription.unsubscribe();
            source.disconnect();
        });
    });
});
