import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
@Component({
    selector: 'consul-service',
    templateUrl: './consul-service.component.html',
    styleUrls: ['./consul-service.component.css']
})
export class ConsulServiceComponent {

    @Input() consulService: StreampipesPeContainer;
    @Output() updateConsulService: EventEmitter<StreampipesPeContainer> = new EventEmitter<StreampipesPeContainer>();
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

    adjustConfigurationKey() {
        // nur auf der ui ändern configuration.key unverändert lassen
         this.consulService.configs.forEach((configuration) => {
            var str1 = configuration.key;
            str1 = str1.replace(/SP/g,"");
            configuration.modifiedKey = str1.replace(/_/g," ");  
            
            
        } )
        this.updateConfiguration();
    }

}
