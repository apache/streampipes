import { NgModule, COMPILER_OPTIONS, CompilerFactory, Compiler } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';

import { AppContainerComponent } from './app-container.component';
import { AppContainerService } from './shared/app-container.service';
import { ViewComponent } from './view/view.component';

import { JitCompilerFactory } from '@angular/platform-browser-dynamic';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule
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