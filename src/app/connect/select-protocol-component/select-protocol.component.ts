import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { isUndefined } from 'util';
import { ProtocolDescription } from '../model/ProtocolDescription';
import { ProtocolDescriptionList } from '../model/ProtocolDescriptionList';
import { ValidateUrl } from './input.validator';
import { FormControl, FormGroup } from '@angular/forms';


@Component({
  selector: 'app-select-protocol',
  templateUrl: './select-protocol.component.html',
  styleUrls: ['./select-protocol.component.css']
})
export class SelectProtocolComponent implements OnInit {

  @Output() selectedProtocolChange = new EventEmitter<ProtocolDescription>();
  @Output() inputValueChanged = new EventEmitter<any>();
  @Output() selectedProtocolEmitter = new EventEmitter();

  @Input() selectedProtocol: ProtocolDescription;
  @Input() allProtocols: ProtocolDescription[];
  index: Number;
  hasInput: Boolean[] = [false];
  showStaticProperty: Boolean[] = [false]

  // onSubmit() {
  //   console.log(this.allProtocols);
  // }

  constructor() {
    
    
    // for (var i = 0; i < 3 - 1; i++) {
    //   this.showStaticProperty.push(false);
    //   this.hasInput.push(false);
    // }
  }

  isSelected(p: ProtocolDescription): boolean {
    if (isUndefined(this.selectedProtocol)) {
      return false;
    } else {
      this.selectedProtocolChange.emit(this.selectedProtocol);
      return p.label === this.selectedProtocol.label;
    }

  }

  protocolSelected(selectedProtocol) {
    this.selectedProtocol = selectedProtocol;
    this.selectedProtocolEmitter.emit(selectedProtocol);
  }

  // textValidation1(hasInput, index) {
  //   this.hasInput[index] = hasInput;

  //   if (this.hasInput[index] && this.showStaticProperty[index]) {
  //     this.inputValueChanged.emit(true);
  //   } else {
  //     this.inputValueChanged.emit(false);
  //   }
  // }

  validateAll(allValid) {
    this.inputValueChanged.emit(allValid);
  }

  // clickedOption(i) {
  //   if (!this.showStaticProperty[i]) {
  //     var listItem = document.getElementById("protocolList").getElementsByClassName("listItem");
  //     for (var j = 0; j < this.showStaticProperty.length; j++) {
  //       if (j == i) {
  //         this.showStaticProperty[j] = true;

  //         //If selected Element already has Input -> enable next button
  //         if (this.hasInput[i] && this.showStaticProperty[i]) {
  //           this.inputValueChanged.emit(true);
  //         } else {
  //           this.inputValueChanged.emit(false);
  //         }

  //         listItem[i].classList.add("selectedItem");
  //         switch (this.allProtocols[j].config.length) {
  //           case 2: {
  //             listItem[i].classList.add("twoStaticProperties");
  //             break;
  //           }
  //           case 3: {
  //             listItem[i].classList.add("threeStaticProperties");
  //             break;
  //           }
  //           default: {
  //             break;
  //           }
  //         }
  //       }
  //       else {
  //         this.showStaticProperty[j] = false;
  //         listItem[j].classList.remove("selectedItem");
  //         listItem[j].classList.remove("twoStaticProperties");
  //         listItem[i].classList.remove("threeStaticProperties");
  //       }
  //     }
  //   }
  // }

  ngOnInit() {

    // setTimeout(() => {
    //   for (var i = 0; i < this.allProtocols.length - 1; i++) {
    //     this.showStaticProperty.push(false);
    //     this.hasInput.push(false);
    //   }
    // }, 30);

    // console.log("test1");
    
    // console.log(this.allProtocols);
    
    // for (var i = 0; i<this.allProtocols.length-1; i++){
    //    this.showStaticProperty[i] = false;
    //   }
    //   console.log("ngOnit durchlaufen")
    // Problem showStaticProperty wird zu langsam deklariert, error im HTML, das showStaticProperty undefined an index 0 ist
  }

}
