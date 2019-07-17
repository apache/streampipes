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

    constructor(private appContainerService: AppContainerService) {
        this.appContainerService.getInstalledApps().subscribe(installedApps => {
            this.installedApps = installedApps;
        });
    }

    activateApp(installedApp: InstalledApp): void {
        this.activeApp = installedApp;
        this.isAppActive = true;
    }

}