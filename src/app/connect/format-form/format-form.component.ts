import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormatDescription } from '../model/FormatDescription';
import { isUndefined } from 'util';

@Component({
  selector: 'app-format-form',
  templateUrl: './format-form.component.html',
  styleUrls: ['./format-form.component.css']
})
export class FormatFormComponent implements OnInit {

  @Input() selectedFormat: FormatDescription;

  @Output() selectedFormatChange = new EventEmitter<FormatDescription>();
  @Output() inputValueChanged = new EventEmitter<Boolean>();

  @Input() allFormats: FormatDescription[];

  showStaticProperty: Boolean[] = [false]
  hasInput: Boolean[] = [false];

  constructor() {
    // for (var i = 0; i < 2 - 1; i++) {
    //   this.showStaticProperty.push(false);
    //   this.hasInput.push(false);
    // }
  }

  isSelected(f: FormatDescription): boolean {
    if (isUndefined(this.selectedFormat)) {
      return false;
    } else {
      this.selectedFormatChange.emit(this.selectedFormat);
      return f.label === this.selectedFormat.label;
    }
  }


  ngOnInit() {
    setTimeout(() => {
      for (var i = 0; i < this.allFormats.length - 1; i++) {
        this.showStaticProperty.push(false);
        this.hasInput.push(false);
      }
    }, 30);
    console.log("formatForm");
    
    console.log(this.allFormats);    
  }

  validateAll(allValid) {
    this.inputValueChanged.emit(allValid);
  }

  // textValidation(hasInput, index) {
  //   this.hasInput[index] = hasInput;

  //   if (this.hasInput[index] && this.showStaticProperty[index]) {
  //     this.inputValueChanged.emit(true);
  //   } else {
  //     this.inputValueChanged.emit(false);
  //   }
  // }

  // clickedOption(i) {
  //   if (!this.showStaticProperty[i]) {
  //     var listItem = document.getElementById("formList").getElementsByClassName("listItem");

  //     for (var j = 0; j < this.showStaticProperty.length; j++) {
  //       if (j == i) {
  //         this.showStaticProperty[j] = true;
  //         if (this.hasInput[i] && this.showStaticProperty[i]) {
  //           this.inputValueChanged.emit(true);
  //         } else {
  //           this.inputValueChanged.emit(false);
  //         }
  //         listItem[i].classList.add("selectedItem");
  //         switch (this.allFormats[j].config.length) {
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



}
