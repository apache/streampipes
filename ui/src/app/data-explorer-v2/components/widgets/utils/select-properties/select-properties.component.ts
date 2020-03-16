import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EventProperty } from '../../../../../connect/schema-editor/model/EventProperty';

@Component({
  selector: 'sp-select-properties',
  templateUrl: './select-properties.component.html',
  styleUrls: ['./select-properties.component.css']
})
export class SelectPropertiesComponent implements OnInit {

  @Output()
  changeSelectedProperties: EventEmitter<EventProperty[]> = new EventEmitter();

  @Input()
  availableProperties: EventProperty[];

  @Input()
  selectedProperties: EventProperty[];

  constructor() { }

  ngOnInit(): void {
  }

  triggerSelectedProperties(properties: EventProperty[]) {
    this.changeSelectedProperties.emit(properties);
  }

}
