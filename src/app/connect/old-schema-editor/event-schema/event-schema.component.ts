import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';
import {EventPropertyNested} from '../model/EventPropertyNested';
import {EventProperty} from '../model/EventProperty';
import {EventPropertyList} from '../model/EventPropertyList';
// import {DragulaService} from 'ng2-dragula';
import {UUID} from 'angular2-uuid';
import {WriteJsonService} from '../write-json.service';
// import {DragDropService} from '../drag-drop.service';
import {EventPropertyNestedComponent} from '../event-property-nested/event-property-nested.component';
import {RestService} from '../../rest.service';
import {EventPropertyPrimitive} from '../model/EventPropertyPrimitive';
import {EventSchema} from '../model/EventSchema';
import {AdapterDescription} from '../../model/AdapterDescription';
import {ProtocolDescription} from '../../model/ProtocolDescription';
import {FormatDescription} from '../../model/FormatDescription';


@Component({
  selector: 'app-event-schema',
  templateUrl: './event-schema.component.html',
  styleUrls: ['./event-schema.component.css']
})

export class EventSchemaComponent implements OnInit {

  // @Input() selectedFormat: FormatDescription;

  private dragularOptions: any = {
        removeOnSpill: true
  };

  @Input() protocol: ProtocolDescription;
  @Input() format: FormatDescription;

  @Output() adapterChange = new EventEmitter<AdapterDescription>();

  public eventSchema: EventSchema = null;

  constructor(private restService: RestService,
              // private dragulaService: DragulaService) {
  ) {

  }

  ngOnInit() {
    // this.dragulaService.setOptions('schema-bag', {
    //   removeOnSpill: false
    // });

    this.eventSchema = new EventSchema();
    // this.restService.getGuessSchema().subscribe(x => {
    //
    //   this.eventSchema = x;
    //   console.log(x);
    // });
  }

  public guessSchema(): void {
    const adapter = new AdapterDescription('http://bb.de');
    adapter.protocol = this.protocol;
    adapter.format = this.format;

    this.restService.getGuessSchema(adapter).subscribe(x => {
      this.eventSchema = x;
      console.log(x);
    });
  }

  public deleteProperty(property): void {
    const writeJsonService: WriteJsonService = WriteJsonService.getInstance();
    // const dragDropService: DragDropService = DragDropService.getInstance();

    const toDelete: number = this.eventSchema.eventProperties.indexOf(property);
    this.eventSchema.eventProperties.splice(toDelete, 1);

    if (property.label !== undefined) {
      // const path: string = dragDropService.buildPath(property);
      // writeJsonService.remove(path);
    }
  }

  public addPrimitiveProperty(): void {
    const uuid: string = UUID.UUID();
    const path = '/' + uuid;

    this.eventSchema.eventProperties.push(new EventPropertyPrimitive(uuid, undefined));
  }

  public addNestedProperty(): void {
    const uuid: string = UUID.UUID();
    const path = '/' + uuid;

    this.eventSchema.eventProperties.push(new EventPropertyNested(uuid, undefined));
  }

  // TODO fertig implementieren --> entsprechene if else Bedingungen hinzufügen, wie bei typeOfProperty
  private addListProperty(): void {
    const writeJsonService: WriteJsonService = WriteJsonService.getInstance();
    writeJsonService.patcher();
    const uuid = UUID.UUID();
    // this.properties.push(new EventPropertyList(uuid));
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

  // TODO move code to backend
  // Code to detect schema from Json
  //   private jsonData = '{\n' +
  //   '     "foo": {\n' +
  //   '       "bar": "baz",\n' +
  //   '       "waldo": "fred",\n' +
  //   '       "nested": {\n' +
  //   '          "peter": "pan"\n' +
  //   '        }\n' +
  //   '     },\n' +
  //   '     "qux": {\n' +
  //   '       "corge": "grault"\n' +
  //   '     },\n' +
  //   '      "bla": "test_flat"\n' +
  //   '   }';
  //
  // eingelesenes JSON, aus dem die UI generiert wird
  // private data = JSON.parse(this.jsonData);
  //
  // private createUI(data): void {
  //
  //   for (let index in data) {
  //     if (this.recursion >= 1) { // call here in nested-property
  //       if (typeof data[index] === 'object') {
  //         this.nestedComponent.addNestedProperty();
  //         this.recursion++;
  //         this.createUI(data[index]);
  //         this.recursion--;
  //       } else if (typeof data[index] === 'string') {
  //         this.nestedComponent.addPrimitiveProperty();
  //       }
  //     } else { // call here in Event-Schema
  //       if (typeof data[index] === 'object') {
  //         this.addNestedProperty();
  //         this.recursion++;
  //         this.createUI(data[index]);
  //         this.recursion--;
  //       } else if (typeof data[index] === 'string') {
  //         this.addPrimitiveProperty();
  //       }
  //     }
  //   }
  // }

}
