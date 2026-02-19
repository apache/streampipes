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

import { Component, Input } from '@angular/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-alternate-id-configuration',
    templateUrl: './alternate-id-configuration.component.html',
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MatIconButton,
        MatIcon,
        LayoutGapDirective,
        MatFormField,
        MatLabel,
        MatInput,
        FormsModule,
        MatButton,
        TranslatePipe,
    ],
})
export class AlternateIdConfigurationComponent {
    @Input()
    alternateIds: string[] = [];

    newAlternateId: string = '';

    addAlternateId(): void {
        if (!this.alternateIds) {
            this.alternateIds = [];
        }
        this.alternateIds.push(this.newAlternateId);
    }

    removeAlternateId(id: string): void {
        this.alternateIds.splice(this.alternateIds.indexOf(id), 1);
    }
}
