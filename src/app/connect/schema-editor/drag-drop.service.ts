import {Injectable} from '@angular/core';
import {WriteJsonService} from './write-json.service';
import {EventProperty} from './properties/EventProperty';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class DragDropService {

  private static instance: DragDropService = new DragDropService();

  constructor() {
    if (DragDropService.instance) {
      throw new Error('Error: Instantiation failed : Use getInstance() instead of new.');
    }
  }

  private dragged: EventProperty;
  private dropped: EventProperty;

  private nestConfirmedSource: Subject<EventProperty> = new Subject<EventProperty>();
  public nestConfirmed$ = this.nestConfirmedSource.asObservable();

  public static getInstance(): DragDropService {
    return DragDropService.instance;
  }

  announceDrag(property: EventProperty) {
    this.dragged = property;
    console.log(this.dragged.propertyID);
  }

  announceDrop(property: EventProperty) {
    const writeJsonService: WriteJsonService = WriteJsonService.getInstance();

    this.dropped = property;
    console.log('dropped ' + this.dropped.propertyID);

    const dragged_path: string = this.buildPath(this.dragged);
    let dropped_path: string = this.buildPath(this.dropped);


    if ((this.buildPath(this.dragged.parent) !== dropped_path) && !(this.dragged.propertyID === this.dropped.propertyID)) {
      if (dragged_path.length > dropped_path.length) {
        if (this.dropped.parent === undefined) {
          dropped_path = '';
        }
        // unnest property
        console.log('un_nest, path: ' + dragged_path + ' to ' + dropped_path + '/' + this.dragged.propertyID);
        writeJsonService.move(dragged_path, dropped_path + '/' + this.dragged.propertyID);

      } else {
        // nest property
        if (this.dragged.parent === undefined) {

        }
        console.log('nest, path: ' + this.buildPath(this.dragged) + ' to ' + this.buildPath(this.dropped) + '/' + this.dragged.propertyID);
        writeJsonService.move(dragged_path, dropped_path + '/' + this.dragged.propertyID);
      }
      this.nestConfirmedSource.next(this.dropped);
    }
  }

  // erstellt den Pointer für den JSON Patch
  public buildPath(property: EventProperty): string {
    let path = '';

    while (property !== undefined) {
      path = '/' + property.propertyID + path;
      property = property.parent;
    }
    return path;
  }
}
