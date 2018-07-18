import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
import {ConfigurationService} from '../shared/configuration.service'
@Component({
    selector: 'consul-configs-text',
    templateUrl: './consul-configs-text.component.html',
    styleUrls: ['./consul-configs-text.component.css'],
    providers: [ConfigurationService]
})
export class ConsulConfigsTextComponent {
    @Input() configuration: StreampipesPeContainerConifgs
    constructor(private configService: ConfigurationService) {    
    }
    
}