/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { NgModule } from '@angular/core';
import {
    MatButtonModule,
    MatCheckboxModule,
    MatDividerModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatTooltipModule
} from '@angular/material';

import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { InfoComponent } from './info.component';
import { CustomMaterialModule } from "../CustomMaterial/custom-material.module";
import { LicenseComponent } from "./license/license.component";
import { AboutComponent } from "./about/about.component";
import { VersionsComponent } from "./versions/versions.component";
import { VersionInfoService } from "./versions/service/version-info.service";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatButtonModule,
        MatIconModule,
        MatInputModule,
        MatCheckboxModule,
        MatTooltipModule,
        FormsModule,
        CustomMaterialModule,
        MatDividerModule
    ],
    declarations: [
        InfoComponent,
        LicenseComponent,
        VersionsComponent,
        AboutComponent
    ],
    providers: [
        VersionInfoService
    ],
    entryComponents: [
        InfoComponent
    ]
})
export class InfoModule {
}