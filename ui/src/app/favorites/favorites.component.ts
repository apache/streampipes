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

import { Component, computed, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FavoriteResourceType,
    FavoriteSummary,
} from '@streampipes/platform-services';
import {
    SpAlertBannerComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpPageHeaderComponent,
    SpSpinnerComponent,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { FavoriteToggleComponent } from './favorite-toggle.component';
import { FavoritesStore } from './favorites.store';

@Component({
    selector: 'sp-favorites',
    styles: [
        `
            .mat-column-favorite {
                box-sizing: border-box;
                width: 56px;
                min-width: 56px;
                max-width: 56px;
                padding-left: 4px !important;
                padding-right: 4px !important;
            }
        `,
    ],
    imports: [
        MatIconButton,
        MatIcon,
        MatTableModule,
        MatTooltip,
        TranslatePipe,
        SpAlertBannerComponent,
        SpBasicViewComponent,
        SpPageHeaderComponent,
        SpSpinnerComponent,
        SpTableComponent,
        FavoriteToggleComponent,
    ],
    template: `
        <sp-basic-view [hideNavbar]="true" [padding]="true">
            <sp-page-header
                [title]="'Favorites' | translate"
                icon="star"
                iconColor="var(--color-primary)"
            >
                <button
                    pageActions
                    type="button"
                    mat-icon-button
                    [attr.aria-label]="'Refresh' | translate"
                    [matTooltip]="'Refresh' | translate"
                    matTooltipPosition="below"
                    color="accent"
                    (click)="store.load()"
                >
                    <mat-icon aria-hidden="true">refresh</mat-icon>
                </button>
            </sp-page-header>
            @if (store.error()) {
                <sp-alert-banner
                    type="error"
                    [title]="
                        'Could not load or update favorites. Please retry.'
                            | translate
                    "
                />
            }
            @if (store.loading()) {
                <sp-spinner [text]="'Loading favorites' | translate" />
            } @else if (store.loaded() && store.favorites().length === 0) {
                <sp-alert-banner
                    type="info"
                    [title]="'No favorites yet' | translate"
                    [description]="
                        'Use the star beside an asset, adapter, pipeline, dashboard or chart to save it here.'
                            | translate
                    "
                />
            } @else {
                <sp-table
                    [dataSource]="rows()"
                    [columns]="columns"
                    [nameSearchConfig]="nameSearchConfig"
                    [groupForRow]="groupByType"
                    [groupLabel]="'Type' | translate"
                    [rowsClickable]="true"
                    (rowClicked)="openFavorite($event)"
                >
                    <ng-container matColumnDef="favorite">
                        <th
                            mat-header-cell
                            *matHeaderCellDef
                            class="favorite-column"
                            [attr.aria-label]="'Favorite' | translate"
                        ></th>
                        <td
                            mat-cell
                            *matCellDef="let favorite"
                            class="favorite-column"
                        >
                            <sp-resource-favorite
                                [resourceType]="favorite.resourceType"
                                [resourceId]="favorite.resourceId"
                            />
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="name">
                        <th mat-header-cell *matHeaderCellDef>
                            {{ 'Name' | translate }}
                        </th>
                        <td mat-cell *matCellDef="let favorite">
                            @if (favorite.available) {
                                <span>{{ favorite.name }}</span>
                            } @else {
                                <span>{{
                                    'Resource deleted or no longer accessible'
                                        | translate
                                }}</span>
                            }
                        </td>
                    </ng-container>
                    <ng-container matColumnDef="type">
                        <th mat-header-cell *matHeaderCellDef>
                            {{ 'Type' | translate }}
                        </th>
                        <td mat-cell *matCellDef="let favorite">
                            <mat-icon aria-hidden="true">{{
                                typeIcons[favorite.resourceType]
                            }}</mat-icon>
                            {{ typeLabels[favorite.resourceType] | translate }}
                        </td>
                    </ng-container>
                </sp-table>
            }
        </sp-basic-view>
    `,
})
export class FavoritesComponent implements OnInit {
    readonly store = inject(FavoritesStore);
    private readonly router = inject(Router);
    private readonly breadcrumbService = inject(SpBreadcrumbService);
    private readonly translateService = inject(TranslateService);
    readonly columns = ['favorite', 'name', 'type'];
    readonly nameSearchConfig = {
        enabled: true,
        searchKey: ['name'] as (keyof FavoriteSummary)[],
    };
    readonly groupByType = (favorite: FavoriteSummary) => ({
        id: favorite.resourceType,
        title: this.translateService.instant(
            this.typeLabels[favorite.resourceType],
        ),
    });
    readonly rows = computed(
        () => new MatTableDataSource(this.store.favorites()),
    );
    readonly typeLabels: Record<FavoriteResourceType, string> = {
        ASSET: 'Asset',
        ADAPTER: 'Adapter',
        PIPELINE: 'Pipeline',
        DASHBOARD: 'Dashboard',
        CHART: 'Chart',
    };
    readonly typeIcons: Record<FavoriteResourceType, string> = {
        ASSET: 'precision_manufacturing',
        ADAPTER: 'power',
        PIPELINE: 'play_arrow',
        DASHBOARD: 'dashboard',
        CHART: 'query_stats',
    };

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink({
                label: 'Favorites',
                link: ['favorites'],
            }),
        );
        this.store.load();
    }

    link(favorite: FavoriteSummary): string[] {
        const id = favorite.resourceId;
        switch (favorite.resourceType) {
            case 'ASSET':
                return ['/assets', 'details', id, 'view'];
            case 'ADAPTER':
                return ['/connect', 'details', id];
            case 'PIPELINE':
                return ['/pipelines', 'details', id];
            case 'DASHBOARD':
                return ['/dashboard', id];
            case 'CHART':
                return ['/chart', id];
        }
    }

    openFavorite(favorite: FavoriteSummary): void {
        if (favorite.available) {
            this.router.navigate(this.link(favorite));
        }
    }
}
