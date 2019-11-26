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

import { Component, Input, Output, EventEmitter } from '@angular/core';
import {ProtocolDescription} from '../model/connect/grounding/ProtocolDescription';
import {FormatDescription} from '../model/connect/grounding/FormatDescription';
@Component({
    selector: 'app-protocol-list',
    templateUrl: './protocol-list.component.html',
    styleUrls: ['./protocol-list.component.css']
  })

export class ProtocolListComponent {
    @Input() allProtocols: ProtocolDescription[];
    @Input() selectedProtocol: ProtocolDescription;
    @Input() allFormats: FormatDescription[];    @Output() validateEmitter = new EventEmitter();
    @Output() selectedProtocolEmitter = new EventEmitter();


    constructor() {
      
    }
    protocolEditable(selectedProtocol) {

      this.allProtocols.forEach(protocol => {
        if(protocol!=selectedProtocol){
          protocol.edit = false;
        }
      });

    }
    validateAll(allValid) {
      this.validateEmitter.emit(allValid);
    }

    protocolSelected(selectedProtocol) {
      this.selectedProtocol = selectedProtocol;
      this.selectedProtocolEmitter.emit(selectedProtocol);
    }

    ngOnInit(){
    }

  }