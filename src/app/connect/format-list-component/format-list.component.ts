import { Component, Input, Output, EventEmitter } from '@angular/core';
import {ProtocolDescription} from '../model/ProtocolDescription';
import {FormatDescription} from '../model/FormatDescription';
@Component({
    selector: 'app-format-list',
    templateUrl: './format-list.component.html',
    styleUrls: ['./format-list.component.css']
  })

export class FormatListComponent {
    @Input() selectedFormat: FormatDescription;
    @Input() allFormats: FormatDescription[];    
    @Output() validateEmitter = new EventEmitter();
    @Output() selectedFormatEmitter = new EventEmitter();

    constructor() {
      
    }
    formatEditable(selectedFormat) {

      this.allFormats.forEach(format => {
        if(format!=selectedFormat){
          format.edit = false;
        }
      });

    }

    formatSelected(selectedFormat) {
      this.selectedFormat = selectedFormat;
      this.selectedFormatEmitter.emit(this.selectedFormat)

    }

    validateAll(allValid) {
      this.validateEmitter.emit(allValid);
    }
    ngOnInit(){
      console.log("test4");
      
      console.log(this.allFormats)
    }

  }