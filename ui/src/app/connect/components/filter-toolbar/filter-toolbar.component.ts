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

import { Component, EventEmitter, Output } from '@angular/core';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatFormField, MatPrefix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-connect-filter-toolbar',
    templateUrl: './filter-toolbar.component.html',
    styleUrls: ['./filter-toolbar.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        MatFormField,
        MatInput,
        MatIcon,
        MatPrefix,
        TranslatePipe,
    ],
})
export class SpConnectFilterToolbarComponent {
    @Output()
    filterChangedEmitter: EventEmitter<AdapterFilterSettingsModel> =
        new EventEmitter<AdapterFilterSettingsModel>();

    currentFilter: AdapterFilterSettingsModel = {
        textFilter: '',
    };

    updateFilterTerm(event: string) {
        this.currentFilter.textFilter = event;
        this.filterChangedEmitter.emit(this.currentFilter);
    }
}
