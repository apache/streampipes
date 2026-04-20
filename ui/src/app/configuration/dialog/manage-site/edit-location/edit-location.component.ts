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

import { Component, Input, OnInit } from '@angular/core';
import { AssetSiteDesc, LocationConfig } from '@streampipes/platform-services';
import {
    FormControl,
    FormGroup,
    FormsModule,
    ReactiveFormsModule,
    Validators,
} from '@angular/forms';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { EditAssetLocationAreaComponent } from './edit-location-area/edit-location-area.component';
import { SingleMarkerMapComponent } from '../../../../core-ui/single-marker-map/single-marker-map.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-asset-location-component',
    templateUrl: './edit-location.component.html',
    imports: [
        LayoutDirective,
        FormsModule,
        ReactiveFormsModule,
        SplitSectionComponent,
        FlexDirective,
        MatFormField,
        MatInput,
        MatError,
        EditAssetLocationAreaComponent,
        SingleMarkerMapComponent,
        TranslatePipe,
    ],
})
export class EditAssetLocationComponent implements OnInit {
    @Input()
    site: AssetSiteDesc;

    @Input()
    locationConfig: LocationConfig;

    siteAreaControl: FormGroup;

    ngOnInit() {
        this.siteAreaControl = new FormGroup({
            label: new FormControl(this.site.label || '', [
                Validators.required,
            ]),
            location: new FormControl(this.site.location || null, []),
        });
    }
}
