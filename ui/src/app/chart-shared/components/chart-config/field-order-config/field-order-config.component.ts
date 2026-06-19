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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DataExplorerField } from '@streampipes/platform-services';
import {
    CdkDrag,
    CdkDragDrop,
    CdkDragHandle,
    CdkDropList,
    moveItemInArray,
} from '@angular/cdk/drag-drop';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { SpAlertBannerComponent } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import { SplitSectionComponent } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-field-order-config',
    templateUrl: './field-order-config.component.html',
    styleUrls: ['./field-order-config.component.scss'],
    imports: [
        SplitSectionComponent,
        CdkDropList,
        CdkDrag,
        CdkDragHandle,
        LayoutDirective,
        LayoutAlignDirective,
        MatIcon,
        MatIconButton,
        SpAlertBannerComponent,
        TranslatePipe,
    ],
})
export class FieldOrderConfigComponent {
    @Input() selectedFields: DataExplorerField[] = [];
    @Input() sectionTitle = 'Field Order';
    @Input() emptyMessage = 'No fields selected.';

    @Output() selectedFieldsChange = new EventEmitter<DataExplorerField[]>();

    dropSelectedField(event: CdkDragDrop<DataExplorerField[]>): void {
        if (event.previousIndex === event.currentIndex) {
            return;
        }

        const fields = [...(this.selectedFields ?? [])];
        moveItemInArray(fields, event.previousIndex, event.currentIndex);
        this.selectedFieldsChange.emit(fields);
    }

    moveSelectedField(fromIndex: number, offset: number): void {
        const fields = [...(this.selectedFields ?? [])];
        const targetIndex = fromIndex + offset;

        if (
            fromIndex < 0 ||
            targetIndex < 0 ||
            fromIndex >= fields.length ||
            targetIndex >= fields.length
        ) {
            return;
        }

        const [movedField] = fields.splice(fromIndex, 1);
        fields.splice(targetIndex, 0, movedField);
        this.selectedFieldsChange.emit(fields);
    }

    canMoveSelectedFieldUp(index: number): boolean {
        return index > 0;
    }

    canMoveSelectedFieldDown(index: number): boolean {
        return index < (this.selectedFields?.length ?? 0) - 1;
    }

    selectedFieldLabel(field: DataExplorerField): string {
        return `${field.runtimeName} (${field.measure})`;
    }

    fieldKey(field: DataExplorerField): string {
        return `${field.fullDbName}:${field.sourceIndex}`;
    }
}
