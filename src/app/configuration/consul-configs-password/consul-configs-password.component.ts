/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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