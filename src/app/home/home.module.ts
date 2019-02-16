import { NgModule } from '@angular/core';
import { HomeComponent } from './home.component';
import { HomeService } from './home.service';
import { MatGridListModule, MatIconModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { StatusComponent } from "./components/status.component";
import { RestApi } from "../services/rest-api.service";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatIconModule
    ],
    declarations: [
        HomeComponent,
        StatusComponent
    ],
    providers: [
        HomeService,
        RestApi,
        {
            provide: '$http',
            useFactory: ($injector: any) => $injector.get('$http'),
            deps: ['$injector'],
        },
        {
            provide: 'apiConstants',
            useFactory: ($injector: any) => $injector.get('apiConstants'),
            deps: ['$injector'],
        },
        {
            provide: 'AuthStatusService',
            useFactory: ($injector: any) => $injector.get('AuthStatusService'),
            deps: ['$injector'],
        },
    ],
    entryComponents: [
        HomeComponent
    ]
})
export class HomeModule {
}