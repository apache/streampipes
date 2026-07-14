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
    ChangeDetectionStrategy,
    Component,
    ContentChild,
    ViewChild,
    booleanAttribute,
    computed,
    inject,
    input,
    model,
    signal,
} from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import {
    MatChipGrid,
    MatChipRow,
    MatChipsModule,
} from '@angular/material/chips';
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatOption } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import {
    SearchSelectChipTemplateDirective,
    SearchSelectOptionTemplateDirective,
} from './search-select-template.directive';
import { SpColorizationService } from '../../services/colorization.service';
import { SpLabelComponent } from '../sp-label/sp-label.component';

const DISPLAY_VALUE_FIELDS = [
    'label',
    'name',
    'measureName',
    'title',
    'email',
    'groupName',
    'value',
    'filename',
    'assetName',
    '_id',
    'id',
];

const IDENTITY_FIELDS = [
    '_id',
    'id',
    'measureName',
    'principalId',
    'groupId',
    'value',
    'filename',
    'assetName',
];

@Component({
    selector: 'sp-search-select',
    templateUrl: './search-select.component.html',
    styleUrls: ['./search-select.component.scss'],
    imports: [
        MatAutocomplete,
        MatAutocompleteTrigger,
        MatChipGrid,
        MatChipRow,
        MatChipsModule,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatOption,
        MatPrefix,
        MatSuffix,
        NgTemplateOutlet,
        SpLabelComponent,
        TranslatePipe,
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchSelectComponent<T = unknown> {
    private colorizationService = inject(SpColorizationService);

    readonly items = input<T[]>([]);
    readonly multiple = input(false);
    readonly disabled = input(false, { transform: booleanAttribute });
    readonly placeholder = input<string | undefined>();
    readonly dataCy = input<string | undefined>();

    readonly value = model<T | T[] | undefined>(undefined);

    readonly searchText = signal('');
    readonly panelOpen = signal(false);

    @ContentChild(SearchSelectOptionTemplateDirective)
    optionTemplate?: SearchSelectOptionTemplateDirective<T>;

    @ContentChild(SearchSelectChipTemplateDirective)
    chipTemplate?: SearchSelectChipTemplateDirective<T>;

    @ViewChild(MatAutocompleteTrigger)
    autocompleteTrigger?: MatAutocompleteTrigger;

    readonly selectedItems = computed(() => {
        const value = this.value();

        if (!this.multiple()) {
            return value && !Array.isArray(value) ? [value] : [];
        }

        return Array.isArray(value) ? value : [];
    });

    readonly selectedValue = computed(() => this.selectedItems()[0]);

    readonly selectedText = computed(() => {
        const selected = this.selectedValue();
        return selected ? this.displayValue(selected) : '';
    });

    readonly inputValue = computed(() =>
        this.panelOpen() || this.multiple()
            ? this.searchText()
            : this.selectedText(),
    );

    readonly placeholderText = computed(() => {
        if (this.placeholder()) {
            return this.placeholder();
        }

        return this.multiple() ? 'Add' : 'Select';
    });

    readonly filteredItems = computed(() => {
        const query = this.searchText().trim().toLowerCase();

        if (!query) {
            return this.items();
        }

        return this.items().filter(item =>
            this.displayValue(item).toLowerCase().includes(query),
        );
    });

    readonly autocompleteDisplayWith = (item: T): string =>
        this.displayValue(item);

    onInput(value: string): void {
        this.searchText.set(value);
    }

    onOpened(): void {
        this.panelOpen.set(true);
        this.searchText.set('');
    }

    onClosed(): void {
        this.panelOpen.set(false);
        this.searchText.set('');
    }

    onSelected(event: MatAutocompleteSelectedEvent): void {
        const item = event.option.value as T;

        if (this.multiple()) {
            this.toggleItem(item);
            setTimeout(() => this.autocompleteTrigger?.openPanel());
        } else {
            this.value.set(item);
        }

        this.searchText.set('');
    }

    removeItem(item: T): void {
        if (!this.multiple()) {
            if (this.isSelected(item)) {
                this.value.set(undefined);
            }
            return;
        }

        this.value.set(
            this.selectedItems().filter(
                selected => !this.compare(selected, item),
            ),
        );
    }

    clearValue(): void {
        this.value.set(this.multiple() ? [] : undefined);
        this.searchText.set('');
    }

    isSelected(item: T): boolean {
        return this.selectedItems().some(selected =>
            this.compare(selected, item),
        );
    }

    displayValue(item: T): string {
        if (item === undefined || item === null) {
            return '';
        }

        if (
            typeof item === 'string' ||
            typeof item === 'number' ||
            typeof item === 'boolean'
        ) {
            return String(item);
        }

        const record = this.toRecord(item);
        const displayValue = DISPLAY_VALUE_FIELDS.map(
            field => record?.[field],
        ).find(value => value !== undefined && value !== null);

        return displayValue ? String(displayValue) : '';
    }

    itemColor(item: T): string | undefined {
        const color = this.toRecord(item)?.['color'];
        return typeof color === 'string' ? color : undefined;
    }

    itemContrastColor(item: T): string | undefined {
        const color = this.itemColor(item);
        return color
            ? this.colorizationService.generateContrastColor(color)
            : undefined;
    }

    protected trackItem = (_index: number, item: T): string =>
        this.itemKey(item);

    private toggleItem(item: T): void {
        if (this.isSelected(item)) {
            this.removeItem(item);
        } else {
            this.value.set([...this.selectedItems(), item]);
        }
    }

    private compare(a: T, b: T): boolean {
        if (a === b) {
            return true;
        }

        const aRecord = this.toRecord(a);
        const bRecord = this.toRecord(b);

        return IDENTITY_FIELDS.some(key => {
            const aValue = aRecord?.[key];
            const bValue = bRecord?.[key];
            return aValue !== undefined && aValue === bValue;
        });
    }

    private itemKey(item: T): string {
        const record = this.toRecord(item);
        const key = IDENTITY_FIELDS.map(field => record?.[field]).find(
            value => value !== undefined && value !== null,
        );

        return String(key ?? this.displayValue(item));
    }

    private toRecord(item: T): Record<string, unknown> | undefined {
        if (item === undefined || item === null || typeof item !== 'object') {
            return undefined;
        }

        return item as Record<string, unknown>;
    }
}
