import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormatDescription} from '../model/FormatDescription';
import {isUndefined} from 'util';

@Component({
  selector: 'app-format-form',
  templateUrl: './format-form.component.html',
  styleUrls: ['./format-form.component.css']
})
export class FormatFormComponent implements OnInit {

  @Input() selectedFormat: FormatDescription;

  @Output() selectedFormatChange = new EventEmitter<FormatDescription>();

  @Input() allFormats: FormatDescription[];


  constructor() { }

  isSelected(f: FormatDescription): boolean {
    if (isUndefined(this.selectedFormat)) {
      return false;
    } else {
      this.selectedFormatChange.emit(this.selectedFormat);
      return f.label === this.selectedFormat.label;
    }
  }


  ngOnInit() {
  }

}
