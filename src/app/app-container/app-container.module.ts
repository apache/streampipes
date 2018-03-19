import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';

import { AppContainerComponent } from './app-container.component';
import { AppContainerService } from './shared/app-container.service';
import { ViewComponent } from './view/view.component';

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
        AppContainerService
    ],
    entryComponents: [
        AppContainerComponent
    ]
})
export class AppContainerModule {
}