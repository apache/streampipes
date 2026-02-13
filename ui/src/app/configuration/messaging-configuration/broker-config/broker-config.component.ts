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
import {
    FlexDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-messaging-broker-config',
    templateUrl: './broker-config.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        FormFieldComponent,
        LayoutGapDirective,
        MatFormField,
        MatInput,
        FormsModule,
        TranslatePipe,
    ],
})
export class SpMessagingBrokerConfigComponent {
    @Input()
    title: string;

    @Input()
    host: string;

    @Input()
    port: number;

    @Output()
    hostChange = new EventEmitter<string>();

    @Output()
    portChange = new EventEmitter<number>();
}
