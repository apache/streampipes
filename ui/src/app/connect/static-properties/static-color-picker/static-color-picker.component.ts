import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";
import {StaticProperty} from "../../model/StaticProperty";
import {StaticPropertyUtilService} from "../static-property-util.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-static-color-picker',
    templateUrl: './static-color-picker.component.html',
    styleUrls: ['./static-color-picker.component.css']
})
export class StaticColorPickerComponent implements OnInit {

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();

    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<any> = new EventEmitter<any>();

    private inputValue: String;
    private hasInput: Boolean;
    private colorPickerForm: FormGroup;

    ngOnInit() {
        this.colorPickerForm = new FormGroup({
            'colorPickerStaticProperty': new FormControl(this.inputValue, [
                Validators.required
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if (inputValue == "" || !inputValue) {
            this.hasInput = false;
        } else {
            this.hasInput = true;
        }

        this.inputEmitter.emit(this.hasInput);

    }

    emitUpdate() {
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, this.staticPropertyUtil.asColorPickerStaticProperty(this.staticProperty).selectedColor && this.staticPropertyUtil.asColorPickerStaticProperty(this.staticProperty).selectedColor !== ""));
    }

}