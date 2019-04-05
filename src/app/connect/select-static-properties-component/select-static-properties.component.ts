import { Component, Input, Output, EventEmitter } from '@angular/core';

import { StaticProperty } from '../model/StaticProperty';
import { DataSetDescription } from '../model/DataSetDescription';
import {EventSchema} from '../schema-editor/model/EventSchema';
@Component({
  selector: 'app-select-static-properties',
  templateUrl: './select-static-properties.component.html',
  styleUrls: ['./select-static-properties.component.css'],
})
export class SelectStaticPropertiesComponent {

  @Input()
  staticProperties: StaticProperty[];
  @Input()
  eventSchema: EventSchema;
  @Output()
  validateEmitter = new EventEmitter();
  @Output()
  emitter: EventEmitter<any> = new EventEmitter<any>();

  validateText() {
    if (this.staticProperties.every(this.allValid)) {
      this.validateEmitter.emit(true);
    } else {
      this.validateEmitter.emit(false);
    }
  }

  allValid(staticProperty) {
    return staticProperty.isValid;
  }
}
