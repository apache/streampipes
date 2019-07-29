import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { StaticProperty } from '../../model/StaticProperty';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { ValidateString } from '../../select-protocol-component/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";

@Component({
    selector: 'app-static-secret-input',
    templateUrl: './static-secret-input.component.html',
    styleUrls: ['./static-secret-input.component.css']
})
export class StaticSecretInputComponent implements OnInit {

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();

    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<any> = new EventEmitter<any>();

    private inputValue: String;
    private hasInput: Boolean;
    private secretForm: FormGroup;
    private errorMessage = "Please enter a valid Text";

    ngOnInit() {
        this.secretForm = new FormGroup({
            'secretStaticProperty': new FormControl(this.inputValue, [
                Validators.required
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        this.staticPropertyUtil.asSecretStaticProperty(this.staticProperty).isEncrypted = false;

        if (inputValue == "" || !inputValue) {
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);

    }

    emitUpdate() {
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value && this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value !== ""));
    }

}