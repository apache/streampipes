import { Component, DoCheck, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EventPropertyList } from '../model/EventPropertyList';
import { EventProperty } from '../model/EventProperty';
import { DataTypesService } from '../data-type.service';


@Component({
  selector: 'app-event-property-list',
  templateUrl: './event-property-list.component.html',
  styleUrls: ['./event-property-list.component.css']
})
export class EventPropertyListComponent implements OnInit {

  constructor(private dataTypeService: DataTypesService) { }


  @Input() property: EventPropertyList;
  @Input() index: number;

  private runtimeDataTypes;

  @Input() isEditable: boolean;

  @Output() delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();

  ngOnInit() {
    this.runtimeDataTypes = this.dataTypeService.getDataTypes();
  }
}
