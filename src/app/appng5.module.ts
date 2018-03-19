import './app.module';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { MatGridListModule } from '@angular/material/grid-list';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';

import { HomeModule } from './home/home.module';
import { ConfigurationModule } from './configuration/configuration.module';
import { AuthStatusService } from './services/auth-status.service';
import { AppContainerModule } from './app-container/app-container.module';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        UpgradeModule,
        MatGridListModule,
        MatIconModule,
        FlexLayoutModule,
        HttpClientModule,
        FormsModule,
        UIRouterUpgradeModule.forChild(),
        HomeModule,
        ConfigurationModule,
        AppContainerModule
    ],
    providers: [
        AuthStatusService
    ]
})
export class AppModule {

    constructor(private upgrade: UpgradeModule) {
    }

    ngDoBootstrap() {
        this.upgrade.bootstrap(document.body, ['streamPipesApp']);
    }

}