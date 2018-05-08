import {Component, DoCheck, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EventProperty} from '../model/EventProperty';
// import {DragulaService} from 'ng2-dragula';
import {DragDropService} from '../drag-drop.service';
import {Subscription} from 'rxjs/Subscription';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataTypesService} from '../data-type.service';

@Component({
  selector: 'app-event-property-primitive',
  templateUrl: './event-property-primitive.component.html',
  styleUrls: ['./event-property-primitive.component.css']
})
export class EventPropertyPrimitiveComponent implements OnInit, DoCheck {

  @Input() property: EventPropertyPrimitive;

  constructor() {
  }

  ngOnInit() {
  }

  ngDoCheck() {
  }

}
