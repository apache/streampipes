import { Component } from '@angular/core';
import { ConfigurationService } from './configuration.service';

@Component({
    templateUrl: './configuration.component.html',
    styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent {

    consulServices = [];

    constructor(private configurationService: ConfigurationService) {
        this.getConsulServices();
    }

    getConsulServices() {
        this.configurationService.getConsulServices()
            .subscribe(services => {
                for (let service of services as any[]) {
                    for (let config of service['configs']) {
                        if (config.valueType === 'xs:integer') {
                            config.value = parseInt(config.value);
                        } else if (config.valueType === 'xs:double') {
                            config.value = parseFloat('xs:double');
                        } else if (config.valueType === 'xs:boolean') {
                            config.value = (config.value === 'true');
                        }
                    }
                }
                this.consulServices = services as any[];
            });
    }

    updateConsulService(consulService) {
        this.configurationService.updateConsulService(consulService)
            .subscribe(response => {

            });
    }

}