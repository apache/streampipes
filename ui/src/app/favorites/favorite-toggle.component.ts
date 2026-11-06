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

import { Component, inject, input, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { MatButton } from '@angular/material/button';
import { FavoriteResourceType } from '@streampipes/platform-services';
import { SpFavoriteButtonComponent } from '@streampipes/shared-ui';
import { FavoritesStore } from './favorites.store';

@Component({
    selector: 'sp-resource-favorite',
    imports: [SpFavoriteButtonComponent, TranslatePipe, MatButton],
    template: `
        <sp-favorite-button
            [selected]="store.selected(resourceType(), resourceId())"
            [disabled]="
                !store.loaded() ||
                store.loading() ||
                store.pending().has(store.key(resourceType(), resourceId()))
            "
            [label]="
                (store.selected(resourceType(), resourceId())
                    ? 'Remove from favorites'
                    : 'Add to favorites'
                ) | translate
            "
            (toggled)="store.toggle(resourceType(), resourceId())"
        />
        @if (store.error() && !store.loaded()) {
            <button
                type="button"
                mat-button
                (click)="$event.stopPropagation(); store.load()"
            >
                {{ 'Retry' | translate }}
            </button>
        }
    `,
})
export class FavoriteToggleComponent implements OnInit {
    readonly store = inject(FavoritesStore);
    readonly resourceType = input.required<FavoriteResourceType>();
    readonly resourceId = input.required<string>();

    ngOnInit(): void {
        if (!this.store.loaded()) {
            this.store.load();
        }
    }
}
