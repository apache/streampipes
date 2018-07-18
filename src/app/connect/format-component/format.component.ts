import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormatDescription } from '../model/FormatDescription';

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
  formatEditable() {
    this.format.edit = !this.format.edit;
    this.editableEmitter.emit(this.format);
  }
  validateText(textValid) {
    if (textValid && this.format.edit) {
      this.validateEmitter.emit(true);
      this.selectedFormat = this.format;
      this.selectedFormatEmitter.emit(this.selectedFormat)
    }
    else {
      this.validateEmitter.emit(false);
      this.selectedFormat = null;
    }
  }
  ngOnInit() {

  }





}