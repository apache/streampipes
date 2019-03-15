import { Component, Input, EventEmitter, OnInit, Output, OnChanges } from '@angular/core';
import { UUID } from 'angular2-uuid';
import { RestService } from '../../rest.service';
import { DragulaService } from 'ng2-dragula';
import { EventProperty } from '../model/EventProperty';
import { EventPropertyNested } from '../model/EventPropertyNested';
import { EventPropertyList } from '../model/EventPropertyList';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { DomainPropertyProbabilityList } from '../model/DomainPropertyProbabilityList';
import { DomainPropertyProbability } from '../model/DomainPropertyProbability';
import {DataTypesService} from '../data-type.service';


@Component({
    selector: 'app-event-property-bag',
    templateUrl: './event-property-bag.component.html',
    styleUrls: ['./event-property-bag.component.css']
})

export class EventPropertyBagComponent implements OnInit {

    @Input() eventProperties: EventProperty[];

    @Input() eventPropertyNested: boolean = false;

    @Input()
    isEditable: Boolean;

    @Output() eventPropertiesChange = new EventEmitter<EventProperty[]>();

    @Input() domainPropertyGuesses: DomainPropertyProbabilityList[];

    private dragularOptions: any = {
        removeOnSpill: false
    };

    constructor(private restService: RestService,
        private dragulaService: DragulaService,
                private dataTypesService: DataTypesService) {
    }

    ngOnInit() {
        if (this.domainPropertyGuesses == null) {
            this.domainPropertyGuesses = [];
        }
    }

    public getDomainProbability(name: string) {
        var result: DomainPropertyProbabilityList;

        for (let entry of this.domainPropertyGuesses) {
            if (entry.runtimeName == name) {
                result = entry;
            }
        }

        return result;
    }

    public addStaticValueProperty(): void {
        var eventProperty = new EventPropertyPrimitive('staticValue/' + UUID.UUID(), undefined);

        eventProperty.setRuntimeName("key_0");
        eventProperty.setRuntimeType(this.dataTypesService.getStringTypeUrl());

        this.eventProperties.push(eventProperty);
    }

    public addTimestampProperty(): void {

        var eventProperty = new EventPropertyPrimitive('timestamp/' + UUID.UUID(), undefined);

        eventProperty.setRuntimeName("timestamp");
        eventProperty.setLabel("Timestamp");
        eventProperty.setDomainProperty("http://schema.org/DateTime");
        eventProperty.setRuntimeType(this.dataTypesService.getNumberTypeUrl());


        this.eventProperties.push(eventProperty);
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

    public deletePropertyPrimitive(e) {

        var property: EventPropertyPrimitive = <EventPropertyPrimitive> e;

        var index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }

    }

    public deletePropertyNested(e) {

        var property: EventPropertyNested = <EventPropertyNested> e;

        var index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }

    }

    public deletePropertyList(e) {

        var property: EventPropertyList = <EventPropertyList> e;

        var index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }

    }




}
