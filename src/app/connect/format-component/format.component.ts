import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';

@Component({
  selector: 'app-format',
  templateUrl: './format.component.html',
  styleUrls: ['./format.component.css']
})

export class FormatComponent {

  @Input() format: FormatDescription;
  @Input() selectedFormat: FormatDescription;
  @Output() validateEmitter = new EventEmitter();
  @Output() editableEmitter = new EventEmitter();
  @Output() selectedFormatEmitter = new EventEmitter();
  private hasConfig: Boolean;

  constructor() {
    this.hasConfig=true;
  }

  formatEditable() {
    /*
    this.format.edit = !this.format.edit;
    this.editableEmitter.emit(this.format);
    if (this.format.config.length == 0) {
      console.log(this.format.config);
      this.hasConfig = false;
      this.validateEmitter.emit(true);
      this.selectedFormat = this.format;
      this.selectedFormatEmitter.emit(this.selectedFormat);
    }
    */
   this.selectedFormat = this.format;
   this.selectedFormatEmitter.emit(this.selectedFormat);
  }
  validateText(textValid) {
    if (textValid && this.format.edit) {
      this.validateEmitter.emit(true);
      this.selectedFormat = this.format;
      this.selectedFormatEmitter.emit(this.selectedFormat);
    }
    else {
      this.validateEmitter.emit(false);
      this.selectedFormat = null;
    }
  }
  ngOnInit() {

  }





}