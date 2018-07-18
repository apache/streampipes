import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
import {ConfigurationService} from '../shared/configuration.service'
@Component({
    selector: 'consul-configs-boolean',
    templateUrl: './consul-configs-boolean.component.html',
    styleUrls: ['./consul-configs-boolean.component.css'],
    providers: [ConfigurationService]
})
export class ConsulConfigsBooleanComponent {
    @Input() configuration: StreampipesPeContainerConifgs
    constructor(private configService:ConfigurationService) {    
    }

}