import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { MappingPropertyUnary } from '../../model/MappingPropertyUnary';
import { DataSetDescription } from '../../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {Logger} from '../../../shared/logger/default-log.service';
import { ifError } from 'assert';
import { ValidateUrl} from '../../protocol-form/input.validator';
import {StaticPropertyUtilService} from '../static-property-util.service';

@Component({
    selector: 'app-static-url-input',
    templateUrl: './static-url-input.component.html',
    styleUrls: ['./static-url-input.component.css']
})
export class StaticUrlInputComponent implements OnInit {
    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }

    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    
    private freeTextForm: FormGroup;
    private inputValue: String;
    private hasInput: Boolean;
    private errorMessage = "Please enter a valid Url";
    ngOnInit() {
        this.freeTextForm = new FormGroup({
            'freeStaticTextUrl':new FormControl(this.inputValue, [
                Validators.required,
                ValidateUrl
            ]),
        })
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else if(inputValue.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g) != null) {
            this.hasInput = true;
        }
        else{
            this.hasInput = false;
        }

        this.inputEmitter.emit(this.hasInput);
    }

}