import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
@Component({
    selector: 'consul-configs-password',
    templateUrl: './consul-configs-password.component.html',
    styleUrls: ['./consul-configs-password.component.css']
})
export class ConsulConfigsPasswordComponent {
    @Input() configuration: StreampipesPeContainerConifgs;
    password = "*****";
    show = false;
    
    constructor() {
        
    }
    changeInput(){
       
        this.show = !this.show 
        
    }

}