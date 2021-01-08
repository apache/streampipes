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

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {CommonModule} from '@angular/common';
import {FlexLayoutModule} from '@angular/flex-layout';
import {ConsulServiceComponent} from './consul-service.component';
import {
    MatButtonModule,
    MatCheckboxModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatTooltipModule
} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {Component} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {StreampipesPeContainer} from '../shared/streampipes-pe-container.model';

describe('ConsulServiceComponent', () => {

    let fixture: ComponentFixture<TestComponentWrapper>;
    let consulServiceComponent: ConsulServiceComponent;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [
                CommonModule,
                BrowserAnimationsModule,
                FlexLayoutModule,
                MatGridListModule,
                MatButtonModule,
                MatIconModule,
                MatInputModule,
                MatCheckboxModule,
                MatTooltipModule,
                FormsModule
            ],
            declarations: [
                TestComponentWrapper,
                ConsulServiceComponent
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(TestComponentWrapper);
        fixture.detectChanges();
        consulServiceComponent = fixture.debugElement.children[0].componentInstance;
    }));

    it('should create the component', waitForAsync(() => {
        expect(consulServiceComponent).toBeTruthy();
    }));
    it('should set header to test1', waitForAsync(() => {
        expect(fixture.nativeElement.querySelector('h4').innerText).toBe('test1');
    }));
    it('should set icon to passing', waitForAsync(() => {
        expect(fixture.nativeElement.querySelector('mat-icon').classList).toContain('service-icon-passing');
    }));
    it('should not show form before toggled', waitForAsync(() => {
        expect(fixture.nativeElement.querySelector('form')).toBeFalsy();
    }));
    it('should show form after toggled', waitForAsync(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelector('form')).toBeTruthy();
    }));
    it('should set type of first input to number', waitForAsync(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelectorAll('input')[0].type).toBe('number');
    }));
    it('should set type of second input to text', waitForAsync(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelectorAll('input')[1].type).toBe('text');
    }));
    it('should set type of third input to checkbox', waitForAsync(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelectorAll('input')[2].type).toBe('checkbox');
    }));
});

@Component({
    selector: 'test-component-wrapper',
    template: '<consul-service [consulService]="consulService"></consul-service>'
})
class TestComponentWrapper {
    
    consulService : [StreampipesPeContainer] = [
    {
        name: "TestName",
        mainKey: "id",
        meta: {
            status: "passing",
            tag: "primary"
        },
        configs:[{
            key: "TECH ACTIVE",
            description: "Source for nissatech data", 
            value: "false", 
            valueType: "xs:boolean",
            isPassword: false,

        }]

        
    }]
   
    // consulService = {
    //     mainKey: 'sp/test/1',
    //     meta: {
    //         status: 'passing'
    //     },
    //     name: 'test1',
    //     configs: [
            
            
    //         {
    //             key: 'testint',
    //             value: '80',
    //             valueType: 'xs:integer',
    //             isPassword: false,
    //             description: 'test int'
    //         },
    //         // {
    //         //     key: 'teststring',
    //         //     value: '765',
    //         //     valueType: 'xs:string',
    //         //     isPassword: false,
    //         //     description: 'test string'
    //         // },
    //         // {
    //         //     key: 'testbool',
    //         //     value: 'true',
    //         //     valueType: 'xs:boolean',
    //         //     isPassword: false,
    //         //     description: 'test boolean'
    //         // }



       // ]
    }
