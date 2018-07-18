import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ProtocolDescription } from '../model/ProtocolDescription';

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
  protocolEditable() {
    this.protocol.edit = !this.protocol.edit;
    this.editableEmitter.emit(this.protocol);
  }
  validateText(textValid) {
    if(textValid && this.protocol.edit) {
      this.validateEmitter.emit(true);
      this.selectedProtocol = this.protocol;
    }
    else {
      this.validateEmitter.emit(false);
      this.selectedProtocol = null;
    }
  }
  ngOnInit() {

  }





}