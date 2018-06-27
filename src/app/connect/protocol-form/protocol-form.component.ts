import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {isUndefined} from 'util';
import {ProtocolDescription} from '../model/ProtocolDescription';
import {ProtocolDescriptionList} from '../model/ProtocolDescriptionList';
import {ValidateUrl} from './input.validator';
import { FormControl, FormGroup } from '@angular/forms';


@Component({
  selector: 'app-protocol-form',
  templateUrl: './protocol-form.component.html',
  styleUrls: ['./protocol-form.component.css']
})
export class ProtocolFormComponent implements OnInit {

  @Output() selectedProtocolChange = new EventEmitter<ProtocolDescription>();
  @Output() inputValueChanged = new EventEmitter<any>();

  @Input() selectedProtocol: ProtocolDescription;
  @Input() allProtocols: ProtocolDescription[];
  index: Number;
  
  hasInput: Boolean [];
  // onSubmit() {
  //   console.log(this.allProtocols);
  // }

  constructor() { 
    
   }

  isSelected(p: ProtocolDescription): boolean {
    if (isUndefined(this.selectedProtocol)) {
      return false;
    } else {
      this.selectedProtocolChange.emit(this.selectedProtocol);
      return p.label === this.selectedProtocol.label;
    }
    
  }

  textValidation(hasInput) {
    this.hasInput = hasInput;
    this.inputValueChanged.emit(hasInput);
  }

  ngOnInit() {
    
  }

}
