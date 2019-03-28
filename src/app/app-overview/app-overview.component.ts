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
            appName: "Asset Dashboards",
            appDescription: "Monitor measurements of your assets by placing visualizations on an image of your asset.",
            appId: "asset-monitoring",
        },
        {
            appName: "Transport Monitoring",
            appDescription: "Monitors the current transport status",
            appId: "transport-monitoring"
        },
        {
            appName: "Pallet 3D Model",
            appDescription: "View a virtual 3D model of a pallet.",
            appId: "pallet-3d-model"
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
        this.currentlySelectedApp = appId;
    }


}