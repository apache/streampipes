import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';
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
    @Output() selectedFormatEmitter = new EventEmitter();
    @Input() allFormats: FormatDescription[];

    showStaticProperty: Boolean[] = [false]
    hasInput: Boolean[] = [false];

    constructor() {
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
        var selectedFormat = this.selectedFormat;

        setTimeout(() => {
            for (var i = 0; i < this.allFormats.length ; i++) {
                if (selectedFormat && this.allFormats[i].label == selectedFormat.label) {
                    this.showStaticProperty.push(true);
                    this.hasInput.push(true);
                } else {
                    this.showStaticProperty.push(false);
                    this.hasInput.push(false);
                }

            }
        }, 30);

    }

    validateAll(allValid) {
        this.inputValueChanged.emit(allValid);
    }

    formatSelected(selectedFormat) {
        this.selectedFormat = selectedFormat;
        this.selectedFormatEmitter.emit(this.selectedFormat)

    }

}
