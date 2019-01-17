import { Component } from '@angular/core';

import { AppContainerService } from './shared/app-container.service';
import { InstalledApp } from './shared/installed-app.model';


@Component({
    templateUrl: './app-container.component.html',
    styleUrls: ['./app-container.component.css']
})
export class AppContainerComponent {

    installedApps: InstalledApp[];
    activeApp: InstalledApp;
    isAppActive: boolean = false;
    isLoading: boolean = false;
    term: string="";

    constructor(private appContainerService: AppContainerService) {
        this.appContainerService.getInstalledApps().subscribe(installedApps => {
            this.installedApps = installedApps;
        });
    }

    activateApp(installedApp: InstalledApp): void {
        this.isAppActive = false;
        this.isLoading = true;
        setTimeout(() => {
            this.activeApp = installedApp;
            this.isLoading = false;
            this.isAppActive = true;
        }, 200);
    }

}