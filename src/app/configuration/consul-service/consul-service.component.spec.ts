import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ConsulServiceComponent } from './consul-service.component';
import { MatButtonModule, MatGridListModule, MatInputModule, MatIconModule, MatTooltipModule, MatCheckboxModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {StreampipesPeContainerConifgs} from '../shared/streampipes-pe-container-configs';
import {StreampipesPeContainer} from '../shared/streampipes-pe-container.model';

describe('ConsulServiceComponent', () => {

    let fixture: ComponentFixture<TestComponentWrapper>;
    let consulServiceComponent: ConsulServiceComponent;

    beforeEach(async(() => {
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

    it('should create the component', async(() => {
        expect(consulServiceComponent).toBeTruthy();
    }));
    it('should set header to test1', async(() => {
        expect(fixture.nativeElement.querySelector('h4').innerText).toBe('test1');
    }));
    it('should set icon to passing', async(() => {
        expect(fixture.nativeElement.querySelector('mat-icon').classList).toContain('service-icon-passing');
    }));
    it('should not show form before toggled', async(() => {
        expect(fixture.nativeElement.querySelector('form')).toBeFalsy();
    }));
    it('should show form after toggled', async(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelector('form')).toBeTruthy();
    }));
    it('should set type of first input to number', async(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelectorAll('input')[0].type).toBe('number');
    }));
    it('should set type of second input to text', async(() => {
        consulServiceComponent.toggleConfiguration();
        fixture.detectChanges();
        expect(fixture.nativeElement.querySelectorAll('input')[1].type).toBe('text');
    }));
    it('should set type of third input to checkbox', async(() => {
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
            status: "passing"
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
