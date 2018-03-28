import {Component, DoCheck, EventEmitter, Injectable, Input, OnInit, Output} from '@angular/core';
import {EventProperty} from '../model/EventProperty';
import {EventPropertyNested} from '../model/EventPropertyNested';
import {UUID} from 'angular2-uuid';
import {EventPropertyList} from '../model/EventPropertyList';
// import {DragulaService} from 'ng2-dragula';
// import {DragDropService} from '../drag-drop.service';
// import {WriteJsonService} from '../write-json.service';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';

@Component({
  selector: 'app-event-property-nested',
  templateUrl: './event-property-nested.component.html',
  styleUrls: ['./event-property-nested.component.css']

})

@Injectable()
export class EventPropertyNestedComponent implements OnInit, DoCheck {

  // constructor(private dragulaService: DragulaService) {  }
    constructor() {  }

  open = false;

  @Input() eventPropertyNested: EventPropertyNested;
  @Input() index: number;

  @Output()delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();

  ngOnInit() {
    // this.addPrimitiveProperty();
    // const dragDropService: DragDropService = DragDropService.getInstance();
    // this.dragulaService.drag.subscribe((value: any) => { // wenn nested gedragt wird
    //   console.log('drag at nested');
    //   dragDropService.announceDrag(this.eventPropertyNested);
    // });
    //
    // this.dragulaService.drop.subscribe((value: any) => { // von Dragula-Service aufgerufen, wenn Element hier gedroppt wird, um dies zu bestätigen
    //   console.log('drop at nested' + this.eventPropertyNested.propertyID);
    //   dragDropService.announceDrop(this.eventPropertyNested);
    // });

    this.eventPropertyNested.propertyNumber = this.index;
  }

  ngDoCheck() {
    this.eventPropertyNested.propertyNumber = this.index;
  }

  private OnClickOpen(): void {
    this.open = !this.open;
  }

  private OnClickDeleteProperty(): void {
      this.delete.emit(this.eventPropertyNested);
  }

  public deleteProperty(property): void {
    // const writeJsonService: WriteJsonService = WriteJsonService.getInstance();
    // const dragDropService: DragDropService = DragDropService.getInstance();

    const toDelete: number = this.eventPropertyNested.eventProperties.indexOf(property);
    this.eventPropertyNested.eventProperties.splice(toDelete, 1);

    if (property.label !== undefined) {
      // const path: string = dragDropService.buildPath(property);
      // writeJsonService.remove(path);
    }
  }

  public addPrimitiveProperty(): void {
    console.log('called primitive');

    const uuid: string = UUID.UUID();
    const parent: EventProperty = this.eventPropertyNested;
    this.eventPropertyNested.eventProperties.push(new EventPropertyPrimitive(uuid, parent));
  }

  public addNestedProperty(): void {
    console.log('called nested');

    const uuid: string = UUID.UUID();
    const parent: EventProperty = this.eventPropertyNested;
    this.eventPropertyNested.eventProperties.push(new EventPropertyNested(uuid, parent));
  }

  private getLabel(): string {
    if (typeof this.eventPropertyNested.getRuntimeName() !== 'undefined') {
      return this.eventPropertyNested.getRuntimeName();
    } else {
        return 'Nested Property';
    }
  }

  private isEventPropertyPrimitive(instance): boolean {
    return instance instanceof EventPropertyPrimitive;
  }

  private isEventPropertyNested(instance): boolean {
    return instance instanceof EventPropertyNested;
  }

  private isEventPropertyList(instance): boolean {
    return instance instanceof EventPropertyList;
  }
}
