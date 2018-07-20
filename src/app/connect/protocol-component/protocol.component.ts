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
  @Output() selectedProtocolEmitter = new EventEmitter();
  private hasConfig: Boolean;

  constructor() {
    this.hasConfig=true;
  }

  protocolEditable() {
    this.protocol.edit = !this.protocol.edit;
    this.editableEmitter.emit(this.protocol);
    if (this.protocol.config.length == 0) {
      console.log(this.protocol.config);
      this.hasConfig = false;
      this.validateEmitter.emit(true);
      this.selectedProtocol = this.protocol;
      this.selectedProtocolEmitter.emit(this.selectedProtocol);
    }
  }
  validateText(textValid) {
    if(textValid && this.protocol.edit) {
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
    console.log("test1234")
    console.log(this.protocol);
    
  }





}