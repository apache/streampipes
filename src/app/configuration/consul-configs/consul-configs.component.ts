import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import {xsService} from '../../NS/XS.service'
@Component({
    selector: 'consul-configs',
    templateUrl: './consul-configs.component.html',
    styleUrls: ['./consul-configs.component.css'],
    providers: [ xsService ]
})
export class ConsulConfigsComponent {
    @Input() consulService: StreampipesPeContainer;
    @Output() updateConsulService: EventEmitter<StreampipesPeContainer> = new EventEmitter<StreampipesPeContainer>();
    constructor(private service: xsService) {    
    }

    updateConfiguration(): void {
        this.updateConsulService.emit(this.consulService);
    }

}