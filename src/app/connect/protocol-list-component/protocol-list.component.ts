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