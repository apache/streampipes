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

import { Component, OnDestroy, OnInit } from '@angular/core';
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
    locationConfig: LocationConfig;

    parentForm: UntypedFormGroup;
    formSubscription: Subscription;
    showTileUrlInput = false;

    constructor(
        private fb: UntypedFormBuilder,
        private snackBar: MatSnackBar,
        private locationConfigService: LocationConfigService,
    ) {}

    ngOnInit(): void {
        this.parentForm = this.fb.group({});
        this.locationConfigService.getLocationConfig().subscribe(res => {
            this.locationConfig = res;
            this.showTileUrlInput = res.locationEnabled;
            this.parentForm.addControl(
                'locationFeaturesEnabled',
                new UntypedFormControl(this.locationConfig.locationEnabled),
            );
            this.parentForm.addControl(
                'tileServerUrl',
                new UntypedFormControl(
                    this.locationConfig.tileServerUrl,
                    this.showTileUrlInput ? Validators.required : [],
                ),
            );
            this.formSubscription = this.parentForm
                .get('locationFeaturesEnabled')
                .valueChanges.subscribe(checked => {
                    this.showTileUrlInput = checked;
                    if (checked) {
                        this.parentForm.controls.tileServerUrl.setValidators(
                            Validators.required,
                        );
                    } else {
                        this.parentForm.controls.tileServerUrl.setValidators(
                            [],
                        );
                    }
                });
        });
    }

    save(): void {
        this.locationConfig.locationEnabled = this.parentForm.get(
            'locationFeaturesEnabled',
        ).value;
        if (this.locationConfig.locationEnabled) {
            this.locationConfig.tileServerUrl =
                this.parentForm.get('tileServerUrl').value;
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
