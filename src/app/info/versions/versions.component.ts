import {Component, Input} from '@angular/core';
import {VersionInfoService} from "./service/version-info.service";
import {VersionInfo} from "./service/version-info.model";
import {SystemInfo} from "./service/system-info.model";

@Component({
    selector: 'sp-versions',
    templateUrl: './versions.component.html',
    styleUrls: ['./versions.component.css']
})
export class VersionsComponent  {

    versionInfo: VersionInfo;
    systemInfo: SystemInfo;

    constructor(private versionInfoService: VersionInfoService) {
        this.getVersionInfo();
        this.getSystemInfo();
    }

    getVersionInfo(): void {
        this.versionInfoService.getVersionInfo()
            .subscribe(response => {
                this.versionInfo = response;
            }, error => {
                console.error(error);
            });
    }

    getSystemInfo(): void {
        this.versionInfoService.getSysteminfo()
            .subscribe(response => {
                this.systemInfo = response;
            }, error => {
                console.error(error);
            });
    }

}
