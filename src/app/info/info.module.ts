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