import './app.module';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { MatGridListModule } from '@angular/material/grid-list';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UIRouterUpgradeModule } from '@uirouter/angular-hybrid';
import { HomeModule } from './home/home.module';

@NgModule({
    imports: [
        BrowserModule,
        UpgradeModule,
        MatGridListModule,
        FlexLayoutModule,
        UIRouterUpgradeModule.forChild(),
        HomeModule
    ]
})
export class AppModule {

    constructor(private upgrade: UpgradeModule) {
    }

    ngDoBootstrap() {
        this.upgrade.bootstrap(document.body, ['streamPipesApp']);
    }

}