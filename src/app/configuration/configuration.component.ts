import { Component } from '@angular/core';

import { ConfigurationService } from './shared/configuration.service';
import { StreampipesPeContainer } from "./shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "./shared/streampipes-pe-container-configs";
@Component({
    templateUrl: './configuration.component.html',
    styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent {

    consulServices: StreampipesPeContainer[];

    constructor(private configurationService: ConfigurationService) {
        this.getConsulServices();
    }

    getConsulServices(): void {
        this.configurationService.getConsulServices()
            .subscribe( response => {
                this.consulServices = response;
            }, error => {
                console.error(error);
            });

            
    }

    updateConsulService(consulService: StreampipesPeContainer): void {
        this.configurationService.updateConsulService(consulService)
            .subscribe(response => {

            }, error => {
                console.error(error);
            });
    }
    
}