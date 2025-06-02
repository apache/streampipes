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
    Component,
    ElementRef,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import {
    LabelsService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';
import { MatChipInputEvent } from '@angular/material/chips';
import { FormControl } from '@angular/forms';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Observable, of } from 'rxjs';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { filter, map, startWith } from 'rxjs/operators';
import { SpColorizationService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-asset-details-labels',
    templateUrl: './asset-details-labels.component.html',
    standalone: false,
})
export class AssetDetailsLabelsComponent implements OnInit, OnChanges {
    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    labels: SpLabel[] = [];
    labelTextColors: Record<string, string> = {};

    separatorKeysCodes: number[] = [ENTER, COMMA];
    labelCtrl = new FormControl('');
    filteredLabels: Observable<SpLabel[]>;
    allLabels: SpLabel[] = [];
    labelsAvailable = false;

    @ViewChild('labelInput') labelInput: ElementRef<HTMLInputElement>;

    constructor(
        private labelsService: LabelsService,
        private colorizationService: SpColorizationService,
    ) {}

    ngOnInit(): void {
        this.labelsService.getAllLabels().subscribe(labels => {
            this.allLabels = labels.sort((a, b) =>
                a.label.localeCompare(b.label),
            );
            labels.forEach(
                label =>
                    (this.labelTextColors[label._id] =
                        this.colorizationService.generateContrastColor(
                            label.color,
                        )),
            );
            this.asset.labelIds =
                this.asset.labelIds?.filter(id =>
                    this.allLabels.find(l => l._id === id),
                ) || [];
            this.refreshCurrentLabels();
            this.labelsAvailable = true;
            this.updateFilteredLabels();
        });

        this.filteredLabels = this.labelCtrl.valueChanges.pipe(
            startWith(''),
            map(value => this._filter(value as string)),
        );
    }

    refreshCurrentLabels(): void {
        this.asset.labelIds ??= [];
        this.labels =
            this.asset.labelIds?.map(id =>
                this.allLabels.find(l => l._id === id),
            ) || [];
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset']) {
            this.refreshCurrentLabels();
        }
    }

    getAvailableLabels(): SpLabel[] {
        return this.allLabels.filter(
            label =>
                !this.labels.some(
                    selectedLabel => selectedLabel._id === label._id,
                ),
        );
    }

    add(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();
        if (value) {
            this.addLabelToSelection(value);
        }
        event.chipInput?.clear();
        this.labelCtrl.setValue(null);
    }

    findLabel(value: string): SpLabel {
        return this.allLabels.find(l => l._id === value);
    }

    remove(label: SpLabel): void {
        const index = this.asset.labelIds.indexOf(label._id);
        const labelsIndex = this.labels.findIndex(l => l._id === label._id);
        if (index >= 0) {
            this.labels.splice(labelsIndex, 1);
            this.asset.labelIds.splice(index, 1);
        }
        this.updateFilteredLabels();
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.addLabelToSelection(event.option.value);
        this.labelInput.nativeElement.value = '';
        this.labelCtrl.setValue(null);
    }

    addLabelToSelection(textLabel: string): void {
        const label = this.findLabel(textLabel);
        if (label && !this.labels.some(l => l._id === label._id)) {
            this.labels.push(label);
            this.asset.labelIds.push(label._id);
        }
    }

    private _filter(value: string): SpLabel[] {
        const filterValue = value.toLowerCase();
        return this.getAvailableLabels().filter(label =>
            label.label.toLowerCase().includes(filterValue),
        );
    }

    private updateFilteredLabels(): void {
        this.filteredLabels = this.labelCtrl.valueChanges.pipe(
            startWith(''),
            map(value => this._filter(typeof value === 'string' ? value : '')),
        );
    }
}
