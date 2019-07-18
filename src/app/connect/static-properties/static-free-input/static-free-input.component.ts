import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { StaticProperty } from '../../model/StaticProperty';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";


@Component({
    selector: 'app-static-free-input',
    templateUrl: './static-free-input.component.html',
    styleUrls: ['./static-free-input.component.css']
})
export class StaticFreeInputComponent implements OnInit {


    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    @Output() updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();
    
    private freeTextForm: FormGroup;
    private inputValue: String;
    private hasInput: Boolean;
    private errorMessage = "Please enter a value";

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }


    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticText':new FormControl(this.inputValue, [
                Validators.required,
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else{
            this.hasInput = true;
        }

        this.inputEmitter.emit(this.hasInput);
    }

    emitUpdate() {
        let valid = (this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value != undefined && this.staticPropertyUtil.asFreeTextStaticProperty(this.staticProperty).value !== "");
        this.updateEmitter.emit(new ConfigurationInfo(this.staticProperty.internalName, valid));
    }
}