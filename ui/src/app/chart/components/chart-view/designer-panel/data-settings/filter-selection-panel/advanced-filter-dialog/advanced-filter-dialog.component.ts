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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    DialogRef,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import {
    FieldConfig,
    FilterExpressionCondition,
    FilterExpressionGroup,
    SelectedFilter,
} from '@streampipes/platform-services';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { NgTemplateOutlet } from '@angular/common';
import { AdvancedFilterConditionRowComponent } from './advanced-filter-condition-row/advanced-filter-condition-row.component';
import { TranslatePipe } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout';
import { MatDivider } from '@angular/material/list';
import { FilterExpressionPreviewService } from '../filter-expression-preview.service';

export interface AdvancedFilterDialogResult {
    action: 'save' | 'clear';
    expression?: FilterExpressionGroup;
}

@Component({
    selector: 'sp-advanced-filter-dialog',
    templateUrl: './advanced-filter-dialog.component.html',
    styleUrls: ['./advanced-filter-dialog.component.scss'],
    imports: [
        SplitSectionComponent,
        MatButton,
        MatIconButton,
        MatFormField,
        MatSelect,
        MatOption,
        NgTemplateOutlet,
        AdvancedFilterConditionRowComponent,
        TranslatePipe,
        SpAlertBannerComponent,
        LayoutGapDirective,
        MatDivider,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
    ],
})
export class AdvancedFilterDialogComponent implements OnInit {
    @Input()
    existingExpression?: FilterExpressionGroup;

    @Input()
    selectedFilters: SelectedFilter[] = [];

    @Input()
    possibleFields: FieldConfig[] = [];

    @Input()
    tagValues: Map<string, string[]> = new Map<string, string[]>();

    private dialogRef = inject(DialogRef<AdvancedFilterDialogComponent>);
    private filterExpressionPreviewService = inject(
        FilterExpressionPreviewService,
    );

    expression: FilterExpressionGroup = this.createEmptyGroup();
    validationMessage?: string;

    ngOnInit(): void {
        if (this.existingExpression) {
            this.expression = this.cloneGroup(this.existingExpression);
            return;
        }

        const expressionFromSimple = this.buildExpressionFromSimpleFilters(
            this.selectedFilters,
        );
        this.expression = expressionFromSimple ?? this.createEmptyGroup();
    }

    addCondition(group: FilterExpressionGroup): void {
        group.children.push(this.createEmptyCondition());
        this.validationMessage = undefined;
    }

    addGroup(group: FilterExpressionGroup): void {
        group.children.push(this.createEmptyGroup());
        this.validationMessage = undefined;
    }

    removeChild(group: FilterExpressionGroup, index: number): void {
        group.children.splice(index, 1);
    }

    close(): void {
        this.dialogRef.close();
    }

    clearAdvancedFilter(): void {
        this.dialogRef.close({ action: 'clear' } as AdvancedFilterDialogResult);
    }

    save(): void {
        const validationError = this.validateGroup(this.expression);
        if (validationError) {
            this.validationMessage = validationError;
            return;
        }

        this.dialogRef.close({
            action: 'save',
            expression: this.cloneGroup(this.expression),
        } as AdvancedFilterDialogResult);
    }

    onExpressionChanged(): void {
        this.validationMessage = undefined;
    }

    hasPreview(): boolean {
        return this.expression?.children?.length > 0;
    }

    previewSummary(): string {
        if (!this.hasPreview()) {
            return '';
        }

        return this.filterExpressionPreviewService.format(this.expression);
    }

    formatGroupLabel(depth: number): string {
        return depth === 0 ? 'Root group' : 'Group';
    }

    private createEmptyGroup(): FilterExpressionGroup {
        return {
            type: 'group',
            operator: 'AND',
            children: [],
        };
    }

    private createEmptyCondition(): FilterExpressionCondition {
        return {
            type: 'condition',
            field: '',
            operator: '=',
            condition: '',
        };
    }

    private cloneGroup(group: FilterExpressionGroup): FilterExpressionGroup {
        return {
            type: 'group',
            operator: group.operator ?? 'AND',
            children: group.children.map(child =>
                child.type === 'group'
                    ? this.cloneGroup(child)
                    : {
                          type: 'condition',
                          field: child.field,
                          operator: child.operator,
                          condition: child.condition,
                      },
            ),
        };
    }

    private buildExpressionFromSimpleFilters(
        filters: SelectedFilter[],
    ): FilterExpressionGroup | undefined {
        const validFilters = filters.filter(filter =>
            this.isValidSimpleFilter(filter),
        );
        if (validFilters.length === 0) {
            return undefined;
        }

        const root: FilterExpressionGroup = {
            type: 'group',
            operator: 'AND',
            children: [],
        };

        validFilters.forEach((filter, index) => {
            if (index > 0 && filter.chainingOperator === 'OR') {
                // Preserve flat OR chains when opening the advanced editor.
                root.operator = 'OR';
            }

            root.children.push({
                type: 'condition',
                field: filter.field?.runtimeName ?? '',
                operator: filter.operator,
                condition: filter.value,
            });
        });

        // For mixed AND/OR flat chains, preserve left-associative semantics.
        if (
            validFilters.some(
                (filter, index) =>
                    index > 0 && filter.chainingOperator === 'OR',
            ) &&
            validFilters.some(
                (filter, index) =>
                    index > 0 && filter.chainingOperator !== 'OR',
            )
        ) {
            let expression: FilterExpressionCondition | FilterExpressionGroup =
                root.children[0] as FilterExpressionCondition;

            for (let i = 1; i < validFilters.length; i++) {
                const current = validFilters[i];
                expression = {
                    type: 'group',
                    operator: current.chainingOperator ?? 'AND',
                    children: [expression, root.children[i]],
                };
            }

            return expression.type === 'group'
                ? expression
                : {
                      type: 'group',
                      operator: 'AND',
                      children: [expression],
                  };
        }

        return root;
    }

    private isValidSimpleFilter(filter: SelectedFilter): boolean {
        const hasValue =
            filter.value !== undefined &&
            filter.value !== null &&
            filter.value !== '';
        return !!filter.field && !!filter.operator && hasValue;
    }

    private validateGroup(group: FilterExpressionGroup): string | undefined {
        if (!group.children?.length) {
            return 'Every group must contain at least one condition or sub-group.';
        }

        for (const child of group.children) {
            if (child.type === 'group') {
                const nestedError = this.validateGroup(child);
                if (nestedError) {
                    return nestedError;
                }
            } else {
                const hasConditionValue =
                    child.condition !== undefined &&
                    child.condition !== null &&
                    child.condition !== '';
                if (!child.field || !child.operator || !hasConditionValue) {
                    return 'Please complete all fields, operators and values before applying the advanced filter.';
                }
            }
        }

        return undefined;
    }
}
