import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { FreeTextStaticProperty } from '../model/FreeTextStaticProperty';
import { StaticProperty } from '../model/StaticProperty';
import { MappingPropertyUnary } from '../model/MappingPropertyUnary';
import { OneOfStaticProperty } from '../model/OneOfStaticProperty';
import { Logger } from '../../shared/logger/default-log.service';

import { xsService } from '../../NS/XS.service';
import { StaticPropertyUtilService } from './static-property-util.service';
import { AnyStaticProperty } from '../model/AnyStaticProperty';
import {FileStaticProperty} from '../model/FileStaticProperty';
import {MappingPropertyNary} from '../model/MappingPropertyNary';
import {EventSchema} from '../schema-editor/model/EventSchema';
import {RuntimeResolvableOneOfStaticProperty} from "../model/RuntimeResolvableOneOfStaticProperty";
import {RuntimeResolvableAnyStaticProperty} from "../model/RuntimeResolvableAnyStaticProperty";
import {ConfigurationInfo} from "../model/message/ConfigurationInfo";

@Component({
  selector: 'app-static-property',
  templateUrl: './static-property.component.html',
  styleUrls: ['./static-property.component.css'],
  providers: [xsService],
})
export class StaticPropertyComponent implements OnInit {
  @Input()
  staticProperty: StaticProperty;

  @Input()
  staticProperties: StaticProperty[];

  @Input()
  adapterId: string;

  @Output()
  validateEmitter: EventEmitter<any> = new EventEmitter<any>();

  @Output()
  updateEmitter: EventEmitter<ConfigurationInfo> = new EventEmitter();


  @Input()
  eventSchema: EventSchema;

  private frTxt: FreeTextStaticProperty;

  @Input()
  completedStaticProperty: ConfigurationInfo;

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

  isFreeTextStaticProperty(val) {
    return val instanceof FreeTextStaticProperty;
  }

  isFileStaticProperty(val) {
    return val instanceof FileStaticProperty;
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

  isMappingNaryProperty(val) {
    return val instanceof MappingPropertyNary;
  }

  isRuntimeResolvableOneOfStaticProperty(val) {
    return val instanceof RuntimeResolvableOneOfStaticProperty;
  }

  isRuntimeResolvableAnyStaticProperty(val) {
    return val instanceof RuntimeResolvableAnyStaticProperty;
  }

  valueChange(hasInput) {
    this.staticProperty.isValid = hasInput;
    this.validateEmitter.emit();
  }

  emitUpdate(configurationInfo: ConfigurationInfo) {
    this.updateEmitter.emit(configurationInfo);
  }
}
