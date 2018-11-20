import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import { StaticProperty } from '../model/StaticProperty';
import { MappingPropertyUnary } from '../model/MappingPropertyUnary';
import { OneOfStaticProperty } from '../model/OneOfStaticProperty';
import { DataSetDescription } from '../model/DataSetDescription';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { Logger } from '../../shared/logger/default-log.service';
import { ifError } from 'assert';
import {
  ValidateUrl,
  ValidateNumber,
  ValidateString,
} from '../select-protocol-component/input.validator';
import { xsService } from '../../NS/XS.service';
import { StaticPropertyUtilService } from './static-property-util.service';
import { AnyStaticProperty } from '../model/AnyStaticProperty';

@Component({
  selector: 'app-static-property',
  templateUrl: './static-property.component.html',
  styleUrls: ['./static-property.component.css'],
  providers: [xsService],
})
export class StaticPropertyComponent implements OnInit {
  @Input()
  staticProperty: StaticProperty;
  @Output()
  emitter: EventEmitter<any> = new EventEmitter<any>();
  @Output()
  validateEmitter: EventEmitter<any> = new EventEmitter<any>();
  @Input()
  dataSet: DataSetDescription;
  // private mappingFormControl: FormControl = new FormControl();
  // private freeTextFormControl: FormControl = new FormControl([Validators.required]);
  // private doNotRender: boolean;
  private frTxt: FreeTextStaticProperty;

  constructor(
    private logger: Logger,
    private xsService: xsService,
    private staticPropertyUtil: StaticPropertyUtilService
  ) {
    logger.log(this.staticProperty);
  }

  ngOnInit() {
    // this.mappingFormControl.valueChanges
    //     .subscribe(res => {
    //         this.emitter.emit(res);
    //     });

    // this.freeTextFormControl.valueChanges
    //     .subscribe(res => {
    //         this.emitter.emit();
    //     });
    // for(let property of this.staticProperties) {

    //     this.frTxt = <FreeTextStaticProperty> property;
    //     this.frTxt.requiredDomainProperty = "";
    // }
    this.frTxt = <FreeTextStaticProperty>this.staticProperty;
    this.frTxt.requiredDomainProperty = '';
  }

  getName(eventProperty) {
    return eventProperty.label
      ? eventProperty.label
      : eventProperty.runTimeName;
  }

  isFreeTextStaticProperty(val) {
    return val instanceof FreeTextStaticProperty;
  }

  isAnyStaticProperty(val) {
    return val instanceof AnyStaticProperty;
  }

  isMappingPropertyUnary(val) {
    return val instanceof MappingPropertyUnary;
  }

  isOneOfStaticProperty(val) {
    return val instanceof OneOfStaticProperty;
  }

  valueChange(hasInput) {
    this.staticProperty.isValid = hasInput;
    this.validateEmitter.emit();
  }
}
