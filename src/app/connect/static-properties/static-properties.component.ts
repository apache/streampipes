import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import { StaticProperty } from '../model/StaticProperty';
import { MappingPropertyUnary } from '../model/MappingPropertyUnary';
import { DataSetDescription } from '../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import {Logger} from '../../shared/logger/default-log.service';
import { ifError } from 'assert';
import { ValidateUrl, ValidateNumber, ValidateString } from '../protocol-form/input.validator';
import {xsService} from '../../NS/XS.service';
import {StaticPropertyUtilService} from './static-property-util.service';

@Component({
    selector: 'app-static-properties',
    templateUrl: './static-properties.component.html',
    styleUrls: ['./static-properties.component.css'],
    providers: [xsService]
})
export class StaticPropertiesComponent implements OnInit {

    @Input() staticProperties: StaticProperty[];
    @Input() dataSet: DataSetDescription;
    //@Input() firstCtrl: FormControl;
    @Output() emitter: EventEmitter<any> = new EventEmitter<any>();
    @Output() emitter1:EventEmitter<any> = new EventEmitter<any>();

    private mappingFormControl: FormControl = new FormControl();
    private freeTextFormControl: FormControl = new FormControl([Validators.required]);
    private doNotRender: boolean;
    private frTxt: FreeTextStaticProperty;

    constructor(private logger: Logger, private xsService: xsService, private staticPropertyUtil: StaticPropertyUtilService) {
        logger.log(this.staticProperties);
    }

    ngOnInit() {
        this.mappingFormControl.valueChanges
            .subscribe(res => {
                this.emitter.emit(res);
            });

        this.freeTextFormControl.valueChanges
            .subscribe(res => {
                this.emitter.emit();
            });
        for(let property of this.staticProperties) {

            this.frTxt = <FreeTextStaticProperty> property;
            this.frTxt.requiredDomainProperty = "";
            
        }
        
    }

    getName(eventProperty) {
        return eventProperty.label ? eventProperty.label : eventProperty.runTimeName;
    }

    isFreeTextStaticProperty(val) {
        return val instanceof FreeTextStaticProperty;
    }

    isMappingPropertyUnary(val) {
        return val instanceof MappingPropertyUnary;
    }

    valueChange(hasInput) {
        this.emitter1.emit(hasInput);
    }
}
