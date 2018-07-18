import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';


@Component({
    selector: 'app-static-free-input',
    templateUrl: './static-free-input.component.html',
    styleUrls: ['./static-free-input.component.css']
})
export class StaticFreeInputComponent implements OnInit {


    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    
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

}