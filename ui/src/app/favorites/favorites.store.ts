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

import {
    DestroyRef,
    Injectable,
    effect,
    inject,
    signal,
    untracked,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentUserService } from '@streampipes/shared-ui';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import {
    FavoritesService,
    FavoriteResourceType,
    FavoriteSummary,
} from '@streampipes/platform-services';
import {
    distinctUntilChanged,
    finalize,
    map,
    Observable,
    Subject,
    takeUntil,
} from 'rxjs';

@Injectable()
export class FavoritesStore {
    private readonly api = inject(FavoritesService);
    private readonly destroyRef = inject(DestroyRef);
    private readonly snackBar = inject(MatSnackBar);
    private readonly translate = inject(TranslateService);
    private readonly userChanged = new Subject<void>();
    readonly favorites = signal<FavoriteSummary[]>([]);
    readonly loading = signal(false);
    readonly loaded = signal(false);
    readonly error = signal(false);
    readonly pending = signal<Set<string>>(new Set());
    private revision = 0;

    constructor() {
        inject(CurrentUserService)
            .user$.pipe(
                map(user => user?.username),
                distinctUntilChanged(),
                takeUntilDestroyed(),
            )
            .subscribe(() => {
                this.userChanged.next();
                this.revision++;
                this.favorites.set([]);
                this.pending.set(new Set());
                this.loaded.set(false);
                this.error.set(false);
            });
    }

    private reportError(): void {
        this.error.set(true);
        this.snackBar.open(
            this.translate.instant(
                'Could not load or update favorites. Please retry.',
            ),
            this.translate.instant('Close'),
            { duration: 6000 },
        );
    }

    key(type: FavoriteResourceType, id: string): string {
        return JSON.stringify([type, id]);
    }

    selected(type: FavoriteResourceType, id: string): boolean {
        return this.favorites().some(
            item => item.resourceType === type && item.resourceId === id,
        );
    }

    sortFavoritesFirst<T extends { elementId: string }>(
        dataSource: MatTableDataSource<T>,
        type: FavoriteResourceType,
    ): void {
        const sortData = dataSource.sortData;
        dataSource.sortData = (rows, sort) => {
            const favoriteIds = new Set(
                this.favorites()
                    .filter(favorite => favorite.resourceType === type)
                    .map(favorite => favorite.resourceId),
            );
            const sorted: T[] = sortData.call(dataSource, rows, sort);
            return [
                ...sorted.filter(row => favoriteIds.has(row.elementId)),
                ...sorted.filter(row => !favoriteIds.has(row.elementId)),
            ];
        };

        effect(() => {
            this.favorites();
            untracked(() => {
                dataSource.data = [...dataSource.data];
            });
        });
        inject(DestroyRef).onDestroy(() => {
            dataSource.sortData = sortData;
        });
    }

    load(): void {
        if (this.loading() || this.pending().size) {
            return;
        }
        const revision = ++this.revision;
        this.loading.set(true);
        this.error.set(false);
        this.api
            .list()
            .pipe(
                takeUntil(this.userChanged),
                takeUntilDestroyed(this.destroyRef),
                finalize(() => this.loading.set(false)),
            )
            .subscribe({
                next: items => {
                    if (revision === this.revision) {
                        this.favorites.set(items);
                        this.loaded.set(true);
                    }
                },
                error: () => this.reportError(),
            });
    }

    toggle(type: FavoriteResourceType, id: string): void {
        const key = this.key(type, id);
        if (!this.loaded() || this.loading() || this.pending().has(key)) {
            return;
        }
        this.pending.update(value => new Set([...value, key]));
        this.error.set(false);
        const previous = this.favorites().find(
            item => this.key(item.resourceType, item.resourceId) === key,
        );
        const selected = previous !== undefined;
        this.favorites.update(items =>
            selected
                ? items.filter(
                      item =>
                          this.key(item.resourceType, item.resourceId) !== key,
                  )
                : [
                      ...items,
                      {
                          resourceType: type,
                          resourceId: id,
                          name: id,
                          available: true,
                      },
                  ],
        );
        const request: Observable<FavoriteSummary | void> = selected
            ? this.api.remove(type, id)
            : this.api.add(type, id);
        request
            .pipe(
                takeUntil(this.userChanged),
                takeUntilDestroyed(this.destroyRef),
                finalize(() =>
                    this.pending.update(value => {
                        const next = new Set(value);
                        next.delete(key);
                        return next;
                    }),
                ),
            )
            .subscribe({
                next: result => {
                    this.favorites.update(items => {
                        const remaining = items.filter(
                            item =>
                                this.key(item.resourceType, item.resourceId) !==
                                key,
                        );
                        return selected
                            ? remaining
                            : [...remaining, result as FavoriteSummary];
                    });
                },
                error: () => {
                    this.favorites.update(items => {
                        const remaining = items.filter(
                            item =>
                                this.key(item.resourceType, item.resourceId) !==
                                key,
                        );
                        return previous ? [...remaining, previous] : remaining;
                    });
                    this.reportError();
                },
            });
    }
}
