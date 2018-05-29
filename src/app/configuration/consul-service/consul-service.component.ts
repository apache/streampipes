import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConsulService } from '../shared/consul-service.model';
import {ConsulServiceConfigs} from '../shared/consul-service-configs'
@Component({
    selector: 'consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css']
})
export class ConsulServiceComponent {

    @Input() consulService: ConsulService;
    @Output() updateConsulService: EventEmitter<ConsulService> = new EventEmitter<ConsulService>();
    showConfiguration: boolean = false;

    constructor() {
        
    }

    toggleConfiguration(): void {
        this.showConfiguration = !this.showConfiguration;
        this.adjustConfigurationKey();
    }

    updateConfiguration(): void {
        this.updateConsulService.emit(this.consulService);
    }

    adjustConfigurationKey(): void {
        // nur auf der ui ändern configuration.key unverändert lassen
         this.consulService.configs.forEach((configuration) => {
            var str = configuration.key;
            str = str.replace(/SP/g,"");
            configuration.key = str.replace(/_/g," ");  
        } )
        this.updateConfiguration();
    }

}
