import {Component, DoCheck, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EventProperty} from '../properties/EventProperty';
import {DragulaService} from 'ng2-dragula';
import {DragDropService} from '../drag-drop.service';
import {Subscription} from 'rxjs/Subscription';
import {EventPropertyPrimitive} from '../properties/EventPropertyPrimitive';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataTypesService} from '../data-type.service';

@Component({
  selector: 'app-event-property-primitive',
  templateUrl: './event-property-primitive.component.html',
  styleUrls: ['./event-property-primitive.component.css']
})
export class EventPropertyPrimitiveComponent implements OnInit, DoCheck {

  @Input() property: EventPropertyPrimitive;
  @Input() index: number;


  private propertyPrimitivForm: FormGroup;
  private runtimeDataTypes;
  @Output() delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addPrimitive: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();
  @Output() addNested: EventEmitter<any> = new EventEmitter<any>();


  constructor(private dragulaService: DragulaService, private formBuilder: FormBuilder, private dataTypesService: DataTypesService) {
    this.propertyPrimitivForm = formBuilder.group({
      dataType: ['', Validators.required]
    });

    this.runtimeDataTypes = this.dataTypesService.getDataTypes();
  }

  protected open = false;
  subscription: Subscription;


  ngOnInit() {
    this.dragulaService.drag.subscribe((value: any) => this.drag());
    this.property.propertyNumber = this.index;
  }

  ngDoCheck() {
    this.property.propertyNumber = this.index;
  }

  // von Dragula-Service aufgerufen nach Drag dieses Elements
  private drag() {
    const dragDropService: DragDropService = DragDropService.getInstance();
    dragDropService.announceDrag(this.property);
    dragDropService.nestConfirmed$.subscribe(result => {
      this.property.parent = result;
    });
  }

  private OnClickDeleteProperty(): void {
    this.delete.emit(this.property);
  }

  private OnClickOpen(): void {
    this.open = !this.open;
  }

  private getLabel(): string {
    if (typeof this.property.getRuntimeName() !== 'undefined') {
      return this.property.getRuntimeName();
    } else {
      return 'Property ' + this.property.propertyID;
    }
  }
}
