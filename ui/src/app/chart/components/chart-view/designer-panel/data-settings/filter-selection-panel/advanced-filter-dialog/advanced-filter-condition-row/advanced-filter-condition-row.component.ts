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
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
} from '@angular/core';
import {
    FieldConfig,
    FilterExpressionCondition,
    SelectedFilter,
} from '@streampipes/platform-services';
import { FilterSelectionPanelRowPropertySelectionComponent } from '../../filter-selection-panel-row/panel-row-property-selection/filter-selection-panel-row-property-selection.component';
import { FilterSelectionPanelRowOperationSelectionComponent } from '../../filter-selection-panel-row/panel-row-operation-selection/filter-selection-panel-row-operation-selection.component';
import { FilterSelectionPanelRowValueInputComponent } from '../../filter-selection-panel-row/panel-row-value-input/filter-selection-panel-row-value-input.component';
import { FilterSelectionPanelRowValueAutocompleteComponent } from '../../filter-selection-panel-row/panel-row-value-input-autocomplete/filter-selection-panel-row-value-autocomplete.component';
import { MatIconButton } from '@angular/material/button';

@Component({
    selector: 'sp-advanced-filter-condition-row',
    templateUrl: './advanced-filter-condition-row.component.html',
    imports: [
        FilterSelectionPanelRowPropertySelectionComponent,
        FilterSelectionPanelRowOperationSelectionComponent,
        FilterSelectionPanelRowValueInputComponent,
        FilterSelectionPanelRowValueAutocompleteComponent,
        MatIconButton,
    ],
})
export class AdvancedFilterConditionRowComponent implements OnChanges {
    @Input()
    condition: FilterExpressionCondition;

    @Input()
    possibleFields: FieldConfig[] = [];

    @Input()
    tagValues: Map<string, string[]> = new Map<string, string[]>();

    @Output()
    update = new EventEmitter<void>();

    @Output()
    remove = new EventEmitter<void>();

    filterModel: SelectedFilter = {
        operator: '=',
        value: '',
    };

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['condition'] || changes['possibleFields']) {
            this.syncFromCondition();
        }
    }

    onUpdate(): void {
        this.syncToCondition();
        this.update.emit();
    }

    onRemove(): void {
        this.remove.emit();
    }

    private syncFromCondition(): void {
        const selectedField = this.possibleFields.find(
            field => field.runtimeName === this.condition?.field,
        );

        this.filterModel = {
            field: selectedField as any,
            operator: this.condition?.operator ?? '=',
            value: this.condition?.condition ?? '',
        };
    }

    private syncToCondition(): void {
        this.condition.field = this.filterModel.field?.runtimeName;
        this.condition.operator = this.filterModel.operator;
        this.condition.condition = this.filterModel.value;
    }
}
