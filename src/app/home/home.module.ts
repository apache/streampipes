import { NgModule } from '@angular/core';
import { HomeComponent } from './home.component';
import { HomeService } from './home.service';
import { MatGridListModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule
    ],
    declarations: [
        HomeComponent
    ],
    providers: [
        HomeService
    ],
    entryComponents: [
        HomeComponent
    ]
})
export class HomeModule {}