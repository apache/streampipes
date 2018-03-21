import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';
import {EventPropertyNested} from '../../old-schema-editor/model/EventPropertyNested';
import {EventProperty} from '../../old-schema-editor/model/EventProperty';
import {EventPropertyList} from '../../old-schema-editor/model/EventPropertyList';
// import {DragulaService} from 'ng2-dragula';
import {UUID} from 'angular2-uuid';
import {WriteJsonService} from '../../old-schema-editor/write-json.service';
// import {DragDropService} from '../drag-drop.service';
import {EventPropertyNestedComponent} from '../../old-schema-editor/event-property-nested/event-property-nested.component';
import {RestService} from '../../rest.service';
import {EventPropertyPrimitive} from '../../old-schema-editor/model/EventPropertyPrimitive';
import {EventSchema} from '../../old-schema-editor/model/EventSchema';
import {AdapterDescription} from '../../model/AdapterDescription';
import {ProtocolDescription} from '../../model/ProtocolDescription';
import {FormatDescription} from '../../model/FormatDescription';
import {DragulaService} from 'ng2-dragula';


@Component({
    selector: 'app-event-property-bag',
    templateUrl: './event-property-bag.component.html',
    styleUrls: ['./event-property-bag.component.css']
})

export class EventPropertyBagComponent implements OnInit {

    @Input() eventProperties: EventProperty[];

    private dragularOptions: any = {
        removeOnSpill: false
    };

    constructor(private restService: RestService,
                private dragulaService: DragulaService) {
    }


    ngOnInit() {

    }
    public addPrimitiveProperty(): void {
        const uuid: string = UUID.UUID();
        const path = '/' + uuid;

        this.eventProperties.push(new EventPropertyPrimitive(uuid, undefined));
    }


    public addNestedProperty(): void {
        const uuid: string = UUID.UUID();
        const path = '/' + uuid;

        this.eventProperties.push(new EventPropertyNested(uuid, undefined));
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
