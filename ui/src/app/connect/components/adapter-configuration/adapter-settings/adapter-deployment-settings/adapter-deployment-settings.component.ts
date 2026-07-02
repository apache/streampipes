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
    ElementRef,
    Input,
    OnInit,
    ViewChild,
    inject,
} from '@angular/core';
import {
    ExtensionDeploymentConfiguration,
    ServiceTagService,
    SpServiceTag,
} from '@streampipes/platform-services';
import { Observable } from 'rxjs';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { map, startWith } from 'rxjs/operators';
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import {
    MatRadioButton,
    MatRadioChange,
    MatRadioGroup,
} from '@angular/material/radio';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatIcon } from '@angular/material/icon';
import { MatOption } from '@angular/material/select';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-adapter-deployment-settings',
    templateUrl: './adapter-deployment-settings.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        MatChipsModule,
        MatIcon,
        MatAutocompleteTrigger,
        ReactiveFormsModule,
        MatAutocomplete,
        MatOption,
        AsyncPipe,
        TranslatePipe,
    ],
})
export class SpAdapterDeploymentSettingsComponent implements OnInit {
    private serviceTagService = inject(ServiceTagService);

    @Input()
    deploymentConfiguration: ExtensionDeploymentConfiguration;

    availableServiceTags: SpServiceTag[] = [];
    availableServiceTagValues: string[] = [];

    deploymentMode = 'all';

    separatorKeysCodes: number[] = [ENTER, COMMA];
    serviceTagCtrl = new FormControl('');
    filteredServiceTags: Observable<string[]>;

    @ViewChild('serviceTagInput') serviceTagInput: ElementRef<HTMLInputElement>;

    constructor() {
        this.filteredServiceTags = this.serviceTagCtrl.valueChanges.pipe(
            startWith(null),
            map((serviceTagValue: string | null) => {
                return serviceTagValue
                    ? this._filter(serviceTagValue)
                    : this.availableServiceTagValues.slice();
            }),
        );
    }

    ngOnInit(): void {
        if (this.deploymentConfiguration.desiredServiceTags.length > 0) {
            this.deploymentMode = 'filter';
        }
        this.serviceTagService.getCustomServiceTags().subscribe(res => {
            this.availableServiceTags = res;
            this.availableServiceTagValues = res.map(st => st.value);
        });
    }

    add(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();

        if (value) {
            this.deploymentConfiguration.desiredServiceTags.push(
                this.findTag(value),
            );
        }

        if (event.chipInput) {
            event.chipInput.clear();
        }

        this.serviceTagCtrl.setValue(null);
    }

    findTag(value: string): SpServiceTag {
        return this.availableServiceTags.find(st => st.value === value);
    }

    remove(serviceTag: string): void {
        const index = this.deploymentConfiguration.desiredServiceTags.findIndex(
            st => st.value === serviceTag,
        );

        if (index >= 0) {
            this.deploymentConfiguration.desiredServiceTags.splice(index, 1);
        }
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.deploymentConfiguration.desiredServiceTags.push(
            this.findTag(event.option.viewValue),
        );
        this.serviceTagInput.nativeElement.value = '';
        this.serviceTagCtrl.setValue(null);
    }

    private _filter(value: string): string[] {
        const filterValue = value.toLowerCase();

        return this.availableServiceTagValues.filter(st =>
            st.toLowerCase().includes(filterValue),
        );
    }

    handleSelectionChange(event: MatRadioChange): void {
        if (event.value === 'all') {
            this.deploymentConfiguration.desiredServiceTags = [];
        }
    }
}
