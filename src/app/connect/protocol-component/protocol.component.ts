import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ProtocolDescription } from '../model/connect/grounding/ProtocolDescription';
import {ConfigurationInfo} from "../model/message/ConfigurationInfo";

@Component({
  selector: 'app-protocol',
  templateUrl: './protocol.component.html',
  styleUrls: ['./protocol.component.css']
})

export class ProtocolComponent {

  @Input() protocol: ProtocolDescription;
  @Input() selectedProtocol: ProtocolDescription;
  @Output() validateEmitter = new EventEmitter();
  @Output() editableEmitter = new EventEmitter();
  @Output() selectedProtocolEmitter = new EventEmitter();

  private hasConfig: Boolean;

  constructor() {
    this.hasConfig=true;
  }

  validateText(textValid) {

    // if(textValid && this.protocol.edit) {
        if(textValid) {
      this.validateEmitter.emit(true);
      this.selectedProtocol = this.protocol;
      this.selectedProtocolEmitter.emit(this.selectedProtocol);
    }
    else {
      this.validateEmitter.emit(false);
      this.selectedProtocol = null;
    }
  }
  ngOnInit() {

  }
}