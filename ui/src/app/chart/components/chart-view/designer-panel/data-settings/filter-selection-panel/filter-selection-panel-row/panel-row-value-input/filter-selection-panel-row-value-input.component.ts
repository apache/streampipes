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
    OnInit,
    Output,
    inject,
} from '@angular/core';
import { SelectedFilter } from '@streampipes/platform-services';
import { EscapeNumberFilterService } from '../escape-number-filter.service';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-filter-selection-panel-row-value-input',
    templateUrl: './filter-selection-panel-row-value-input.component.html',
    imports: [MatFormField, MatInput, FormsModule, TranslatePipe],
})
export class FilterSelectionPanelRowValueInputComponent implements OnInit {
    private escapeNumberFilterService = inject(EscapeNumberFilterService);

    @Input()
    public filter: SelectedFilter;

    // This is only required to correctly escape numbers
    @Input()
    public tagValues: Map<string, string[]>;

    @Output()
    public update = new EventEmitter<void>();

    public value: string;

    ngOnInit(): void {
        this.value = this.escapeNumberFilterService.removeEnclosingQuotes(
            this.filter.value,
        );
    }

    updateParentComponent() {
        this.filter.value = this.escapeNumberFilterService.escapeIfNumberValue(
            this.filter,
            this.value,
            this.tagValues,
        );
        this.update.emit();
    }
}
