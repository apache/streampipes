import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {StaticPropertyUtilService} from '../static-property-util.service';
import {EventSchema} from '../../schema-editor/model/EventSchema';


@Component({
    selector: 'app-static-mapping-unary',
    templateUrl: './static-mapping-unary.component.html',
    styleUrls: ['./static-mapping-unary.component.css']
})
export class StaticMappingUnaryComponent implements OnInit {


    @Input() staticProperty: StaticProperty;
    @Input() eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();
    
    private unaryTextForm: FormGroup;
    private inputValue: String;
    private hasInput: Boolean;
    private errorMessage = "Please enter a value";

    constructor(private staticPropertyUtil: StaticPropertyUtilService){

    }


    ngOnInit() {
        this.unaryTextForm = new FormGroup({
            'unaryStaticText':new FormControl(this.inputValue, [
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

    getName(eventProperty) {
    return eventProperty.label
      ? eventProperty.label
      : eventProperty.runTimeName;
  }

}