import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';
import {ShepherdService} from '../../services/tour/shepherd.service';

@Component({
  selector: 'app-format',
  templateUrl: './format.component.html',
  styleUrls: ['./format.component.css'],
})
export class FormatComponent {
  @Input()
  format: FormatDescription;
  @Input()
  selectedFormat: FormatDescription;
  @Output()
  validateEmitter = new EventEmitter();
  @Output()
  editableEmitter = new EventEmitter();
  @Output()
  selectedFormatEmitter = new EventEmitter();
  private hasConfig: Boolean;



  constructor(private ShepherdService: ShepherdService) {
    this.hasConfig = true;
  }

  formatEditable() {
    this.selectedFormat = this.format;
    this.selectedFormatEmitter.emit(this.selectedFormat);

    this.ShepherdService.trigger("select-" + this.selectedFormat.label.toLocaleLowerCase());

  }

  validateText(textValid) {
    if (textValid && this.format.edit) {
      this.validateEmitter.emit(true);
      this.selectedFormat = this.format;
      this.selectedFormatEmitter.emit(this.selectedFormat);
    } else {
      this.validateEmitter.emit(false);
      this.selectedFormat = null;
    }
  }
  isSelected(): boolean {
    return this.selectedFormat === this.format;
  }
  ngOnInit() {}
}
