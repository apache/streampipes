import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { AppComponent } from "./app.component";

@NgModule({
    imports: [
        BrowserModule,
        UpgradeModule
    ],
    bootstrap: [AppComponent],
    declarations: [AppComponent]
})
export class AppModule {

    constructor(private upgrade: UpgradeModule) {}

}