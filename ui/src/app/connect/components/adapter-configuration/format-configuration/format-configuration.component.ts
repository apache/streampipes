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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
    AdapterDescriptionUnion,
    FormatDescription,
    GenericAdapterSetDescription,
    GenericAdapterStreamDescription,
} from '@streampipes/platform-services';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { RestService } from '../../../services/rest.service';
import { MatStepper } from '@angular/material/stepper';

@Component({
    selector: 'sp-format-configuration',
    templateUrl: './format-configuration.component.html',
    styleUrls: ['./format-configuration.component.scss'],
})
export class FormatConfigurationComponent implements OnInit {
    /**
     * Adapter description the selected format is added to
     */
    @Input() adapterDescription: AdapterDescriptionUnion;

    @Output() goBackEmitter: EventEmitter<MatStepper> = new EventEmitter();

    /**
     * Cancels the adapter configuration process
     */
    @Output() removeSelectionEmitter: EventEmitter<boolean> =
        new EventEmitter();

    /**
     * Go to next configuration step when this is complete
     */
    @Output() clickNextEmitter: EventEmitter<MatStepper> = new EventEmitter();

    formatConfigurationValid: boolean;

    /**
     * Local reference of the format description from the adapter description
     */
    selectedFormat: FormatDescription;

    /**
     * The form group to validate the configuration for the format
     */
    formatForm: UntypedFormGroup;

    constructor(
        private restService: RestService,
        private _formBuilder: UntypedFormBuilder,
    ) {}

    ngOnInit(): void {
        // initialize form for validation
        this.formatForm = this._formBuilder.group({});
        this.formatForm.statusChanges.subscribe(status => {
            this.formatConfigurationValid = this.formatForm.valid;
        });

        // ensure that adapter description is a generic adapter
        if (
            this.adapterDescription instanceof GenericAdapterSetDescription ||
            this.adapterDescription instanceof GenericAdapterStreamDescription
        ) {
            this.selectedFormat = this.adapterDescription.formatDescription;
        }
        if (this.adapterDescription instanceof GenericAdapterSetDescription) {
            if (
                (this.adapterDescription as GenericAdapterSetDescription)
                    .formatDescription !== undefined
            ) {
                this.formatConfigurationValid = this.formatForm.valid;
            }
        }
        if (
            this.adapterDescription instanceof GenericAdapterStreamDescription
        ) {
            if (
                (this.adapterDescription as GenericAdapterStreamDescription)
                    .formatDescription !== undefined
            ) {
                this.formatConfigurationValid = this.formatForm.valid;
            }
        }

        this.formatConfigurationValid = false;
    }

    formatSelected(selectedFormat) {
        if (
            this.adapterDescription instanceof GenericAdapterSetDescription ||
            this.adapterDescription instanceof GenericAdapterStreamDescription
        ) {
            this.adapterDescription.formatDescription = selectedFormat;
            this.selectedFormat = selectedFormat;
            if (selectedFormat.config.length === 0) {
                this.formatConfigurationValid = this.formatForm.valid;
            }
        }
    }

    public removeSelection() {
        this.removeSelectionEmitter.emit();
    }

    public clickNext() {
        this.clickNextEmitter.emit();
    }

    public goBack() {
        this.goBackEmitter.emit();
    }
}
