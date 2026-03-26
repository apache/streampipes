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
    effect,
    inject,
    input,
    output,
    signal,
} from '@angular/core';
import { SpLabel } from '@streampipes/platform-services';
import {
    FormFieldComponent,
    SpColorizationService,
    SpLabelComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ColorPickerDirective } from 'ngx-color-picker';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-label',
    templateUrl: './edit-label.component.html',
    styleUrls: ['./edit-label.component.scss'],
    imports: [
        LayoutDirective,
        NgClass,
        ClassDirective,
        FlexDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        FormFieldComponent,
        SpLabelComponent,
        MatFormField,
        MatInput,
        FormsModule,
        ColorPickerDirective,
        MatButton,
        TranslatePipe,
    ],
})
export class SpEditLabelComponent {
    private colorizationService = inject(SpColorizationService);

    readonly editMode = input(false);

    readonly label = input<SpLabel | undefined>(undefined);

    readonly showPreview = input(true);

    readonly cancelEmitter = output<void>();

    readonly saveEmitter = output<SpLabel>();

    readonly draftLabel = signal<SpLabel>(this.createDefaultLabel());

    constructor() {
        effect(() => {
            const label = this.label();
            this.draftLabel.set(
                label ? { ...label } : this.createDefaultLabel(),
            );
        });
    }

    saveLabel(): void {
        this.saveEmitter.emit({ ...this.draftLabel() });
        if (this.showPreview()) {
            this.draftLabel.update(label => ({
                ...label,
                color: this.colorizationService.generateRandomColor(),
            }));
        }
    }

    updateLabelName(label: string): void {
        this.updateDraftLabel({ label });
    }

    updateDescription(description: string): void {
        this.updateDraftLabel({ description });
    }

    updateColor(color: string): void {
        this.updateDraftLabel({ color });
    }

    private updateDraftLabel(partial: Partial<SpLabel>): void {
        this.draftLabel.update(label => ({
            ...label,
            ...partial,
        }));
    }

    private createDefaultLabel(): SpLabel {
        return {
            color: this.colorizationService.generateRandomColor(),
            label: 'New label',
            description: '',
        };
    }
}
