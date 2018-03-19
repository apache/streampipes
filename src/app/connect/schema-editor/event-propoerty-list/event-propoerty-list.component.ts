import {Component, DoCheck, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EventPropertyList} from '../properties/EventPropertyList';
import {EventProperty} from '../properties/EventProperty';


@Component({
  selector: 'app-event-propoerty-list',
  templateUrl: './event-propoerty-list.component.html',
  styleUrls: ['./event-propoerty-list.component.css']
})
export class EventPropoertyListComponent implements OnInit, DoCheck {

  constructor() { }

  @Input() eventPropertyList: EventPropertyList;
  @Input() index: number;

  protected open = false;

  @Output() delete: EventEmitter<EventProperty> = new EventEmitter<EventProperty>();

  ngOnInit() {
    this.eventPropertyList.propertyNumber = this.index;
  }

  ngDoCheck() {
    this.eventPropertyList.propertyNumber = this.index;
  }

  private OnClickDeleteProperty(): void {
    this.delete.emit(this.eventPropertyList);
  }

  private OnClickOpen(): void {
    this.open = !this.open;
  }

  private getLabel(): string {
    if (typeof this.eventPropertyList.getRuntimeName() !== 'undefined') {
      return this.eventPropertyList.getRuntimeName();
    } else {
      return this.eventPropertyList.label;
    }
  }
}
