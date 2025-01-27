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
import { FieldConfig, SelectedFilter } from '@streampipes/platform-services';

@Component({
    selector: 'sp-filter-selection-panel-row',
    templateUrl: './filter-selection-panel-row.component.html',
})
export class FilterSelectionPanelRowComponent {
    @Input()
    public filter: SelectedFilter;

    @Input()
    public possibleFields: FieldConfig[];

    @Input()
    public tagValues: Map<string, string[]>;

    @Output()
    public update = new EventEmitter<void>();

    @Output()
    public removeFilter = new EventEmitter<number>();

    constructor() {}

    updateParentComponent() {
        this.update.emit();
    }

    remove() {
        this.removeFilter.emit();
    }
}
