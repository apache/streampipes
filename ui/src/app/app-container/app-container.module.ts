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

import { NgModule, COMPILER_OPTIONS, CompilerFactory, Compiler } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';

import { AppContainerComponent } from './app-container.component';
import { AppContainerService } from './shared/app-container.service';
import { ViewComponent } from './view/view.component';

import { JitCompilerFactory } from '@angular/platform-browser-dynamic';
import { MatDialogModule } from '@angular/material';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatDialogModule,
        CustomMaterialModule
    ],
    declarations: [
        AppContainerComponent,
        ViewComponent
    ],
    providers: [
        AppContainerService,
        {
            provide: COMPILER_OPTIONS,
            useValue: {},
            multi: true
        },
        {
            provide: CompilerFactory,
            useClass: JitCompilerFactory,
            deps: [COMPILER_OPTIONS]
        },
        {
            provide: Compiler,
            useFactory: (fn: CompilerFactory) => {
                return fn.createCompiler();
            },
            deps: [CompilerFactory]
        },
    ],
    entryComponents: [
        AppContainerComponent
    ]
})
export class AppContainerModule {
}