import { Component, Input } from '@angular/core';
import { StreampipesPeContainer } from "../shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "../shared/streampipes-pe-container-configs";
import {ConfigurationService} from '../shared/configuration.service'

const hiddenPasswordString = '*****';

@Component({
    selector: 'consul-configs-password',
    templateUrl: './consul-configs-password.component.html',
    styleUrls: ['./consul-configs-password.component.css'],
    providers: [ConfigurationService]
})
export class ConsulConfigsPasswordComponent {
    
    @Input() configuration: StreampipesPeContainerConifgs;

    password: string; 
    private show: Boolean;
    private className: String;
    private hide: Boolean;
    
    constructor(private configService: ConfigurationService) { 
        this.password = hiddenPasswordString; 
        this.show = false;
        this.className  = "hideText";
        this.hide = true;
    }

    changePw() {
        if (this.password == hiddenPasswordString) {
            this.password = this.password.replace(hiddenPasswordString, "")
        }
        this.configuration.value = this.password;
        this.show = true
    }

    addCssClass() {
        this.hide = !this.hide;

        if (!this.hide){
            this.className = "";
        }
        else {
            this.className = "hideText";
        }
    }

}