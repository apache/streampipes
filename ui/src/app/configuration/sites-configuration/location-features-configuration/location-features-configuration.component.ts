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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    LocationConfig,
    LocationConfigService,
} from '@streampipes/platform-services';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'sp-location-features-configuration',
    templateUrl: './location-features-configuration.component.html',
})
export class LocationFeaturesConfigurationComponent
    implements OnInit, OnDestroy
{
    @Input()
    locationConfig: LocationConfig;

    locationForm: UntypedFormGroup;
    formSubscription: Subscription;
    showLocationDetails = false;

    constructor(
        private fb: UntypedFormBuilder,
        private snackBar: MatSnackBar,
        private locationConfigService: LocationConfigService,
    ) {}

    ngOnInit(): void {
        this.locationForm = this.fb.group({});
        this.showLocationDetails = this.locationConfig.locationEnabled;
        this.locationForm.addControl(
            'locationFeaturesEnabled',
            new UntypedFormControl(this.locationConfig.locationEnabled),
        );
        this.locationForm.addControl(
            'tileServerUrl',
            new UntypedFormControl(
                this.locationConfig.tileServerUrl,
                this.showLocationDetails ? Validators.required : [],
            ),
        );
        this.locationForm.addControl(
            'attributionText',
            new UntypedFormControl(this.locationConfig.attributionText || ''),
        );
        this.formSubscription = this.locationForm
            .get('locationFeaturesEnabled')
            .valueChanges.subscribe(checked => {
                this.showLocationDetails = checked;
                if (checked) {
                    this.locationForm.controls.tileServerUrl.setValidators(
                        Validators.required,
                    );
                } else {
                    this.locationForm.controls.tileServerUrl.setValidators([]);
                }
            });
    }

    save(): void {
        this.locationConfig.locationEnabled = this.locationForm.get(
            'locationFeaturesEnabled',
        ).value;
        if (this.locationConfig.locationEnabled) {
            this.locationConfig.tileServerUrl =
                this.locationForm.get('tileServerUrl').value;
            this.locationConfig.attributionText =
                this.locationForm.get('attributionText').value;
        }
        this.locationConfigService
            .updateLocationConfig(this.locationConfig)
            .subscribe(() => {
                this.snackBar.open('Location configuration updated', 'Ok', {
                    duration: 3000,
                });
            });
    }

    ngOnDestroy() {
        this.formSubscription?.unsubscribe();
    }
}
