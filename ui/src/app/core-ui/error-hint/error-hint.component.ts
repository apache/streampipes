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

import { Component, computed, input, signal } from '@angular/core';
import { UserErrorMessage } from '../../core-model/base/UserErrorMessage';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';

@Component({
    selector: 'sp-error-hint',
    templateUrl: './error-hint.component.html',
    styleUrls: ['./error-hint.component.scss'],
    imports: [LayoutDirective, LayoutAlignDirective],
})
export class ErrorHintComponent {
    readonly errorMessages = input<UserErrorMessage[]>([]);
    readonly displayMessages = input(true);
    readonly validationString = input('');

    readonly errorMessagesDisplayed = signal(false);
    readonly errorCount = computed(() => this.errorMessages().length);
    readonly hasErrorMessages = computed(() => this.errorCount() > 0);

    public toggleErrorMessagesDisplayed() {
        this.errorMessagesDisplayed.update(displayed => !displayed);
    }
}
