import {Component} from '@angular/core';

@Component({
    templateUrl: './app-overview.component.html',
    styleUrls: ['./app-overview.component.css']
})
export class AppOverviewComponent {

    selectedIndex: number = 0;
    appOpen = false;
    currentlySelectedApp: string = "";

    apps: any[] = [
        {
            appName: "Asset Monitoring",
            appDescription: "Monitor measurement of your assets by using an image of your asset.",
            appId: "asset-monitoring",
        },
        {
            appName: "Transport Monitoring",
            appDescription: "Monitors the current transport status",
            appId: "transport-monitoring"
        },
    ];

    constructor() {

    }

    ngOnInit() {

    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

    appOpened(appOpen: boolean) {
        this.appOpen = appOpen;
    }

    appClosed() {
        this.appOpen = false;
        this.currentlySelectedApp = "";
    }

    selectApp(appId: string) {
        console.log(appId);
        this.currentlySelectedApp = appId;
    }


}