import { Component, Input, EventEmitter, OnInit, Output } from '@angular/core';
import { UUID } from 'angular2-uuid';
import { EventProperty } from '../model/EventProperty';
import { EventPropertyNested } from '../model/EventPropertyNested';
import { EventPropertyList } from '../model/EventPropertyList';
import { EventPropertyPrimitive } from '../model/EventPropertyPrimitive';
import { DomainPropertyProbabilityList } from '../model/DomainPropertyProbabilityList';
import { DataTypesService } from '../data-type.service';


@Component({
    selector: 'app-event-property-bag',
    templateUrl: './event-property-bag.component.html',
    styleUrls: ['./event-property-bag.component.css']
})

export class EventPropertyBagComponent implements OnInit {

    @Input() eventProperties: EventProperty[];

    @Input() eventPropertyNested = false;

    @Input() isEditable: boolean;

    @Output() eventPropertiesChange = new EventEmitter<EventProperty[]>();

    @Input() domainPropertyGuesses: DomainPropertyProbabilityList[];

    constructor(private dataTypesService: DataTypesService) { }

    ngOnInit() {
        if (this.domainPropertyGuesses == null) {
            this.domainPropertyGuesses = [];
        }
    }

    public getDomainProbability(name: string) {
        let result: DomainPropertyProbabilityList;

        for (const entry of this.domainPropertyGuesses) {
            if (entry.runtimeName === name) {
                result = entry;
            }
        }

        return result;
    }

    public addStaticValueProperty(): void {
        const eventProperty = new EventPropertyPrimitive('staticValue/' + UUID.UUID(), undefined);

        eventProperty.setRuntimeName('key_0');
        eventProperty.setRuntimeType(this.dataTypesService.getStringTypeUrl());

        this.eventProperties.push(eventProperty);
    }

    public addTimestampProperty(): void {
        const eventProperty = new EventPropertyPrimitive('timestamp/' + UUID.UUID(), undefined);

        eventProperty.setRuntimeName('timestamp');
        eventProperty.setLabel('Timestamp');
        eventProperty.setDomainProperty('http://schema.org/DateTime');
        eventProperty.setRuntimeType(this.dataTypesService.getNumberTypeUrl());

        this.eventProperties.push(eventProperty);
    }


    public addNestedProperty(): void {
        const uuid: string = UUID.UUID();
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
        const property: EventPropertyPrimitive = e as EventPropertyPrimitive;
        const index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }
    }

    public deletePropertyNested(e) {
        const property: EventPropertyNested = e as EventPropertyNested;
        const index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }
    }

    public deletePropertyList(e) {
        const property: EventPropertyList = e as EventPropertyList;
        const index = this.eventProperties.indexOf(property, 0);
        if (index > -1) {
            this.eventProperties.splice(index, 1);
        }
    }
}