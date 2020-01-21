/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { TestBed, async, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import {TransformationRuleService} from './transformation-rule.service';
import {Logger} from '../shared/logger/default-log.service';
import {EventSchema} from './schema-editor/model/EventSchema';
import {EventPropertyPrimitive} from './schema-editor/model/EventPropertyPrimitive';
import {RenameRuleDescription} from './model/connect/rules/RenameRuleDescription';
import {EventProperty} from './schema-editor/model/EventProperty';
import {EventPropertyNested} from './schema-editor/model/EventPropertyNested';
import {AddNestedRuleDescription} from './model/connect/rules/AddNestedRuleDescription';
import {e, r} from '@angular/core/src/render3';
import {MoveRuleDescription} from './model/connect/rules/MoveRuleDesctiption';
import {DeleteRuleDescription} from './model/connect/rules/DeleteRuleDescription';

describe('TransformationRuleService', () => {

    let injector: TestBed;
    let service: TransformationRuleService;
    let logger: Logger;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule
            ],
            providers: [
                TransformationRuleService,
                Logger
            ]
        });
        injector = getTestBed();
        service = injector.get(TransformationRuleService);
    });

    it('Get complete key from schema nested', () => {

        const eventProperties: EventProperty[] = [];
        const eventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        eventPropertyPrimitive.setRuntimeName('a');
        const eventPropertyNested: EventPropertyNested = new EventPropertyNested('id_2', null);
        eventPropertyNested.setRuntimeName('b');
        eventPropertyNested.eventProperties.push(eventPropertyPrimitive);
        eventProperties.push(eventPropertyNested);

        const result: string = service.getCompleteRuntimeNameKey(eventProperties, 'http://eventProperty.de/id_1');

        expect(result).toBe('b.a');
    });

    it('Get complete key from schema primitve', () => {

        const eventProperties: EventProperty[] = [];
        const eventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        eventPropertyPrimitive.setRuntimeName('a');

        eventProperties.push(eventPropertyPrimitive);

        const result: string = service.getCompleteRuntimeNameKey(eventProperties, 'http://eventProperty.de/id_1');

        expect(result).toBe('a');
    });

    it('check get all ids with one id', () => {
        const eventProperties: EventProperty[] = [];
        const eventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);

        eventProperties.push(eventPropertyPrimitive);

        var result: string[] = service.getAllIds(eventProperties);
        expect(result.length).toBe(1);
        expect(result[0]).toBe('http://eventProperty.de/id_1');

    });

    it('check get all ids with multiple ids', () => {
        const eventProperties: EventProperty[] = [];
        const eventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        const eventPropertyPrimitive1: EventPropertyPrimitive = new EventPropertyPrimitive('id_2', null);
        const eventPropertyNesteted: EventPropertyNested = new EventPropertyNested('id_3', null);
        eventPropertyNesteted.eventProperties.push(eventPropertyPrimitive1);

        eventProperties.push(eventPropertyPrimitive);
        eventProperties.push(eventPropertyNesteted);

        var result: string[] = service.getAllIds(eventProperties);
        expect(result.length).toBe(3);
        expect(result[0]).toBe('http://eventProperty.de/id_1');
        expect(result[2]).toBe('http://eventProperty.de/id_2');
        expect(result[1]).toBe('http://eventProperty.de/id_3');

    });

    it('Create Nested Rules simple', () => {

        const oldEventSchema: EventSchema = new EventSchema();

        const newEventSchema: EventSchema = new EventSchema();
        const propertyNested: EventPropertyNested = new EventPropertyNested("id", null);
        propertyNested.runtimeName = 'a';
        newEventSchema.eventProperties.push(propertyNested);

        var result: AddNestedRuleDescription[] = service.getCreateNestedRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].runtimeKey).toBe('a');
    });

    it('Create Nested Rules nested', () => {

        const oldEventSchema: EventSchema = new EventSchema();

        const newEventSchema: EventSchema = new EventSchema();
        const nestedNested: EventPropertyNested = new EventPropertyNested("id_2", null);
        nestedNested.runtimeName = 'a';

        const nestedProperty: EventPropertyNested = new EventPropertyNested("id_1", null);
        nestedProperty.runtimeName = 'b';
        nestedNested.eventProperties.push(nestedProperty);
        newEventSchema.eventProperties.push(nestedNested);


        var result: AddNestedRuleDescription[] = service.getCreateNestedRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(2);
        expect(result[0].runtimeKey).toBe('a');
        expect(result[1].runtimeKey).toBe('a.b');
    });


    it('Create Move Rules simple', () => {

        const oldEventSchema: EventSchema = new EventSchema();
        const oldPropertyToMove: EventPropertyPrimitive = new EventPropertyPrimitive("id_1", null);
        oldPropertyToMove.runtimeName = "a";
        const oldNestedProperty: EventPropertyNested = new EventPropertyNested("id_2", null);
        oldNestedProperty.runtimeName = "b";
        oldEventSchema.eventProperties.push(oldPropertyToMove);
        oldEventSchema.eventProperties.push(oldNestedProperty);

        const newEventSchema: EventSchema = new EventSchema();
        const newPropertyToMove: EventPropertyPrimitive = new EventPropertyPrimitive("id_1", null);
        newPropertyToMove.runtimeName = "a";
        const newNestedProperty: EventPropertyNested = new EventPropertyNested("id_2", null);
        newNestedProperty.runtimeName = "b";
        newNestedProperty.eventProperties.push(newPropertyToMove);
        newEventSchema.eventProperties.push(newNestedProperty);

        var result: MoveRuleDescription[] = service.getMoveRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].oldRuntimeKey).toBe('a');
        expect(result[0].newRuntimeKey).toBe('b.a');

        result = service.getMoveRules(oldEventSchema.eventProperties,
            newEventSchema, oldEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].newRuntimeKey).toBe('a');
        expect(result[0].oldRuntimeKey).toBe('b.a');
    });

    it('Delete simple', () => {
        const oldEventSchema: EventSchema = new EventSchema();
        const eventProperty: EventPropertyPrimitive = new EventPropertyPrimitive("id", null);
        eventProperty.runtimeName = "a";
        oldEventSchema.eventProperties.push(eventProperty);

        const newEventSchema: EventSchema = new EventSchema();

        var result: DeleteRuleDescription[] = service.getDeleteRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].runtimeKey).toBe("a");
    });

    it('Delete nested', () => {
        const oldEventSchema: EventSchema = new EventSchema();
        const eventProperty: EventPropertyPrimitive = new EventPropertyPrimitive("id_2", null);
        eventProperty.runtimeName = "a";
        const eventPropertyNested: EventPropertyNested = new EventPropertyNested("id_1", null);
        eventPropertyNested.eventProperties.push(eventProperty);
        eventPropertyNested.runtimeName = "b";
        oldEventSchema.eventProperties.push(eventPropertyNested);

        var newEventSchema: EventSchema = new EventSchema();
        const newEventPropertyNested: EventPropertyNested = new EventPropertyNested("id_1", null);
        newEventPropertyNested.runtimeName = "b";
        newEventSchema.eventProperties.push(newEventPropertyNested);


        var result: DeleteRuleDescription[] = service.getDeleteRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].runtimeKey).toBe("b.a");

        newEventSchema = new EventSchema();
        result = service.getDeleteRules(newEventSchema.eventProperties,
            oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].runtimeKey).toBe("b");
    });

    it('Rename simple', () => {

        var oldEventSchema: EventSchema = new EventSchema();
        var oldEventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        oldEventPropertyPrimitive.runtimeName = 'a';
        oldEventSchema.eventProperties.push(oldEventPropertyPrimitive);

        var newEventSchema: EventSchema = new EventSchema();
        var newEventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        newEventPropertyPrimitive.runtimeName = 'b';
        newEventSchema.eventProperties.push(newEventPropertyPrimitive);


        var result: RenameRuleDescription[] = service.getRenameRules(newEventSchema.eventProperties, oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].oldRuntimeKey).toBe('a');
        expect(result[0].newRuntimeKey).toBe('b');

    });

     it('Rename nested', () => {

        var oldEventSchema: EventSchema = new EventSchema();
        var oldNestedEventProperty: EventPropertyNested = new EventPropertyNested('id_2', null);
        oldNestedEventProperty.runtimeName = 'b';
        var oldEventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        oldEventPropertyPrimitive.runtimeName = 'a';
        oldNestedEventProperty.eventProperties.push(oldEventPropertyPrimitive);
        oldEventSchema.eventProperties.push(oldNestedEventProperty);

        var newEventSchema: EventSchema = new EventSchema();
        var newNestedEventProperty: EventPropertyNested = new EventPropertyNested('id_2', null);
        newNestedEventProperty.runtimeName = 'b';
        var newEventPropertyPrimitive: EventPropertyPrimitive = new EventPropertyPrimitive('id_1', null);
        newEventPropertyPrimitive.runtimeName = 'b';
        newNestedEventProperty.eventProperties.push(newEventPropertyPrimitive);
        newEventSchema.eventProperties.push(newNestedEventProperty);


        var result: RenameRuleDescription[] = service.getRenameRules(newEventSchema.eventProperties, oldEventSchema, newEventSchema);

        expect(result.length).toBe(1);
        expect(result[0].oldRuntimeKey).toBe('b.a');
        expect(result[0].newRuntimeKey).toBe('b.b');

    });

});
