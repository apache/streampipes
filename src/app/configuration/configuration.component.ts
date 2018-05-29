import { Component } from '@angular/core';

import { ConfigurationService } from './shared/configuration.service';
import { ConsulService } from './shared/consul-service.model';
import {ConsulServiceConfigs} from './shared/consul-service-configs';
@Component({
    templateUrl: './configuration.component.html',
    styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent {

    consulServices: ConsulService[];

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

    updateConsulService(consulService: ConsulService): void {
        console.log(consulService.configs[3].value);
        
        this.configurationService.updateConsulService(consulService)
            .subscribe(response => {

            }, error => {
                console.error(error);
            });
    }
    
}