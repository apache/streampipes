import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import { StaticProperty } from '../model/StaticProperty';
import { MappingPropertyUnary } from '../model/MappingPropertyUnary';
import { DataSetDescription } from '../model/DataSetDescription';
import { FormControl } from '@angular/forms';

@Component({
    selector: 'app-static-properties',
    templateUrl: './static-properties.component.html',
    styleUrls: ['./static-properties.component.css']
})
export class StaticPropertiesComponent implements OnInit {

    @Input() staticProperties: StaticProperty[];
    @Input() dataSet: DataSetDescription;

    @Output() emitter: EventEmitter<any> = new EventEmitter<any>();

    mappingFormControl: FormControl = new FormControl();
    freeTextFormControl: FormControl = new FormControl();

    constructor() {
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

    asFreeTextStaticProperty(val: StaticProperty): FreeTextStaticProperty {
        return <FreeTextStaticProperty> val;
    }

}
