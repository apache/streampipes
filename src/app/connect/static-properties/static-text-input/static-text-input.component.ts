import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { MappingPropertyUnary } from '../../model/MappingPropertyUnary';
import { DataSetDescription } from '../../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Logger } from '../../../shared/logger/default-log.service';
import { ifError } from 'assert';
import { ValidateUrl, ValidateNumber, ValidateString } from '../../select-protocol-component/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';

@Component({
    selector: 'app-static-text-input',
    templateUrl: './static-text-input.component.html',
    styleUrls: ['./static-text-input.component.css']
})
export class StaticTextInputComponent implements OnInit {

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<any> = new EventEmitter<any>();

    private freeTextForm: FormGroup;
    private inputValue: String;
    private hasInput: Boolean;
    private errorMessage = "Please enter a valid Text";
    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticTextString': new FormControl(this.inputValue, [
                Validators.required,
                ValidateString
            ]),
        })
    }
    
    valueChange(inputValue) {
        this.inputValue = inputValue;

        if (inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        //STRING VALIDATOR      
        else if (inputValue.length > 5 && inputValue.includes("@")) {
            this.hasInput = true;
        }

        else {
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);

    }

}