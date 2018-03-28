import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {isUndefined} from 'util';
import {ProtocolDescription} from '../model/ProtocolDescription';
import {ProtocolDescriptionList} from '../model/ProtocolDescriptionList';

@Component({
  selector: 'app-protocol-form',
  templateUrl: './protocol-form.component.html',
  styleUrls: ['./protocol-form.component.css']
})
export class ProtocolFormComponent implements OnInit {

  @Input() selectedProtocol: ProtocolDescription;

  @Output() selectedProtocolChange = new EventEmitter<ProtocolDescription>();

  @Input() allProtocols: ProtocolDescription[];


  // onSubmit() {
  //   console.log(this.allProtocols);
  // }

  constructor() { }

  isSelected(p: ProtocolDescription): boolean {
    if (isUndefined(this.selectedProtocol)) {
      return false;
    } else {
      this.selectedProtocolChange.emit(this.selectedProtocol);
      return p.label === this.selectedProtocol.label;
    }
  }


  ngOnInit() {
  }

}
