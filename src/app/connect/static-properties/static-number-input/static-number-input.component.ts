import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { MappingPropertyUnary } from '../../model/MappingPropertyUnary';
import { DataSetDescription } from '../../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Logger } from '../../../shared/logger/default-log.service';
import { ifError } from 'assert';
import { ValidateNumber } from '../../select-protocol-component/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';

@Component({
    selector: 'app-static-number-input',
    templateUrl: './static-number-input.component.html',
    styleUrls: ['./static-number-input.component.css']
})
export class StaticNumberInputComponent implements OnInit {
    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<any> = new EventEmitter<any>();


    private freeTextForm: FormGroup;
    private inputValue: String;
    private errorMessage = "Please enter a valid Number";
    private hasInput: Boolean;

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticTextNumber': new FormControl(this.inputValue, [
                Validators.required,
                ValidateNumber
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;

        if (inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        //NUMBER VALIDATOR
        else if (!isNaN(inputValue)) {
            this.hasInput = true;
        }
        else {
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);

    }

}